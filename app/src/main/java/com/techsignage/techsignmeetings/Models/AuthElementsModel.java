package com.techsignage.techsignmeetings.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by heat on 5/21/2017.
 */

public class AuthElementsModel {
    @SerializedName("rooms")
    public List<UnitModel> rooms;

    @SerializedName("hours")
    public List<HourModel> hours;

    @SerializedName("loggeduser")
    public UserModel loggeduser;
}
