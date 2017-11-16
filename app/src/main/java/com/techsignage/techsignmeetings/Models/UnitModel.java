package com.techsignage.techsignmeetings.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by heat on 3/10/2017.
 */

public class UnitModel {
    @SerializedName("UNIT_ID")
    public String UNIT_ID;

    @SerializedName("UNIT_NAME")
    public String UNIT_NAME;

    @SerializedName("UNIT_DESCRIPTION")
    public String UNIT_DESCRIPTION;

    @SerializedName("FLOOR_ID")
    public String FLOOR_ID;

    @SerializedName("TSF_Themes")
    public ThemeModel Theme;

}
