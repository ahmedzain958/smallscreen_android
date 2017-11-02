package com.techsignage.techsignmeetings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Network.HttpRequestWrapper;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Map;
import ru.kolotnev.formattedittext.MaskedEditText;

public class LicenseNewActivity extends CoreActivity {
    MaskedEditText license_Txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_new);

        license_Txt = (MaskedEditText)findViewById(R.id.license_Txt);
        Button btn_license = (Button)findViewById(R.id.btn_license);
        btn_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (license_Txt.getText(true).toString().isEmpty())
                {
                    license_Txt.setError("Enter your license");
                    return;
                }
                new LiscenceTask().execute(license_Txt.getText(true).toString());
                Utilities.setSharedValue("licensekey", license_Txt.getText(true).toString(), LicenseNewActivity.this);
            }
        });

    }

    class LiscenceTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog dialog = new ProgressDialog(LicenseNewActivity.this, R.style.StyledDialog);

        @Override
        protected void onPreExecute() {
            final Resources resources = getResources();
            this.dialog.setMessage(resources.getString(R.string.processing));
            this.dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String resgetToken = "";
            try
            {
                HttpRequestWrapper req = new HttpRequestWrapper(LicenseNewActivity.this,
                        Globals.licenseUrl, "application/x-www-form-urlencoded; charset=UTF-8", "", 1);
                Map<String,Object> lst = new LinkedHashMap<>();
                //lst.put("is_avkey", "AQGN0Y0Y00R9KQGX8VH65K1KA8");
                lst.put("is_avkey", params[0]);
                //lst.put("is_pcid", Utilities.getDeviceId(LicenseNewActivity.this));
                //lst.put("is_computer_name", getDeviceName());
                lst.put("is_pcid", "AndroidRMS_mobileApp");
                lst.put("is_computer_name", "");
                lst.put("is_qlmversion", "5.0.00");
                String streamstr = Utilities.formedStream(lst);
                req.setStreamstr(streamstr);
                resgetToken = req.processRequestString();

            }
            catch(Exception ex)
            {

            }

            return resgetToken;
        }

        @Override
        protected void onPostExecute(String xmlResult) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Document doc = Utilities.LoadDocument(xmlResult);
            try {
                if(Utilities.vaidateLicense(doc, LicenseNewActivity.this))
                {
                    Utilities.setSharedValue("licensed", "true", LicenseNewActivity.this);

                    Intent intent = new Intent(LicenseNewActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    //getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }

            super.onPostExecute(xmlResult);
        }
    }

//    private Boolean vaidateLicense(Document doc) {
//        NodeList nodes;
//        nodes = doc.getElementsByTagName("status");
//        int val = Integer.valueOf(nodes.item(0).getChildNodes().item(0).getNodeValue());
//        if (val == 2 || val == 4)
//        {
//            Toast.makeText(LicenseNewActivity.this, "Valid license", Toast.LENGTH_LONG).show();
//            return true;
//        }
//        else
//        {
//            Toast.makeText(LicenseNewActivity.this, "Not a valid license", Toast.LENGTH_LONG).show();
//            return false;
//        }
//    }
}
