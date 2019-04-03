package com.natixis.natixisresearch.app.network.bean;

/**
 * Created by Thibaud on 06/04/2017.
 */

import android.content.Context;
import android.text.format.DateUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.network.bean.type.DocumentType;
import com.natixis.natixisresearch.app.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ResearchDocument implements ITimelineItem {
    @JsonProperty("Id")
    private int documentID;
    @JsonProperty("EncryptedId")
    private String encryptedId;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Summary")
    private String description;

    @JsonProperty("FileName")
    private String filename;

    @JsonProperty("Universe")
    private ResearchDocumentUniverse universe;



    @JsonProperty("DocumentType")
    private ResearchDocumentType documentType;


    @JsonProperty("Date")
    private String dateStr;

    @JsonProperty("UpdateDate")
    private String updateDateStr;

    @JsonIgnore
    private Date date;


    public String getDateStr() {
       return updateDateStr;
    }

    @JsonIgnore
    public Date getDatetime() {
        if (date == null && dateStr != null) {
            date = Utils.convertDotNetDate(this.dateStr);
        }
        return date;
    }


    public ResearchDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(ResearchDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getFilepath() {
        return NatixisResearchApp.getInstance().getCacheDir() + "/" + getFilename();
    }

    final static long MILLI_TO_MINUTES = 1000 * 60;
    final static long MILLI_TO_HOUR = MILLI_TO_MINUTES * 60;
    final static long MILLI_TO_DAY = MILLI_TO_HOUR * 24;

    public String getTimeSince(Context c) {
        Date d = getDatetime();
        if(d!=null) {
            long diff = System.currentTimeMillis() - d.getTime();
           // long nbMins = diff / MILLI_TO_MINUTES;
            long nbMins = Math.max(diff / MILLI_TO_MINUTES,0);
            if (nbMins < 60) {
                return nbMins + "'";
            } else {
                long nbHour = diff / MILLI_TO_HOUR;
                if (nbHour < 24) {
                    return nbHour + "h";
                } else {
                    long nbDays = diff / MILLI_TO_DAY;
                    if (nbDays < 8) {
                        return nbDays + c.getString(R.string.day);
                    } else {
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM");
                        return df.format(d);
                    }
                }
            }
        }
        return "0m";
    }

    public String getUrl() {
        return NatixisResearchApp.getInstance().getWebserviceBaseUrl() + "document" + "/" + getEncryptedId();
    }

    public int getDocumentID() {
        return documentID;
    }

    public String getDocumentUniqueId() {
        return documentID+"";
    }

    public void setDocumentID(int documentID) {
        this.documentID = documentID;
    }

    public String getEncryptedId() {
        return encryptedId;
    }

    public void setEncryptedId(String encryptedId) {
        this.encryptedId = encryptedId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ResearchDocumentUniverse getUniverse() {
        return universe;
    }

    public void setUniverse(ResearchDocumentUniverse universe) {
        this.universe = universe;
    }

    public String getUpdateDateStr() {
        return updateDateStr;
    }


}
