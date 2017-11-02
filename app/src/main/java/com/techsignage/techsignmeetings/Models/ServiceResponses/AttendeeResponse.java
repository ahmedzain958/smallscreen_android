package com.techsignage.techsignmeetings.Models.ServiceResponses;

import com.google.gson.annotations.SerializedName;
import com.techsignage.techsignmeetings.Models.ATTENDEEModel;

/**
 * Created by heat on 5/2/2017.
 */

public class AttendeeResponse extends ServiceResponse {
    @SerializedName("Data")
    public ATTENDEEModel attendeeModel;
}
