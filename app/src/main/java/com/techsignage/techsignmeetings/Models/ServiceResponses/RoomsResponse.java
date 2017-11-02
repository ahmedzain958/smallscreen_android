package com.techsignage.techsignmeetings.Models.ServiceResponses;

import com.google.gson.annotations.SerializedName;
import com.techsignage.techsignmeetings.Models.UnitModel;

import java.util.List;

/**
 * Created by heat on 5/10/2017.
 */

public class RoomsResponse extends ServiceResponse {
    @SerializedName("Data")
    public List<UnitModel> Rooms;
}
