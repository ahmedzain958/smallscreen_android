package com.techsignage.techsignmeetings.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by heat on 4/11/2017.
 */

public class MeetingsModel {
    @SerializedName("Room")
    public UnitModel Room;

    @SerializedName("Meetings")
    public List<UserMeetingModel> Meetings;

    @SerializedName("MeetingsAll")
    public List<UserMeetingModel> MeetingsAll;
}
