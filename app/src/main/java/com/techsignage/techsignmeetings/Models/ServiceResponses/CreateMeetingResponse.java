package com.techsignage.techsignmeetings.Models.ServiceResponses;

import com.google.gson.annotations.SerializedName;
import com.techsignage.techsignmeetings.Models.MeetingModel;

/**
 * Created by heat on 5/25/2017.
 */

public class CreateMeetingResponse extends ServiceResponse {
    @SerializedName("Data")
    public MeetingModel meetingModel;
}
