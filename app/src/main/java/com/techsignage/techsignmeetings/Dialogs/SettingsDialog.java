package com.techsignage.techsignmeetings.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.techsignage.techsignmeetings.Adapters.RoomsAdapter;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.MainActivity;
import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heat on 8/2/2017.
 */

public class SettingsDialog extends DialogFragment {

    Spinner rooms_spinner;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.settings_dialog, null);
        rooms_spinner = (Spinner)v.findViewById(R.id.rooms_spinner);
        final EditText terminalName = (EditText)v.findViewById(R.id.terminalName_txt);
        TextView tv_uId = (TextView)v.findViewById(R.id.tv_uId);
        tv_uId.setText(String.format("%s - %s", getActivity().getResources().getString(R.string.deviceidentifier), Utilities.getDeviceId(getActivity())));
        Button settings_btn = (Button)v.findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (terminalName.getText().toString().isEmpty())
                {
                    terminalName.setError("Provide Terminal Name");
                    return;
                }
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setMessage("hello");
        builder.setCancelable(false);
//        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (terminalName.getText().toString().isEmpty())
//                {
//                    terminalName.setError("Enter Value");
//                }
//            }
//        });

        builder.setTitle("Configuration Settings").setView(v);
        //this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(this.getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        return builder.create();
    }

    public void setSpinner(List<UnitModel> lst)
    {
        RoomsAdapter roomsAdapter = new RoomsAdapter(getActivity(), R.layout.spinner_item, lst);
        rooms_spinner.setAdapter(roomsAdapter);
    }
}
