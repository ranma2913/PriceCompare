package com.ranma2913.pricecompare.placesApi;

/**
 * Created by jsticha on 10/8/2015.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaceJsonParser {
    private final String TAG = PlaceJsonParser.class.getSimpleName();

    /**
     * Receives a JSONObject and returns a list
     */
    public List<HashMap<String, String>> parse(JSONObject jObject) {
        Log.d(TAG + "@parse", "enter");
        JSONArray jPlaces = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            jPlaces = jObject.getJSONArray("predictions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        List<HashMap<String, String>> placesList = getPlaces(jPlaces);
        Log.d(TAG + "@parse", "exit");
        return placesList;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
        Log.d(TAG + "@getPlaces", "enter");
        int placesCount = jPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> place = null;

        /** Taking each place, parses and adds to list object */
        for (int i = 0; i < placesCount; i++) {
            try {
                /** Call getPlace with place JSON object to parse the place */
                place = getPlace((JSONObject) jPlaces.get(i));
                placesList.add(place);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG + "@getPlaces", "exit");
        return placesList;
    }

    /**
     * Parsing the Place JSON object
     */
    private HashMap<String, String> getPlace(JSONObject jPlace) {
        Log.i(TAG + "@getPlace", "enter");
        HashMap<String, String> place = new HashMap<>();

        String id = "";
        String reference = "";
        String longDescription = "";
        String name = "";
        String city = "";
        try {

            longDescription = jPlace.getString("description");
            id = jPlace.getString("id");
            reference = jPlace.getString("reference");
            name = jPlace.getJSONArray("terms").getJSONObject(0).getString("value");
            city = jPlace.getJSONArray("terms").getJSONObject(2).getString("value");
            place.put("name", name);
            place.put("city", city);
            place.put("description", name + ", " + city);
            place.put("longDescription", longDescription);
            place.put("_id", id);
            place.put("reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG + "@getPlace", "exit");
        return place;
    }
}
