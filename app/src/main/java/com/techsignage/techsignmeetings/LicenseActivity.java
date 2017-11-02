package com.techsignage.techsignmeetings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.License.QlmLicense;
import com.techsignage.techsignmeetings.License.QlmResult;
import com.techsignage.techsignmeetings.Network.HttpRequestWrapper;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Constants;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.LinkedHashMap;
import java.util.Map;

public class LicenseActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences prefs;
    EditTextPreference edtLicense;
    static AssetManager mngr;
    ProgressDialog progDialog;
    static final String ALGORITHM = "RSA";
    transient RSAPublicKeySpec rsaPubKey = null;

    String activationKey;
    QlmLicense qlmLicense;
    Context context;
    private static final String PREFS="com.vendor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_license);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        validateLicense();

        context = this;
        mngr = getAssets();
        addPreferencesFromResource(R.xml.prefs);
        qlmLicense = QlmLicense.getInstance(context, prefs);

        Preference akeyPref = findPreference("activationkey");
        edtLicense = (EditTextPreference) akeyPref;

        String akey = "";
        if (qlmLicense != null)
        {
            akey = qlmLicense.getActivationKey();
            edtLicense.setSummary(akey);
            edtLicense.setText(akey);
        }

        edtLicense.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                activationKey = (String)newValue.toString();
                CheckforConnectivityAndprocess();

                return false;
            }
        });
    }

//    public String getDeviceId()
//    {
//        return Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//    }

    private void ProcessActivationkey()
    {
        try {
            progDialog = ProgressDialog.show(this, "Activating your license", "Please wait....",true);
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        if (qlmLicense == null)
                        {
                            qlmLicense = QlmLicense.getInstance(context, prefs);
                        }

                        qlmLicense.ActivateLicense(prefs.getString("ServiceURL",""),
                                activationKey, Utilities.getDeviceId(getApplicationContext()),
                                qlmLicense.getComputerKey());

                    }
                    catch (Exception e)
                    {

                    }

                    Message msg = new Message ();
                    handler.sendMessage(msg);

                    progDialog.dismiss();

                }

            }.start();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            QlmResult qlmResult = qlmLicense.getResult();
            if (qlmResult.valid == true)
            {
                edtLicense.setSummary(activationKey);
                edtLicense.setText(activationKey);
            }
            else if (qlmResult.result == null || qlmResult.result.equals(""))
            {
                qlmResult.result = "Invalid activation key. Please type again..";
            }

            Toast.makeText(getApplicationContext (), qlmResult.result, Toast.LENGTH_LONG).show();

        }
    };

    private void CheckforConnectivityAndprocess()
    {
        if ((qlmLicense != null) && qlmLicense.CheckConnections())
        {
            ProcessActivationkey();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "License activation requires an internet connection. Please check that your device can connect to the internet.", Toast.LENGTH_LONG).show();

        }
    }

    private void validateLicense() {
        // Read the Service URL from Manifest XML and keep in global Utilities class
        SharedPreferences prefs;
        ActivityInfo ai;
        try {
            ai = this.getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("ServiceURL", (String)ai.metaData.get("ServiceURL"));
            edit.commit();

        }
        catch (Exception ex) {
            finish();
        }


    }

    public void onSharedPreferenceChanged(SharedPreferences preference, String arg1) {
        // TODO Auto-generated method stub
    }
}
