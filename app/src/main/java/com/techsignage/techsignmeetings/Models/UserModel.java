package com.techsignage.techsignmeetings.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by heat on 4/16/2017.
 */

public class UserModel {
    @SerializedName("USER_ID")
    public String USER_ID;

    @SerializedName("FIRST_NAME")
    public String FIRST_NAME;

    @SerializedName("LAST_NAME")
    public String LAST_NAME;

    @SerializedName("PASSWORD")
    public String PASSWORD;

    @SerializedName("USERNAME")
    public String USERNAME;

    @SerializedName("UnitId")
    public String UnitId;
}
