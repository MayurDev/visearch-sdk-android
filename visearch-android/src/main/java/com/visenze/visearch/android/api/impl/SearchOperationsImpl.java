package com.visenze.visearch.android.api.impl;

import android.content.Context;

import com.visenze.visearch.android.ColorSearchParams;
import com.visenze.visearch.android.IdSearchParams;
import com.visenze.visearch.android.UploadSearchParams;
import com.visenze.visearch.android.ViSearch;
import com.visenze.visearch.android.ViSearchException;
import com.visenze.visearch.android.api.SearchOperations;
import com.visenze.visearch.android.http.HttpInstance;

/**
 * Implementation of search operations interface.
 */
public class SearchOperationsImpl implements SearchOperations {
    /**
     * URL
     */
    private static String apiBase;
    private final static String ID_SEARCH = "/search";
    private final static String COLOR_SEARCH = "/colorsearch";
    private final static String UPLOAD_SEARCH = "/uploadsearch";

    /**
     * volley http instance
     */
    private HttpInstance httpInstance;

    /**
     * SearchOperationsImpl: search operation implementation
     *
     * @param apiUrl api url
     * @param context activity context
     * @param accessKey access key
     * @param secretKey secret key
     */
    public SearchOperationsImpl(String apiUrl, Context context, String accessKey, String secretKey) {
        apiBase = apiUrl;
        httpInstance = HttpInstance.getInstance(context.getApplicationContext());
        httpInstance.setKeys(accessKey, secretKey);
    }

    /**
     * /search api
     *
     * @param idSearchParams the index search parameter setting
     * @param resultListener result listener
     */
    @Override
    public void search(IdSearchParams idSearchParams, final ViSearch.ResultListener resultListener) {
        String imageId = idSearchParams.getImageName();

        if (imageId == null || imageId.isEmpty()) {
            throw new ViSearchException("Missing parameter, image name is empty");
        }

        httpInstance.addGetRequestToQueue(apiBase + ID_SEARCH, idSearchParams.toMap(), resultListener);
    }

    /**
     * /colorsearch api
     *
     * @param colorSearchParams the color search parameter setting
     * @param resultListener result listener
     */
    @Override
    public void colorSearch(ColorSearchParams colorSearchParams, final ViSearch.ResultListener resultListener) {
        String color = colorSearchParams.getColor();
        if (color == null || color.isEmpty()) {
            throw new ViSearchException("Missing parameter, color code is empty");
        }
        if (!color.matches("^[0-9a-fA-F]{6}$")) {
            throw new ViSearchException("Invalid parameter, only accept hex color code");
        }

        httpInstance.addGetRequestToQueue(apiBase + COLOR_SEARCH, colorSearchParams.toMap(), resultListener);
    }

    /**
     * /uploadsearch api
     *
     * @param uploadSearchParams the index search parameter setting
     * @param resultListener result listener
     */
    @Override
    public void uploadSearch(UploadSearchParams uploadSearchParams, final ViSearch.ResultListener resultListener) {
        byte[] imageBytes = uploadSearchParams.getImage().getByteArray();
        String imageUrl = uploadSearchParams.getImageUrl();

        String response;
        if (imageBytes == null && (imageUrl == null || imageUrl.isEmpty())) {
            throw new ViSearchException("Missing parameter, image empty");

        } else if (imageBytes != null) {
            httpInstance.addMultipartRequestToQueue(apiBase + UPLOAD_SEARCH, uploadSearchParams.toMap(), imageBytes, resultListener);
        } else {
            httpInstance.addGetRequestToQueue(apiBase+UPLOAD_SEARCH, uploadSearchParams.toMap(), resultListener);
        }
    }

    /**
     * cancel search
     *
     * @param resultListener callback listener to be canceled
     */
    public void cancelSearch(ViSearch.ResultListener resultListener) {
        httpInstance.cancelRequest(resultListener);
    }
}
