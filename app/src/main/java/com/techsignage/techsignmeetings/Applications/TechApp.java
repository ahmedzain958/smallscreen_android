package com.techsignage.techsignmeetings.Applications;

import android.app.Application;

import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.Injection.AppModule;
import com.techsignage.techsignmeetings.Models.Injection.DaggerNetComponent;
import com.techsignage.techsignmeetings.Models.Injection.NetComponent;
import com.techsignage.techsignmeetings.Models.Injection.NetModule;

/**
 * Created by heat on 9/6/2017.
 */

public class TechApp extends Application {

    private NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .netModule(new NetModule(Utilities.getSharedValue("token", this)))
                .build();
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }
}