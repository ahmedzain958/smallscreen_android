package com.techsignage.techsignmeetings.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by heat on 3/10/2017.
 */

public class WF_POI {
    @SerializedName("POI_ID")
    public String POI_ID;

    @SerializedName("POI_NAME_E")
    public String POI_NAME_E;

    @SerializedName("POI_NAME_A")
    public String POI_NAME_A;

    @SerializedName("ROOM_NO")
    public Integer ROOM_NO;

}
