package com.ranma2913.valueObjects;

import android.util.Log;

import com.ranma2913.global.Utils;

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
public class PriceComparisonVO {
    final String TAG = "::PriceComparisonVO";

    private String itemDescription;
    private String itemPrice;
    private String numberOfUnits;
    private String typeOfUnits;
    private String creationDate;

    public PriceComparisonVO(Map<String, String> map) {
        this(map.get("itemDescription"), map.get("itemPrice"), map.get("numberOfUnits"), map.get("typeOfUnits"), map.get("creationDate"));
    }

    public PriceComparisonVO(String itemDescription, String itemPrice, String numberOfUnits, String typeOfUnits) {
        this(itemDescription, itemPrice, numberOfUnits, typeOfUnits, Utils.getCurrentTimeStampString());
    }

    public PriceComparisonVO(String itemDescription, String itemPrice, String numberOfUnits, String typeOfUnits, String creationDate) {
        this.setItemDescription(itemDescription);
        this.setItemPrice(itemPrice);
        this.setNumberOfUnits(numberOfUnits);
        this.setTypeOfUnits(typeOfUnits);
        this.setCreationDate(creationDate);
        Log.d(TAG, "PriceComparisonVO Created :" + getValueMap().toString());
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice.replace("$", "").trim();
    }

    public String getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(String numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public String getTypeOfUnits() {
        return typeOfUnits;
    }

    public void setTypeOfUnits(String typeOfUnits) {
        this.typeOfUnits = typeOfUnits;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getPricePerUnit() {
        return new BigDecimal(new BigDecimal(itemPrice).divide(new BigDecimal(numberOfUnits), MathContext.DECIMAL128).toString()).setScale(4, RoundingMode.HALF_UP).toString();
    }

    public HashMap getValueMap() {
        return new HashMap<String, String>() {
            {
                put("itemDescription", itemDescription);
                put("itemPrice", itemPrice);
                put("numberOfUnits", numberOfUnits);
                put("typeOfUnits", typeOfUnits);
                put("creationDate", creationDate);
            }
        };
    }

    public String getPricePerUnitString() {
        return itemDescription + ": $" + getPricePerUnit() + " / " + typeOfUnits;
    }

    @Override
    public String toString() {
        return getPricePerUnitString();
    }
}
