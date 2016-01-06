package com.ranma2913.pricecompare.database;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created by jsticha on 1/6/2016.
 */
public class PriceComparison implements Serializable {
    /**
     * Model class for SQLite PriceComparison table
     */
    private static final long serialVersionUID = 1L;

    // Primary key defined as an auto generated integer
    // If the database table column name differs than the Model class variable name, the way to map to use columnName
    @DatabaseField(generatedId = true, columnName = "_id")
    public int id;

    @DatabaseField
    public String storeName;

    @DatabaseField
    public String itemDescription;

    @DatabaseField
    public String itemPrice;

    @DatabaseField
    public String numberOfUnits;

    @DatabaseField
    public String typeOfUnits;

    @DatabaseField
    public String creationDate;

    public PriceComparison() {

    }

    public PriceComparison(String storeName, String itemDescription, String itemPrice,
                           String numberOfUnits, String typeOfUnits, String creationDate) {
        this.storeName = storeName;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.numberOfUnits = numberOfUnits;
        this.typeOfUnits = typeOfUnits;
        this.creationDate = creationDate;
    }

    public String getPricePerUnit() {
        BigDecimal bigDecimal1 = new BigDecimal(itemPrice);
        BigDecimal bigDecimal2 = new BigDecimal(numberOfUnits);
        BigDecimal bigDecimal = new BigDecimal(bigDecimal1.divide(bigDecimal2, MathContext.DECIMAL128).toString()).setScale(4, RoundingMode.HALF_UP);
        return bigDecimal.toString();
    }

    public String getPricePerUnitString() {
        return "$" + getPricePerUnit() + " / " + typeOfUnits;
    }

    public String getQuantityAndUnitsString() {
        return numberOfUnits + " " + typeOfUnits;
    }

    @Override
    public String toString() {
        return storeName + " " + itemDescription + " " + getPricePerUnitString();
    }
}
