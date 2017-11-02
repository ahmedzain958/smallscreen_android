package com.techsignage.techsignmeetings.Models.ServiceResponses;

import com.google.gson.annotations.SerializedName;
import com.techsignage.techsignmeetings.Models.MeetingsModel;

/**
 * Created by heat on 4/11/2017.
 */

public class RoomMeetingsResponse extends ServiceResponse {
    @SerializedName("Data")
    public MeetingsModel RoomMeetings;
}
