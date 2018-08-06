package com.techsignage.techsignmeetings.Applications;

import android.app.Application;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;

import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.Injection.AppModule;
import com.techsignage.techsignmeetings.Models.Injection.DaggerNetComponent;
import com.techsignage.techsignmeetings.Models.Injection.NetComponent;
import com.techsignage.techsignmeetings.Models.Injection.NetModule;

import java.lang.reflect.Field;

/**
 * Created by heat on 9/6/2017.
 */

public class TechApp extends Application {

    public NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        overrideFont("SERIF", "fonts/Roboto-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

        Intent intent = new Intent("com.android.action.hide_navigationbar");
        sendBroadcast(intent);
        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .netModule(new NetModule(Utilities.getSharedValue("token", this)))
                .build();
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }

    public NetComponent setNetComponent() {
        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .netModule(new NetModule(Utilities.getSharedValue("token", this)))
                .build();

        return mNetComponent;
    }

    public void overrideFont(String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(getApplicationContext().getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
        }
    }
}