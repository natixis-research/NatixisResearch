package com.natixis.natixisresearch.app.network.bean;

/**
 * Created by Thibaud on 06/04/2017.
 */

import com.fasterxml.jackson.annotation.JsonProperty;


public class ResearchDocumentType {
    @JsonProperty("Code")
    private String code;

    @JsonProperty("Text")
    private String title;

    @JsonProperty("Id")
    private int id;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResearchDocumentType that = (ResearchDocumentType) o;

        if (id != that.id) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }
}
