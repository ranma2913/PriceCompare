package com.ranma2913.pricecompare.database;

import android.util.Log;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jsticha on 5/6/2015.
 * <p/>
 * VO containing a price comparison
 */
public class PriceComparison {
    final String TAG = PriceComparison.class.getSimpleName();

    private String documentID;
    private String docType;
    private Map<String, String> documentMap;

    public PriceComparison() {
        this.documentID = "";
        this.docType = "priceComparison";
        this.documentMap = new HashMap<>();
    }

    public PriceComparison(String documentID, Map<String, String> documentMap) {
        this.documentMap = documentMap;
        this.documentID = documentID;
        this.docType = "priceComparison";
        Log.d(TAG + "@PriceComparison", "PriceComparison Created :" + getDocProperties());
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
        this.documentMap.put("documentID", documentID);
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getStoreName() {
        return documentMap.get("storeName");
    }

    public void setStoreName(String storeName) {
        this.documentMap.put("storeName", storeName);
    }

    public String getItemDescription() {
        return documentMap.get("itemDescription");
    }

    public void setItemDescription(String itemDescription) {
        this.documentMap.put("itemDescription", itemDescription);
    }

    public String getItemPrice() {
        return documentMap.get("itemPrice");
    }

    public void setItemPrice(String itemPrice) {
        this.documentMap.put("itemPrice", itemPrice.replace("$", "").trim());
    }

    public String getNumberOfUnits() {
        return documentMap.get("numberOfUnits");
    }

    public void setNumberOfUnits(String numberOfUnits) {
        this.documentMap.put("numberOfUnits", numberOfUnits);
    }

    public String getTypeOfUnits() {
        return documentMap.get("typeOfUnits");
    }

    public void setTypeOfUnits(String typeOfUnits) {
        this.documentMap.put("typeOfUnits", typeOfUnits);
    }

    public String getCreationDate() {
//        return (null != documentMap.get("creationDate") ? documentMap.get("creationDate") : Utils.getCurrentTimeStampString());
        return documentMap.get("creationDate");
    }

    public void setCreationDate(String creationDate) {
        this.documentMap.put("creationDate", creationDate);
    }

    public String getPricePerUnit() {
        return new BigDecimal(new BigDecimal(getItemPrice()).divide(new BigDecimal(getNumberOfUnits()), MathContext.DECIMAL128).toString()).setScale(4, RoundingMode.HALF_UP).toString();
    }

    public Map getDocProperties() {
        return documentMap;
    }

    public String getPricePerUnitString() {
        return "$" + getPricePerUnit() + " / " + getTypeOfUnits();
    }

    public String getQuantityAndUnitsString() {
        return getNumberOfUnits() + " " + getTypeOfUnits();
    }

    @Override
    public String toString() {
        return getStoreName() + " " + getItemDescription() + " " + getPricePerUnitString();
    }
}
