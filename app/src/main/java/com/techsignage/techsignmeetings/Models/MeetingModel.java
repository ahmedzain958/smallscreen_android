package com.techsignage.techsignmeetings.Models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.Streams;

import java.util.List;

/**
 * Created by heat on 3/10/2017.
 */

public class MeetingModel {
    @SerializedName("MEETING_ID")
    public String MEETING_ID;

    @SerializedName("UNIT_ID")
    public String UNIT_ID;

    @SerializedName("MEETING_TYPE_ID")
    public String MEETING_TYPE_ID;

    @SerializedName("MEETING_TITLE")
    public String MEETING_TITLE;

    @SerializedName("START_DATETIME")
    public String START_DATETIME;

    @SerializedName("END_DATETIME")
    public String END_DATETIME;

    @SerializedName("ACTUAL_START_DATETIME")
    public String ACTUAL_START_DATETIME;

    @SerializedName("ACTUAL_END_DATETIME")
    public String ACTUAL_END_DATETIME;

    @SerializedName("IsStarting")
    public Integer IsStarting;

    @SerializedName("TSRM_MEETING_ATTENDEES")
    public List<ATTENDEEModel> ATTENDEES;

    @SerializedName("CREATE_USER")
    public String CREATE_USER;

    @SerializedName("MODIFY_USER")
    public String MODIFY_USER;

    @SerializedName("MEETING_STATUS_ID")
    public String MEETING_STATUS_ID;

    @SerializedName("RECURRENCE_TYPE")
    public String RECURRENCE_TYPE;

    @SerializedName("TheDate")
    public String TheDate;

    @SerializedName("CanCheckin")
    public Boolean CanCheckin;

    @SerializedName("CanCheckOut")
    public Boolean CanCheckOut;

    @SerializedName("Lang")
    public String Lang;
}
