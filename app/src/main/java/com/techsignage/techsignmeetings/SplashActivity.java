package com.techsignage.techsignmeetings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Helpers.AppLocal;
import com.techsignage.techsignmeetings.Helpers.Globals;

public class SplashActivity extends CoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //AppLocal.setAppLocal(getApplicationContext(), AppLocal.PREF_LOCAL_ARABIC);

        Globals.setAppLocal(this, "ar");

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
