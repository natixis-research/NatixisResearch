package com.natixis.natixisresearch.app.network.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;

/**
 * Created by Thibaud on 14/04/2017.
 */
public class ResearchRequestError {

    @JsonProperty("ErrorCode")
    private int errorCode;

    @JsonProperty("FaultMessage")
    private String errorMessage;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchRequestError)) return false;

        ResearchRequestError error = (ResearchRequestError) o;

        if (errorCode != error.errorCode) return false;
        if (errorMessage != null ? !errorMessage.equals(error.errorMessage) : error.errorMessage != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = errorCode;
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result;
    }
}
