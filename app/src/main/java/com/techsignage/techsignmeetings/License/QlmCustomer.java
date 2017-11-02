package com.techsignage.techsignmeetings.License;

/**
 * Created by heat on 7/24/2017.
 */


public class QlmCustomer {
    private static QlmCustomer instance;
    public QlmCustomer() {}
    public static synchronized QlmCustomer getInstance() {
        if (instance == null) {
            instance = new QlmCustomer();
        }
        return instance;
    }

    private String userCompany;
    private String userFullName;
    private String userEmail;

    public String getUserCompany() {
        return userCompany;
    }
    public void setUserCompany(String userCompany) {
        this.userCompany = userCompany;
    }

    public String getUserFullName() {
        return userFullName;
    }
    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
