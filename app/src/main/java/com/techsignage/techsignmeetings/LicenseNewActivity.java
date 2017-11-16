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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Applications.TechApp;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomsResponse;
import com.techsignage.techsignmeetings.Network.ContentTypes;
import com.techsignage.techsignmeetings.Network.HttpRequestWrapper;
import com.techsignage.techsignmeetings.Network.VolleyCallbackString;
import com.techsignage.techsignmeetings.Network.VolleyRequest;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ru.kolotnev.formattedittext.MaskedEditText;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

                    VolleyRequest request = new VolleyRequest();
                    request.getString(new VolleyCallbackString() {
                                          @Override
                                          public void onSuccess(String result) {
                                              try {
                                                  if (result != null) {
                                                      JSONObject object = new JSONObject(result);
                                                      final String token = object.getString("access_token");
                                                      Utilities.setSharedValue("token", token, getApplicationContext());
                                                      Utilities.setSharedValue("username", "Admin", LicenseNewActivity.this);
                                                      String activityName = getIntent().getExtras().getString("activityName");
                                                      if (activityName.equals("AllListActivity"))
                                                      {
                                                          Intent intent = new Intent(LicenseNewActivity.this, AllListActivity.class);
                                                          startActivity(intent);
                                                          finish();
                                                      }
                                                      else
                                                      {
                                                          Intent intent = new Intent(LicenseNewActivity.this, MainActivity.class);
                                                          startActivity(intent);
                                                          finish();
                                                      }
                                                  }
                                              } catch (Exception ex) {
                                              }
                                          }

                                          @Override
                                          public void onError(String result) {

                                          }
                                      }, LicenseNewActivity.this, getApplicationContext(), Globals.tokenUrl, "",
                            String.format("grant_type=password&username=%s&password=%s", "Admin", "P@ssw0rd"), ContentTypes.FormEncoded.toString());


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
