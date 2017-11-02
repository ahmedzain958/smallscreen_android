package com.techsignage.techsignmeetings.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by heat on 4/16/2017.
 */

public class UserMeetingModel {
    @SerializedName("user")
    public UserModel user;

    @SerializedName("meeting")
    public MeetingModel meeting;

    @SerializedName("room")
    public UnitModel unit;
}
