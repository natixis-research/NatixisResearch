package com.natixis.natixisresearch.app.network.listener;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.api.client.http.HttpResponseException;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.HashMap;

/**
 * Created by Thibaud on 14/04/2017.
 */
public abstract class BaseRequestListener<T> implements RequestListener<T> {

    protected HashMap<String, String> mExtraDataException = new HashMap<String, String>();

    @Override
    public void onRequestFailure(SpiceException spiceException) {

       // BugSenseHandler.sendExceptionMap(mExtraDataException, spiceException);
        ResearchRequestError error = null;

        if (spiceException.getCause() instanceof HttpResponseException) {
            HttpResponseException httpEx = (HttpResponseException) spiceException.getCause();
            String result = httpEx.getContent();

            ObjectMapper mapper = new ObjectMapper();
            try {
                error = mapper.readValue(result, ResearchRequestError.class);

            } catch (Exception ex) {
               error = new ResearchRequestError();
                error.setErrorCode(httpEx.getStatusCode());
                error.setErrorMessage(httpEx.getStatusMessage());
              //  BugSenseHandler.sendExceptionMap(mExtraDataException, ex);
            }
        }

        onRequestFailure(spiceException, error);


    }

    public abstract void onRequestFailure(SpiceException spiceException, ResearchRequestError error);
}
