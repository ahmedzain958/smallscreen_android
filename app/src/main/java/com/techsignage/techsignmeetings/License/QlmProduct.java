package com.techsignage.techsignmeetings.License;

/**
 * Created by heat on 7/24/2017.
 */


public class QlmProduct {
    private static QlmProduct instance;
    public QlmProduct() {}
    public static synchronized QlmProduct getInstance() {
        if (instance == null) {
            instance = new QlmProduct();
        }
        return instance;
    }

    private int productID;
    private String ProductName;
    private int majorVersion;
    private int minorVersion;

    public int getProductID() {
        return productID;
    }
    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return ProductName;
    }
    public void setProductName(String productName) {
        ProductName = productName;
    }

    public int getMajorVersion() {
        return majorVersion;
    }
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }
}
