package com.techsignage.techsignmeetings.Models.ServiceResponses;

import com.google.gson.annotations.SerializedName;
import com.techsignage.techsignmeetings.Models.AuthElementsModel;

/**
 * Created by heat on 5/21/2017.
 */

public class AuthResponse extends ServiceResponse {
    @SerializedName("Data")
    public AuthElementsModel authElements;
}
