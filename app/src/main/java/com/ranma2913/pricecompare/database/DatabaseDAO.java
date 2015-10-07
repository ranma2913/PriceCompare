package com.ranma2913.pricecompare.database;

import java.util.ArrayList;

/**
 * Created by jsticha on 8/5/2015.
 */
public interface DatabaseDao {
    String TAG = DatabaseDao.class.getSimpleName();

    ArrayList<PriceComparison> getAllPriceComparisons();

    boolean deletePriceComparison(PriceComparison docToDelete);

    PriceComparison updatePriceComparison(PriceComparison docWithUpdates);

    PriceComparison saveNewPriceComparison(String storeName, String itemDescription, String itemPrice, String numberOfUnits, String typeOfUnits);

    boolean deleteDatabase();
}
