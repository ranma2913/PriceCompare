package com.ranma2913.pricecompare;

import java.util.ArrayList;

/**
 * Created by jsticha on 8/5/2015.
 */
public interface DatabaseDAO {
    ArrayList<PriceComparison> getAllPriceComparisons();

    boolean deletePriceComparison(PriceComparison docToDelete);

    PriceComparison updatePriceComparison(PriceComparison docWithUpdates);

    PriceComparison saveNewPriceComparison(String storeName, String itemDescription, String itemPrice, String numberOfUnits, String typeOfUnits);
}
