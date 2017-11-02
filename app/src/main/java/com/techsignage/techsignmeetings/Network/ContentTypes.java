package com.techsignage.techsignmeetings.Network;

/**
 * Created by mohamed on 2/3/2017.
 */

public enum ContentTypes {
    FormEncoded {
        public String toString() {
            return "application/x-www-form-urlencoded";
        }
    },

    Json {
        public String toString() {
            return "application/json";
        }
    }
}
