package com.techsignage.techsignmeetings.License;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import com.techsignage.techsignmeetings.Helpers.Utilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by heat on 7/24/2017.
 */


@SuppressLint("SimpleDateFormat")
public class QlmLicense {
    static final String ALGORITHM = "RSA";
    static final String RSA_ALGORITHM = "RSA/None/OAEPWithSHA1AndMGF1Padding";
    static final String CHAR_ENCODING = "UTF-8";
    static Context _appContext;
    static SharedPreferences _prefs;

    private static QlmLicense instance;

    transient String modulusKey;
    transient String exponentKey;
    //transient RSAPrivateKeySpec rsaPrivKey = null;
    transient RSAPublicKeySpec rsaPubKey = null;

    transient String result;
    transient String features;
    transient int duration;
    transient long remainingDays = -1;
    transient Date expiryDate;
    transient int status;

    transient QlmResult qlmResult = new QlmResult();

    private QlmCustomer customer;
    private QlmProduct product;

    private String activationKey;
    private String computerKey;
    private String computerID;

    public QlmLicense(Context context, SharedPreferences preference)
    {
        QlmLicense._appContext = context;
        QlmLicense._prefs = preference;
    }

    public static synchronized QlmLicense getInstance(Context context, SharedPreferences preference)
    {
        if (instance == null)
        {
            _appContext = context;
            _prefs = preference;
            instance = new QlmLicense(context, preference);
        }
        return instance;
    }

    public enum ELicenseStatus {
        EKeyPermanent(2), EKeyDemo(4), EKeyInvalid(8), EKeyProductInvalid(16), EKeyVersionInvalid(32),
        EKeyExpired(64), EKeyTampered(128), EKeyMachineInvalid(256), EKeyNeedsActivation(512),
        EKeyExceededAllowedInstances(1024);

        public int keyValue;

        ELicenseStatus(int value) {
            this.keyValue = value;
        }

    }

    static public QlmLicense LoadLicense (Context context, SharedPreferences preference,
                                          String licenseXml, String computerID)
            throws QlmPrivateKeyException
    {
        QlmLicense _license = getInstance(context, preference);

        if (_license.verifySignature(licenseXml))
        {
            _license.ProcessResponseforActivation(licenseXml, computerID);
        }
        else
        {
            _license.setStatus(ELicenseStatus.EKeyInvalid.keyValue);
            _license.qlmResult.result = "Failed to validate the signature of the license file.";
        }

        return _license;
    }

    //
    // Public methods
    //

    // Activate a license
    public Boolean ActivateLicense(String _url, String _activationKey, String _deviceId, String _computerKey)
            throws ClientProtocolException, IOException, QlmPrivateKeyException
    {
        HttpResponse response = GetServiceResponse(_url, _activationKey, _deviceId, _computerKey);

        HttpEntity r_entity = response.getEntity();

        String xmlResponse = EntityUtils.toString(r_entity);

        activationKey = _activationKey;

        return ProcessResponseforActivation(xmlResponse, _deviceId);
    }

    // Get the response from the web service
    private HttpResponse GetServiceResponse(final String _url, final String _licenseKey, final String _deviceId, final String _computerKey)
            throws ClientProtocolException, IOException
    {
        HttpResponse serviceResponse = null;

        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(_url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("is_avkey", _licenseKey));
        if (isNullOrEmpty(_computerKey) == false)
        {
            nvps.add(new BasicNameValuePair("is_pckey", _computerKey));
        }

        nvps.add(new BasicNameValuePair("is_pcid", _deviceId));


        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        serviceResponse = httpClient.execute(httpPost, localContext);

        return serviceResponse;
    }

    // Parse the response from the web service
    public Boolean ProcessResponseforActivation(String _xmlResponse, String computerID) throws QlmPrivateKeyException
    {

        qlmResult.valid = false;

        if (verifySignature(_xmlResponse))
        {
        }
        else
        {
            throw new QlmPrivateKeyException ("Failed to validate signature.");
        }

        Document doc = Utilities.LoadDocument(_xmlResponse);

        if (doc == null)
        {
            throw new QlmPrivateKeyException ("Failed to load xml response.");
        }

        NodeList nl  = doc.getElementsByTagName("QuickLicenseManager");;

        for (int i = 0; i < nl.getLength(); i++)
        {
            Element element = (Element) nl.item(i);
            try
            {
                int _status = getXmlIntAttribute (element, "status");
                setStatus(_status);

                String _expiryDate = getXmlStringAttribute (element, "expiryDate");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Date convertedDate = new Date();
                try
                {
                    convertedDate = dateFormat.parse(_expiryDate);
                    setExpiryDate(convertedDate);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

                String _activationResult = getXmlStringAttribute(element, "error");
                setActivationResult(_activationResult);

                // first check whether the key is a valid one or not
                if (IsKeyValid(_status) == false)
                {
                    qlmResult.result = _activationResult.toString();
                }
                else
                {
                    setFeatures(getXmlStringAttribute (element, "features"));
                    setDuration(getXmlIntAttribute (element, "duration"));
                    setActivationKey (getXmlStringAttribute (element, "activationKey"));
                    setComputerKey(getXmlStringAttribute (element, "pckey"));
                    setComputerID(getXmlStringAttribute (element, "computerID"));

                    QlmCustomer qlmCustomer = new QlmCustomer();
                    qlmCustomer.setUserCompany(getXmlStringAttribute (element, "userCompany"));
                    qlmCustomer.setUserFullName(getXmlStringAttribute (element, "userFullName"));
                    qlmCustomer.setUserEmail(getXmlStringAttribute (element, "userEmail"));
                    setCustomer(qlmCustomer);


                    QlmProduct qlmProduct = new QlmProduct();
                    qlmProduct.setProductID(getXmlIntAttribute (element, "productID"));
                    qlmProduct.setProductName(getXmlStringAttribute (element, "productName"));
                    qlmProduct.setMajorVersion(getXmlIntAttribute (element, "majorVersion"));
                    qlmProduct.setMinorVersion(getXmlIntAttribute (element, "minorVersion"));
                    setProduct(qlmProduct);

                    if (IsTrue (_status, (int) ELicenseStatus.EKeyPermanent.keyValue))
                    {
                        qlmResult.result = "Your license was successfully activated.";
                        qlmResult.valid = true;
                    }
                    else
                    {
                        Boolean licenseValid = IsLicenseValid(computerID);

                        if (licenseValid == true)
                        {
                            qlmResult.result = "Your license expires in " + remainingDays + " days.";
                            qlmResult.valid = true;
                        }
                        else
                        {
                            qlmResult.valid = false;
                            qlmResult.result = "Your license key expired.";
                        }
                    }

                    if (qlmResult.valid == true)
                    {
                        SharedPreferences.Editor edit = _prefs.edit();
                        edit.putString("QlmLicense", _xmlResponse);
                        edit.commit();
                    }
                }

            }
            catch (QlmPrivateKeyException ex)
            {
                throw ex;
            }
            catch (Exception ex)
            {
                qlmResult.result = "Error processing activation result: " + ex.getMessage();

            }
        }

        return qlmResult.valid;

    }

    boolean verifySignature(String xmlFragment) throws QlmPrivateKeyException
    {
        boolean valid = false;

        try
        {

            if (rsaPubKey == null)
            {
                Open_PKey_Values();

                byte[] modulusBytes = Base64.decode(modulusKey, 0);
                byte[] exponentBytes = Base64.decode(exponentKey, 0);

                BigInteger modulus = new BigInteger(1, modulusBytes);
                BigInteger exponent = new BigInteger(1, exponentBytes);

                rsaPubKey = new RSAPublicKeySpec(modulus, exponent);
            }


            KeyFactory fact = KeyFactory.getInstance(ALGORITHM);
            PublicKey pk = fact.generatePublic(rsaPubKey);


            // parse the XML
            //Document doc = LoadDocument (xmlFragment);
            Document doc = Utilities.LoadDocument(xmlFragment);
            // verify signature
            NodeList nodes = null;
            try
            {
                nodes = doc.getElementsByTagNameNS(Constants.SignatureSpecNS, "Signature");
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            if (nodes.getLength() == 0)
            {
                throw new QlmPrivateKeyException("Signature NOT found!");
            }

            Element sigElement = (Element) nodes.item(0);
            XMLSignature signature = new XMLSignature(sigElement, "");

            if (pk == null)
            {
                throw new QlmPrivateKeyException("Did not find Public Key");
            }

            valid = signature.checkSignatureValue(pk);

        }
        catch (Exception e)
        {
            throw new QlmPrivateKeyException(e.getMessage());
        }

        return valid;
    }

    // Checks if a license is valid
    public Boolean IsLicenseValid(String _computerID) throws IllegalBlockSizeException, UnsupportedEncodingException,
            InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException,
            BadPaddingException, QlmPrivateKeyException, ParseException, java.text.ParseException
    {
        Boolean ret = false;

        int _status = getStatus();
        Date _expiryDate = getExpiryDate();

        if (IsKeyValid(_status) == false)
        {
            // the key is invalid
            ret = false;
        }
        else if (_computerID.equalsIgnoreCase(computerID) == false)
        {
            ret = false;
        }
        else if (IsTrue(_status, (int) ELicenseStatus.EKeyDemo.keyValue))
        {
            // Check the expiry date, if not expired, return true, else return
            // false

            Date today = Calendar.getInstance().getTime();
            remainingDays = Days.daysBetween(new DateTime(today), new DateTime(_expiryDate)).getDays();

            long diff = (long) _expiryDate.getTime() - (long) today.getTime();

            if (diff > 0)
            {
                long diffInHours = diff / (1000 * 60 * 60);
                remainingDays = diffInHours / 24;
                long extraHours = diffInHours % 24;
                if (extraHours >= 1)
                {
                    // If there is one or more hour left in today, add today
                    remainingDays += 1;
                }
            }

            if (remainingDays <= 0)
            {
                ret = false;
            }
            else
            {
                ret = true;
            }

        }
        else if (IsTrue(_status, (int) ELicenseStatus.EKeyPermanent.keyValue))
        {
            // the key is OK
            ret = true;
        }

        return ret;
    }

    public Boolean CheckConnections()
    {
        if (!CheckLocalNetConnectivity())
        {
            return false;
        }

        return true;
    }

    //
    // Public Accessor methods
    //

    public QlmResult getResult() {
        return qlmResult;
    }

    public void setActivationResult(String _result) {
        this.result = _result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int _status) {
        this.status = _status;
    }

    public String getComputerKey() {
        return computerKey;
    }

    public void setComputerKey(String _computerKey) {
        this.computerKey = _computerKey;
    }

    public void setComputerID(String _computerID) {
        this.computerID = _computerID;
    }

    public void setActivationKey(String _activationKey) {
        this.activationKey = _activationKey;
    }

    public String getActivationKey() {
        return this.activationKey;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String _features) {
        this.features = _features;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int _duration) {
        this.duration = _duration;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(java.util.Date convertedDate) {
        this.expiryDate = convertedDate;
    }

    public QlmCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(QlmCustomer _customer) {
        this.customer = _customer;
    }

    public QlmProduct getProduct() {
        return product;
    }

    public void setProduct(QlmProduct _product) {
        this.product = _product;
    }

    public long getRemainingDays() {
        return remainingDays;
    }


    //
    // Private methods
    //

    // Check if a key is valid
    private Boolean IsKeyValid(int _status)
    {
        Boolean ret = false;

        if (IsTrue(_status, (int) ELicenseStatus.EKeyInvalid.keyValue)
                || IsTrue(_status, (int) ELicenseStatus.EKeyProductInvalid.keyValue)
                || IsTrue(_status,(int) ELicenseStatus.EKeyVersionInvalid.keyValue)
                || IsTrue(_status,(int) ELicenseStatus.EKeyMachineInvalid.keyValue)
                || IsTrue(_status, (int) ELicenseStatus.EKeyTampered.keyValue))
        {
            // the key is invalid
            ret = false;
        }
        else
        {
            ret = true;
        }

        return ret;
    }

    // check net connections
    private Boolean CheckLocalNetConnectivity()
    {
        if (this.isOnline())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @SuppressWarnings("unused")
    private Boolean CheckRemoteserver()
    {
        if (this.isRemoteServerAvailable(_prefs.getString("ServiceURL", "")))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) _appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        try
        {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting())
            {
                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    private boolean isRemoteServerAvailable(String urlServer)
    {
        try
        {
            URL url = new URL(urlServer);
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "Android Application: 1");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1000 * 30);
            // mTimeout is in seconds
            urlc.connect();
            if (urlc.getResponseCode() == 200)
            {
                return true;
            }
        }
        catch (MalformedURLException e1)
        {
            e1.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

//    private Document LoadDocument (String xmlFragment)
//    {
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
//        DocumentBuilder db = null;
//
//        try {
//            db = factory.newDocumentBuilder();
//        } catch (ParserConfigurationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        InputSource inStream = new InputSource();
//        inStream.setCharacterStream(new StringReader(xmlFragment));
//        Document doc = null;
//
//        try {
//            doc = db.parse(inStream);
//        } catch (SAXException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return doc;
//    }

    private Boolean Open_PKey_Values() throws QlmPrivateKeyException
    {
        Boolean ret = false;

        try
        {
            InputStream istr = _appContext.getAssets().open("QlmPublicKey.xml");
            String keys_string = readTextFile(istr);
            Log.i("XML", keys_string.toString());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;

            try
            {
                db = factory.newDocumentBuilder();

                InputSource inStream = new InputSource();
                inStream.setCharacterStream(new StringReader(keys_string));
                Document doc = null;

                try
                {
                    doc = db.parse(inStream);
                }
                catch (SAXException e)
                {
                    throw new QlmPrivateKeyException ("Failed to load the private key: " + e.getMessage());
                }
                catch (IOException e)
                {
                    throw new QlmPrivateKeyException ("Failed to load the private key: " + e.getMessage());
                }

                NodeList nl = doc.getElementsByTagName("RSAKeyValue");
                if (nl != null)
                {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element element = (Element) nl.item(i);
                        try {
                            NodeList name = element.getElementsByTagName("Modulus");
                            if (name != null)
                            {
                                Element line = (Element) name.item(0);
                                modulusKey = getCharacterDataFromElement(line).toString();

                                name = element.getElementsByTagName("Exponent");
                                if (name != null)
                                {
                                    line = (Element) name.item(0);
                                    exponentKey = getCharacterDataFromElement(line).toString();

                                    ret = true;
                                }
                                else
                                {
                                    throw new QlmPrivateKeyException ("Failed to load the Exponent tag.");
                                }
                            }
                            else
                            {
                                throw new QlmPrivateKeyException ("Failed to load the Modulus tag.");
                            }

                        }
                        catch (Exception e)
                        {
                            throw new QlmPrivateKeyException ("Failed to load the private key: " + e.getMessage());
                        }
                    }
                }
                else
                {
                    throw new QlmPrivateKeyException ("Failed to load the PrivateKey tag.");
                }
            }
            catch (ParserConfigurationException e)
            {
                throw new QlmPrivateKeyException ("Failed to load the private key: " + e.getMessage());
            }

        } catch (IOException e)
        {
            throw new QlmPrivateKeyException ("Failed to load the private key: " + e.getMessage());
        }

        return ret;
    }

    private String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

    private Boolean IsTrue(int nVal1, int nVal2) {
        if (((nVal1 & nVal2) == nVal1) || ((nVal1 & nVal2) == nVal2)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    private String getDateTime()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    @SuppressWarnings("unused")
    private Date getDatefromString(String dtString) throws ParseException, java.text.ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        Date convertedDate = new Date();

        convertedDate = dateFormat.parse(dtString);

        return convertedDate;
    }

    private static String getCharacterDataFromElement(Element e) {
        try {
            Node child = e.getFirstChild();
            if (child instanceof CharacterData) {
                CharacterData cd = (CharacterData) child;
                return cd.getData();
            }
            return "";
        } catch (Exception ex) {
            return "";
        }
    }

    private String getXmlStringAttribute (Element element, String attribute)
    {
        NodeList nodeList = element.getElementsByTagName(attribute);
        Element line = (Element) nodeList.item(0);
        String _strval = getCharacterDataFromElement(line).toString();
        //String _val = resp_value.equals("") ? "" : resp_value;

        return _strval;
    }

    private int getXmlIntAttribute (Element element, String attribute)
    {
        String _strval = getXmlStringAttribute (element, attribute);

        int _status = isNullOrEmpty(_strval) ? 0 : Integer.parseInt(_strval);

        return _status;
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    // This is important!
    static {
        org.apache.xml.security.Init.init();
    }

    public class QlmPrivateKeyException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public QlmPrivateKeyException(String message){
            super(message);
        }

    }

}
