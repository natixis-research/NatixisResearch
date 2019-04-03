package com.natixis.natixisresearch.app.network.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Key;

/**
 * Created by Thibaud on 15/04/2017.
 */
public class AppCodeResult {

        @JsonProperty("Result")
        private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppCodeResult)) return false;

        AppCodeResult that = (AppCodeResult) o;

        if (result != that.result) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (result ? 1 : 0);
    }
}
