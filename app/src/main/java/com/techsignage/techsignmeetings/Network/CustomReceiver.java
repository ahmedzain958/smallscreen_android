package com.techsignage.techsignmeetings.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.techsignage.techsignmeetings.BookActivity;

/**
 * Created by heat on 7/17/2017.
 */

public class CustomReceiver extends BroadcastReceiver {

    public Boolean currentStatus = false;
    public IConnector connector;

    public void setConnector(IConnector connector)
    {
        this.connector = connector;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}

