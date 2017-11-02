package com.techsignage.techsignmeetings.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by heat on 5/21/2017.
 */

public class HourModel {
    @SerializedName("CombinedHours")
    public String CombinedHours;

    @SerializedName("StartHour")
    public String StartHour;

    @SerializedName("EndHour")
    public String EndHour;

    @SerializedName("IsEnabled")
    public Boolean IsEnabled;

    @SerializedName("IsBooked")
    public Boolean IsBooked;

    public Boolean IsSelected;
}
