package com.techsignage.techsignmeetings.Helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Applications.TechApp;
import com.techsignage.techsignmeetings.Dialogs.NotAuthorizedDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

/**
 * Created by AhmedNTS on 4/22/2017.
 */
public class AppLocal {
    private static final String PREF_APP_LOCAL = "PREF_APP_LOCAL";
    private static final String KEY_LOCAL = "KEY_LOCAL";
    public static final String PREF_LOCAL_ENGLISH = "en";
    public static final String PREF_LOCAL_ARABIC = "ar";

    private static String mAppLocal;

    public static String getAppLocal(Context context) {
        if (mAppLocal == null)
            mAppLocal = getAppLocalFromPref(context);

        return mAppLocal;
    }

    public static void setAppLocal(Context context, String appLocal) {
        Locale locale;

        mAppLocal = appLocal;
        setAppLocalToPref(context, appLocal);

        if (appLocal.equals(AppLocal.PREF_LOCAL_ARABIC)) {
            locale = new Locale(AppLocal.PREF_LOCAL_ARABIC);
        } else {
            locale = new Locale(AppLocal.PREF_LOCAL_ENGLISH);
        }

        Locale.setDefault(locale);

        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            config.setLocale(locale);
        else
            config.locale = locale;

        context.getApplicationContext().getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
//        context.createConfigurationContext(config);
    }

    private static void setAppLocalToPref(Context context, String appLocal) {
        context.getApplicationContext()
                .getSharedPreferences(PREF_APP_LOCAL, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LOCAL, appLocal)
                .apply();
    }

    private static String getAppLocalFromPref(Context context) {
        String local = context.getApplicationContext()
                .getSharedPreferences(PREF_APP_LOCAL, Context.MODE_PRIVATE)
                .getString(KEY_LOCAL, PREF_LOCAL_ENGLISH);
        if (local.equals(PREF_LOCAL_ARABIC))
            return AppLocal.PREF_LOCAL_ARABIC;
        else
            return AppLocal.PREF_LOCAL_ENGLISH;
    }

    public static boolean checkConfigurationFile(CoreActivity activity) {
        //File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = new File(dir, Globals.filename + ".txt");
        if (!file.exists())
        {
            Log.v("file", "non existing");
            NotAuthorizedDialog dialog = new NotAuthorizedDialog();
            dialog.setCancelable(false);
            dialog.show(activity.getSupportFragmentManager(), "NotAuth_Dialog");
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
                Globals.lang = jsonObject.get("Lang").toString();
            }
            catch (Exception ex)
            {

            }
        }
        return false;
    }

}
