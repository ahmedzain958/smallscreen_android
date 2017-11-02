package com.techsignage.techsignmeetings.Models.ServiceResponses;

import com.google.gson.annotations.SerializedName;
import com.techsignage.techsignmeetings.Models.MeetingModel;

/**
 * Created by heat on 4/13/2017.
 */

public class RoomMeetingResponse extends ServiceResponse {
    @SerializedName("Data")
    public MeetingModel RoomMeeting;
}
