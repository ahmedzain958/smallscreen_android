package com.techsignage.techsignmeetings.Helpers;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;

import com.techsignage.techsignmeetings.Models.HourModel;
import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.Models.UserModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by mohamed on 3/22/2017.
 */

public class Globals {
    //    //public static String coreImageUrl = "http://php1.mgendy.com/public/assets/uploads/";
    public static String coreUrl = "http://197.45.191.5:711/";
    public static String tokenUrl = "http://197.45.191.5:711/token";
    //public static String coreUrl = "http://192.168.8.200:711/";
    //public static String tokenUrl = "http://192.168.8.200:711/token";
    public static int pageSize = 0;
    public static int skipCount = 0;
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    public static SimpleDateFormat format1 = new SimpleDateFormat("h:mm a");
    public static SimpleDateFormat format3 = new SimpleDateFormat("dd/MM/yyyy");
    public static String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss aaa").format(new Date());
    //public static String timeStamp2 = new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date());
    public static SimpleDateFormat format4 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss aaa");
    public static List<HourModel> hours = new ArrayList<>();
    public static UnitModel loggedUnit = null;
    public static UserModel loggedUser = null;
    public static String filename = "tech_configfile";
    public static String unitId = "D268F20D-34FB-424D-B2E6-6DB878B2A8F4";
    public static String lang = "n";

    public static final int flags2 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

    public static final String SPACER = "--";
    public static final String BOUNDARY = "---AmplitudeBoundaryL_o___";
    public static final String NEW_LINE = "\r\n";
    public static final int MAX_BUFFER_SIZE = 4096;
    public static final String plainOcStream = "Content-Type: application/octet-stream";
    public static String modulusKey = "";
    public static String exponentKey = "";
    public static String licenseUrl = "http://technomounts.com/QLMWS/QlmService.asmx/ValidateLicenseHttp";

    private static final String PREF_APP_LOCAL = "PREF_APP_LOCAL";
    private static final String KEY_LOCAL = "KEY_LOCAL";
    private static String mAppLocal;
    public static final String PREF_LOCAL_ENGLISH = "en";
    public static final String PREF_LOCAL_ARABIC = "ar";

    public static void setAppLocal(Context context, String appLocal) {
        Locale locale;

        mAppLocal = appLocal;
        setAppLocalToPref(context, appLocal);

        if (appLocal.equals(PREF_LOCAL_ARABIC)) {
            locale = new Locale(PREF_LOCAL_ARABIC);
        } else {
            locale = new Locale(PREF_LOCAL_ENGLISH);
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
}
