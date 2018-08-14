package com.techsignage.techsignmeetings.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.MeetingAttendees;
import com.techsignage.techsignmeetings.Models.MeetingModel;
import com.techsignage.techsignmeetings.Models.UserMeetingModel;
import com.techsignage.techsignmeetings.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by heat on 3/12/2017.
 */

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.ListViewHolder> {
    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_meetingdate;
        TextView tv_meetingtitle;
        TextView tv_meetingorganizer;

        public ListViewHolder(View itemView) {
            super(itemView);

            tv_meetingdate = (TextView) itemView.findViewById(R.id.tv_meetingdate);
            tv_meetingtitle = (TextView) itemView.findViewById(R.id.tv_meetingtitle);
            tv_meetingorganizer = (TextView) itemView.findViewById(R.id.tv_meetingorganizer);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            UserMeetingModel userMeetingModel = dataList.get(getAdapterPosition());
//            if(userMeetingModel.meeting.ACTUAL_START_DATETIME != null)
//            {
//                Intent intent = new Intent(context, MeetingAttendees.class);
//                intent.putExtra("MEETING_ID", userMeetingModel.meeting.MEETING_ID);
//                intent.putExtra("Status", userMeetingModel.meeting.ACTUAL_START_DATETIME != null);
//                context.startActivity(intent);
//            }
//            else
//            {
//                Toast.makeText(context, "Meeting must be started", Toast.LENGTH_LONG).show();
//            }

            Intent intent = new Intent(context, MeetingAttendees.class);
            intent.putExtra("MEETING_ID", userMeetingModel.meeting.MEETING_ID);
            intent.putExtra("UNIT_NAME", userMeetingModel.unit.UNIT_NAME);
            intent.putExtra("UNIT_ID", userMeetingModel.unit.UNIT_ID);
            intent.putExtra("activityName", ((CoreActivity) context).getLocalClassName());
            //intent.putExtra("Status", userMeetingModel.meeting.ACTUAL_START_DATETIME != null);
            intent.putExtra("Status", userMeetingModel.meeting.CanCheckin);
            intent.putExtra("StatusOut", userMeetingModel.meeting.CanCheckOut);
            context.startActivity(intent);

//            Intent intent = new Intent(context, MeetingAttendees.class);
//            intent.putExtra("MEETING_ID", userMeetingModel.meeting.MEETING_ID);
//            context.startActivity(intent);
        }
    }

    int layout;
    Context context;
    List<UserMeetingModel> dataList = new ArrayList<>();
    LayoutInflater inflater;
    //Listener listener;
    //Context con;

    public MeetingsAdapter(Context context, int layout) {
        this.layout = layout;
        this.context = context;
        //this.con= context;
        inflater = LayoutInflater.from(context);
    }

    public void setLst(List<UserMeetingModel> lst) {
        this.dataList = lst;
    }

    public List<UserMeetingModel> getLst() {
        return this.dataList;
    }

    @Override
    public MeetingsAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View convertView = inflater.inflate(layout, parent, false);
        return new MeetingsAdapter.ListViewHolder(convertView);
    }

    int itemHeight;

    @Override
    public void onBindViewHolder(final MeetingsAdapter.ListViewHolder holder, final int position) {
        try {
            Date startdate = Globals.format.parse(dataList.get(position).meeting.START_DATETIME);
            Date enddate = Globals.format.parse(dataList.get(position).meeting.END_DATETIME);
            if (Globals.lang.equals("ar")) {
                String MeetingDate = String.format("%s - %s", Globals.format1_ar.format(startdate), Globals.format1_ar.format(enddate));
                holder.tv_meetingdate.setText(MeetingDate);
            } else {
                String MeetingDate = String.format("%s - %s", Globals.format1.format(startdate), Globals.format1.format(enddate));
                holder.tv_meetingdate.setText(MeetingDate);
            }

//            holder.tv_meetingdate.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, MeetingAttendees.class);
//                    intent.putExtra("MEETING_ID", dataList.get(position).meeting.MEETING_ID);
//                    context.startActivity(intent);
//                }
//            });
            holder.tv_meetingtitle.setText(dataList.get(position).meeting.MEETING_TITLE);
            holder.tv_meetingorganizer.setText(dataList.get(position).meeting.MEETING_DESCRIPTION);
            Utilities.setFadeAnimation(holder.itemView);
            holder.itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            itemHeight = holder.itemView.getMeasuredHeight();
        } catch (Exception ex) {
        }

    }

    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}