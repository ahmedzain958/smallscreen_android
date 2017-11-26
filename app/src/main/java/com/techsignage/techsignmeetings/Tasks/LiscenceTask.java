package com.techsignage.techsignmeetings.Tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.LicenseNewActivity;
import com.techsignage.techsignmeetings.MainActivity;
import com.techsignage.techsignmeetings.Network.HttpRequestWrapper;
import com.techsignage.techsignmeetings.R;

import org.w3c.dom.Document;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by heat on 9/24/2017.
 */

public class LiscenceTask extends AsyncTask<String, Void, String>
{
    Activity activity;
    public LiscenceTask(Activity activity)
    {
        this.activity = activity;
    }

    //private ProgressDialog dialog = new ProgressDialog(activity, R.style.StyledDialog);

    @Override
    protected void onPreExecute() {
        //final Resources resources = activity.getResources();
        //this.dialog.setMessage(resources.getString(R.string.processing));
        //this.dialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String resgetToken = "";
        try
        {
            HttpRequestWrapper req = new HttpRequestWrapper(activity,
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
//        if (dialog.isShowing()) {
//            dialog.dismiss();
//        }
        try {
            Document doc = Utilities.LoadDocument(xmlResult);
            if(Utilities.vaidateLicense(doc, activity))
            {
                Utilities.setSharedValue("licensed", "true", activity);

//                Intent intent = new Intent(activity, MainActivity.class);
//                activity.startActivity(intent);
//                activity.finish();

                //getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);
            }
            else {
                Intent intent = new Intent(activity, LicenseNewActivity.class);
                activity.startActivity(intent);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        super.onPostExecute(xmlResult);
    }
}