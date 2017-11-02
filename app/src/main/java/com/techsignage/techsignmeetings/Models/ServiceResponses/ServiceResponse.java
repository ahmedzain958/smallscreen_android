package com.techsignage.techsignmeetings.Models.ServiceResponses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by heat on 3/11/2017.
 */

public class ServiceResponse {
    @SerializedName("Message")
    public String Message;

    @SerializedName("ResponseStatus")
    public Boolean ResponseStatus;

    @SerializedName("Code")
    public Integer Code;

    @SerializedName("ArabicMessage")
    public String ArabicMessage;
}