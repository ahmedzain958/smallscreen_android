package com.techsignage.techsignmeetings.Models.Interfaces;

import com.techsignage.techsignmeetings.Models.ATTENDEEModel;
import com.techsignage.techsignmeetings.Models.ServiceResponses.AttendeeResponse;
import com.techsignage.techsignmeetings.Models.ServiceResponses.AuthResponse;
import com.techsignage.techsignmeetings.Models.ServiceResponses.CreateMeetingResponse;
import com.techsignage.techsignmeetings.Models.MeetingModel;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomMeetingsResponse;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomsResponse;
import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.Models.UserModel;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by mohamed on 3/19/2017.
 */

public interface retrofitInterface {

    @POST("/api/rooms/allrooms")
    Observable<RoomsResponse> allrooms();

    @POST("/api/rooms/roomreservations")
    Observable<RoomMeetingsResponse> roomreservations(@Body UnitModel unitModel);

    @POST("/api/rooms/allroomreservations")
    Observable<RoomMeetingsResponse> allroomreservations();

    @POST("/api/rooms/startmeeting")
    Observable<RoomMeetingsResponse> startmeeting(@Body MeetingModel meetingModel);

    @POST("/api/rooms/checkin")
    Observable<AttendeeResponse> checkin(@Body ATTENDEEModel attendeeModel);

    @POST("/api/auth/allowedrooms")
    Observable<AuthResponse> allowedrooms(@Body UserModel userModel);

    @POST("/api/auth/savemeeting")
    Observable<CreateMeetingResponse> savemeeting(@Body MeetingModel meetingModel);

    @POST("/api/auth/roomblocks")
    Observable<AuthResponse> roomblocks(@Body MeetingModel meetingModel);
}
