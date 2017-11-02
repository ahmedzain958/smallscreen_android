package com.techsignage.techsignmeetings.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techsignage.techsignmeetings.BookActivity;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.ATTENDEEModel;
import com.techsignage.techsignmeetings.Models.ServiceResponses.AttendeeResponse;
import com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface;
import com.techsignage.techsignmeetings.R;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by heat on 3/12/2017.
 */

public class AttendeesAdapter extends RecyclerView.Adapter<AttendeesAdapter.ListViewHolder>
{
    class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tv_attendeedate;
        TextView tv_attendeedate2;
        LinearLayout lincontainer_1;
        LinearLayout lincontainer_2;
        CheckBox chk_attend1;
        CheckBox chk_attend2;
        Button btn_swapcheck;
        Button btn_swapcheck2;

        public ListViewHolder(View itemView) {
            super(itemView);

            tv_attendeedate = (TextView) itemView.findViewById(R.id.tv_attendeedate);
            tv_attendeedate2 = (TextView) itemView.findViewById(R.id.tv_attendeedate2);
            chk_attend1 = (CheckBox) itemView.findViewById(R.id.chk_attend1);
            chk_attend2 = (CheckBox) itemView.findViewById(R.id.chk_attend2);
            btn_swapcheck = (Button) itemView.findViewById(R.id.btn_swapcheck);
            btn_swapcheck2 = (Button) itemView.findViewById(R.id.btn_swapcheck2);
            lincontainer_1 = (LinearLayout) itemView.findViewById(R.id.lincontainer_1);
            lincontainer_2 = (LinearLayout) itemView.findViewById(R.id.lincontainer_2);
        }
    }

    Context context;
    List<ATTENDEEModel> dataList = new ArrayList<>();
    LayoutInflater inflater;
    retrofitInterface retrofitInterface;
    private Subscription subscription;
    //Listener listener;
    //Context con;
    Boolean CanCheckIn = false;
    Boolean CanCheckOut = false;
    Boolean showButtons = false;
    ProgressDialog dialog;

    public AttendeesAdapter(Context context, Boolean Status, Boolean StatusOut, Boolean showButtons) {
        this.CanCheckIn = Status;
        this.showButtons = showButtons;
        this.CanCheckOut = StatusOut;
        this.context = context;
        //this.con= context;
        inflater = LayoutInflater.from(context);
        final String token = Utilities.getSharedValue("token", context);
        retrofitInterface = Utilities.liveAPI(token);
    }

    public void setLst(List<ATTENDEEModel> lst)
    {
        this.dataList = lst;
    }

    public List<ATTENDEEModel> getLst()
    {
        return this.dataList;
    }

    @Override
    public AttendeesAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View convertView = inflater.inflate(R.layout.meeting_attendee, parent, false);
        return new AttendeesAdapter.ListViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final AttendeesAdapter.ListViewHolder holder, final int position) {

        int chk = position % 2;
        if(chk == 0) {
            if (!showButtons)
                holder.btn_swapcheck.setVisibility(View.INVISIBLE);
            holder.tv_attendeedate.setText(String.format("%s %s", dataList.get(position).ATTENDTEE_FIRST_NAME,
                    dataList.get(position).ATTENDTEE_LAST_NAME));
            if (dataList.get(position).CHECK_IN != null)
            {
                holder.chk_attend1.setChecked(true);
                holder.btn_swapcheck.setText(context.getResources().getString(R.string.checkout));
            }
            if (dataList.get(position).CHECK_OUT != null) {
                holder.btn_swapcheck.setEnabled(false);
            }

            holder.btn_swapcheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataList.get(position).CHECK_IN == null)
                    {
                        if(!CanCheckIn)
                        {
                            Toast.makeText(context, context.getResources().getString(R.string.notstarted), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    else {
                        if(!CanCheckOut)
                        {
                            Toast.makeText(context, context.getResources().getString(R.string.notcheckin), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    ATTENDEEModel attendeeModel = new ATTENDEEModel();
                    attendeeModel.ATTENDTEE_ID = dataList.get(position).ATTENDTEE_ID;
                    attendeeModel.MEETING_ID = dataList.get(position).MEETING_ID;
                    attendeeModel.CheckIn = true;
                    if (dataList.get(position).CHECK_IN != null)
                        attendeeModel.CheckIn = false;
                    Observable<AttendeeResponse> data = retrofitInterface.checkin(attendeeModel);
                    subscription = data.subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<AttendeeResponse>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();

                                }

                                @Override
                                public void onNext(AttendeeResponse attendeeResponse) {
                                    if(attendeeResponse.attendeeModel.CHECK_OUT != null)
                                    {
                                        dataList.get(position).CHECK_OUT = attendeeResponse.attendeeModel.CHECK_OUT;
                                        holder.btn_swapcheck.setEnabled(false);
                                    }
                                    else if(attendeeResponse.attendeeModel.CHECK_IN != null)
                                    {
                                        dataList.get(position).CHECK_IN = attendeeResponse.attendeeModel.CHECK_IN;
                                        holder.btn_swapcheck.setText(context.getResources().getString(R.string.checkout));
                                    }
                                    holder.btn_swapcheck.setText(context.getResources().getString(R.string.checkout));
                                    holder.chk_attend1.setChecked(true);
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }

                                    ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);
                                }
                            });
                }
            });

            if (dataList.size() > (position + 1)) {
                if (!showButtons)
                    holder.btn_swapcheck2.setVisibility(View.INVISIBLE);
                holder.tv_attendeedate2.setText(String.format("%s %s", dataList.get(position + 1).ATTENDTEE_FIRST_NAME,
                        dataList.get(position + 1).ATTENDTEE_LAST_NAME));
                if (dataList.get(position+1).CHECK_IN != null)
                {
                    holder.btn_swapcheck2.setText(context.getResources().getString(R.string.checkout));
                    holder.chk_attend2.setChecked(true);
                }
                if (dataList.get(position+1).CHECK_OUT != null) {
                    holder.btn_swapcheck2.setEnabled(false);
                }
                holder.btn_swapcheck2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dataList.get(position+1).CHECK_IN == null)
                        {
                            if(!CanCheckIn)
                            {
                                Toast.makeText(context, context.getResources().getString(R.string.notstarted), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        else {
                            if(!CanCheckOut)
                            {
                                Toast.makeText(context, context.getResources().getString(R.string.notcheckin), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        dialog = Utilities.showDialog(context);

                        ATTENDEEModel attendeeModel = new ATTENDEEModel();
                        attendeeModel.ATTENDTEE_ID = dataList.get(position+1).ATTENDTEE_ID;
                        attendeeModel.MEETING_ID = dataList.get(position+1).MEETING_ID;
                        attendeeModel.CheckIn = true;
                        if (dataList.get(position+1).CHECK_IN != null)
                            attendeeModel.CheckIn = false;

                        Observable<AttendeeResponse> data = retrofitInterface.checkin(attendeeModel);
                        subscription = data.subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<AttendeeResponse>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onNext(AttendeeResponse attendeeResponse) {
                                        if(attendeeResponse.attendeeModel.CHECK_OUT != null)
                                        {
                                            dataList.get(position+1).CHECK_OUT = attendeeResponse.attendeeModel.CHECK_OUT;
                                            holder.btn_swapcheck2.setEnabled(false);
                                        }
                                        else if(attendeeResponse.attendeeModel.CHECK_IN != null)
                                        {
                                            dataList.get(position+1).CHECK_IN = attendeeResponse.attendeeModel.CHECK_IN;
                                            holder.btn_swapcheck2.setText(context.getResources().getString(R.string.checkout));
                                        }
                                        holder.chk_attend2.setChecked(true);
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);
                                    }
                                });
                    }
                });
            }
            else
            {
                holder.lincontainer_2.setVisibility(View.INVISIBLE);
            }

            Utilities.setFadeAnimation(holder.itemView);



        }
        else
        {
            holder.lincontainer_1.setVisibility(View.GONE);
            holder.lincontainer_2.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

}