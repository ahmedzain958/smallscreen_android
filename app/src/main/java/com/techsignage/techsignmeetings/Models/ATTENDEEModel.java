package com.techsignage.techsignmeetings.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by heat on 4/23/2017.
 */

public class ATTENDEEModel {
    @SerializedName("ATTENDTEE_ID")
    public String ATTENDTEE_ID;

    @SerializedName("MEETING_ID")
    public String MEETING_ID;

    @SerializedName("ATTENDTEE_FIRST_NAME")
    public String ATTENDTEE_FIRST_NAME;

    @SerializedName("ATTENDTEE_LAST_NAME")
    public String ATTENDTEE_LAST_NAME;

    @SerializedName("CHECK_IN")
    public String CHECK_IN;

    @SerializedName("CHECK_OUT")
    public String CHECK_OUT;

    @SerializedName("CheckIn")
    public Boolean CheckIn;
}
