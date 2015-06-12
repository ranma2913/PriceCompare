package com.ranma2913.global;

import com.ranma2913.valueObjects.PriceComparisonVO;

import java.util.Comparator;

/**
 * Created by jsticha on 5/6/2015.
 * <p/>
 * Comparator to sort VOs by creation date ascending.
 */
public class PriceComparisonComparator implements Comparator<PriceComparisonVO> {
    @Override
    public int compare(PriceComparisonVO lhs, PriceComparisonVO rhs) {
        String creationDate1 = lhs.getCreationDate();
        String creationDate2 = rhs.getCreationDate();
        // ascending order (descending order would be: name2.compareTo(name1))
        return creationDate2.compareTo(creationDate1);
    }
}
