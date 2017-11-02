package com.techsignage.techsignmeetings.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by heat on 8/10/2017.
 */

public class NotAuthorizedDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Configuration");
        builder.setMessage("Configuration data is missing");
        builder.setCancelable(false);
        return builder.create();
        //return super.onCreateDialog(savedInstanceState);
    }
}
