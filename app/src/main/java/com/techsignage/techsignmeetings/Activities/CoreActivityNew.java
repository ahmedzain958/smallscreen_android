package com.techsignage.techsignmeetings.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.techsignage.techsignmeetings.Helpers.AppLocal;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Network.CustomReceiver;
import com.techsignage.techsignmeetings.R;

/**
 * Created by heat on 11/29/2017.
 */

public class CoreActivityNew extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawableResource(R.drawable.mainpage_bg);

        Utilities.getPermissions(this);
        AppLocal.checkConfigurationFile(this);
        Globals.setAppLocal(this, Globals.lang);

//        Intent intent = new Intent("com.android.action.hide_navigationbar");
//        sendBroadcast(intent);

        networkStateReceiver = new CustomReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {

                    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = manager.getActiveNetworkInfo();
                    if (ni != null) {
                        currentStatus = true;
                        //Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        currentStatus = false;
                        //Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
                    }

                    if (connector != null)
                        connector.getConnectionStatus(currentStatus);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    //LogExceptionRequest.log(this, exception);
                }
            }
        };
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //if (hasFocus) {
        //    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //}
    }

    View decorView;
//    final int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE
//            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            |View.SYSTEM_UI_FLAG_FULLSCREEN
//            |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

    @Override
    protected void onResume() {
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        super.onResume();

//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(flags);
//
//        decorView.setOnSystemUiVisibilityChangeListener
//                (new View.OnSystemUiVisibilityChangeListener() {
//                    @Override
//                    public void onSystemUiVisibilityChange(int visibility) {
//                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                            // system bars are visible
//                            // hide them again, maybe with some delay?
//                            //decorView.setSystemUiVisibility(flags);
//                            //executeDelayed();
//                        }
//
//                    }
//                });

//        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        final View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(uiOptions);

        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

//    private void executeDelayed() {
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // execute after 500ms
//                //Toast.makeText(getApplicationContext(), "sasaasas", Toast.LENGTH_SHORT).show();
//                //hideNavBar();
//            }
//        }, 1000);
//    }


//    private void hideNavBar() {
//        if (Build.VERSION.SDK_INT >= 18) {
//            View v = getWindow().getDecorView();
//            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }


//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    public void hideNavBars() {
//        decorView.setSystemUiVisibility(flags);
//    }

    public CustomReceiver networkStateReceiver;

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(networkStateReceiver);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        super.onPause();
    }
}
