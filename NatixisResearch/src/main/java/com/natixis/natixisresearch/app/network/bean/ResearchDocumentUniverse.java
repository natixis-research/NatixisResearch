package com.natixis.natixisresearch.app.network.bean;

/**
 * Created by Thibaud on 06/04/2017.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.type.DocumentType;
import com.natixis.natixisresearch.app.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ResearchDocumentUniverse {
    @JsonProperty("Id")
    private String universeId;

    @JsonProperty("Text")
    private String title;

    @JsonProperty("Code")
    private String code;

    public String getUniverseId() {
        return universeId;
    }

    public void setUniverseId(String universeId) {
        this.universeId = universeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResearchDocumentUniverse that = (ResearchDocumentUniverse) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (universeId != null ? !universeId.equals(that.universeId) : that.universeId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = universeId != null ? universeId.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }
}
