package com.techsignage.techsignmeetings.Helpers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Dialogs.NotAuthorizedDialog;
import com.techsignage.techsignmeetings.License.QlmLicense;
import com.techsignage.techsignmeetings.LicenseNewActivity;
import com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface;
import com.techsignage.techsignmeetings.R;

import org.json.JSONObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by heat on 3/12/2017.
 */

public class Utilities {
    private final static int FADE_DURATION = 1000;

    public static String getLanguage(Context context){
        return context.getResources().getConfiguration().locale.toString();
    }

    @SuppressWarnings("deprecation")
    public static void setSystemLocaleLegacy(Configuration config, Locale locale){
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void setSystemLocale(Configuration config, Locale locale){
        config.setLocale(locale);
    }

    @SuppressWarnings("deprecation")
    public static void setLanguage(Context context, String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale);
        }else{
            setSystemLocaleLegacy(config, locale);
        }
        context.getApplicationContext().getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    public static void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public static void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public static retrofitInterface liveAPI(final String token)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                                "Bearer " + token);

                        Request newRequest = builder.build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.coreUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(retrofitInterface.class);
    }

    public static ProgressDialog showDialog(CoreActivity coreActivity)
    {
        ProgressDialog dialog = new ProgressDialog(coreActivity, R.style.StyledDialog);
        dialog.setMessage(coreActivity.getResources().getString(R.string.processing));
        dialog.show();
        return dialog;
    }

    public static ProgressDialog showDialog(Context context)
    {
        ProgressDialog dialog = new ProgressDialog(context, R.style.StyledDialog);
        dialog.setMessage(context.getResources().getString(R.string.processing));
        dialog.show();
        return dialog;
    }

    public static void RedFlashLight(CoreActivity activity)
    {
        NotificationManager nm = ( NotificationManager)activity.getSystemService(NOTIFICATION_SERVICE);
        Notification notif = new Notification();
        notif.ledARGB = 0xFFff00e4;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 100;
        notif.ledOffMS = 100;
        nm.notify(1, notif);
    }

    public static String getSharedValue(String key, Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    public static void setSharedValue(String key, String value, Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getDeviceId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void getFileContent(DataOutputStream dos, final String name, final File file, String contentType)
    {
        try
        {
            String contentDisposition = "Content-Disposition: form-data; name=\""+name+"\"; filename=\"" + file.getName() + "\"";
            dos.writeBytes(Globals.SPACER + Globals.BOUNDARY + Globals.NEW_LINE);
            dos.writeBytes(contentDisposition
                    + Globals.NEW_LINE);
            dos.writeBytes(contentType + Globals.NEW_LINE);
            dos.writeBytes(Globals.NEW_LINE);

            FileInputStream fis = new FileInputStream(file);
            int bytesRead;
            //byte[] b2 = new byte[(int)fis.getChannel().size()];
            //while ((bytesRead = fis.read(b2)) != -1) {
            //    dos.write(b2, 0, bytesRead);
            //}
            byte[] buffer = new byte[Globals.MAX_BUFFER_SIZE];
            while ((bytesRead = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
            dos.writeBytes(Globals.NEW_LINE);

        }
        catch (Exception ex)
        {
            Log.v("err", ex.getMessage());
        }
    }

    public static String formedStream(Map<String,Object> params)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (sb.length() != 0) sb.append('&');
                sb.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
        }
        catch(Exception ex)
        {

        }
        return sb.toString();
    }

    public static boolean checkConfigurationFile(AppCompatActivity coreActivity) {
        //File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = new File(dir, Globals.filename + ".txt");
        if (!file.exists())
        {
            Log.v("file", "non existing");
            NotAuthorizedDialog dialog = new NotAuthorizedDialog();
            dialog.setCancelable(false);
            dialog.show(coreActivity.getSupportFragmentManager(), "NotAuth_Dialog");
            return true;
        }
        else
        {
            try
            {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    //text.append('\n');
                }
                bufferedReader.close();
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                Globals.unitId = jsonObject.get("UNIT_ID").toString();
                Globals.coreUrl = jsonObject.get("IP").toString();
                Globals.tokenUrl = String.format("%s/token", jsonObject.get("IP").toString());
            }
            catch (Exception ex)
            {

            }
        }
        return false;
    }

    public static Document LoadDocument(String xmlFragment)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder db = null;
        InputSource inStream = new InputSource();
        try {
            db = factory.newDocumentBuilder();
            //InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(xmlFragment));

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Document doc = null;
        try {
            doc = db.parse(inStream);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return doc;
    }

    public static Boolean Open_PKey_Values(Context context) throws QlmLicense.QlmPrivateKeyException
    {
        Boolean ret = false;

        try
        {
            InputStream istr = context.getAssets().open("QlmPublicKey.xml");
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

                }
                catch (IOException e)
                {

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
                                Globals.modulusKey = getCharacterDataFromElement(line).toString();

                                name = element.getElementsByTagName("Exponent");
                                if (name != null)
                                {
                                    line = (Element) name.item(0);
                                    Globals.exponentKey = getCharacterDataFromElement(line).toString();

                                    ret = true;
                                }
                            }
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }
            }
            catch (ParserConfigurationException e)
            {
            }

        } catch (IOException e)
        {
        }

        return ret;
    }

    private static String readTextFile(InputStream inputStream) {
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


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static Boolean vaidateLicense(Document doc, Context context) {
        NodeList nodes;
        nodes = doc.getElementsByTagName("status");
        int val = Integer.valueOf(nodes.item(0).getChildNodes().item(0).getNodeValue());
        if (val == 2 || val == 4)
        {
            Toast.makeText(context, "Valid license", Toast.LENGTH_LONG).show();
            return true;
        }
        else
        {
            Toast.makeText(context, "Not a valid license", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static SweetAlertDialog showProgressPrettyDialog(Context context, String message) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(message);
        pDialog.setCancelable(false);
        return pDialog;
    }

}
