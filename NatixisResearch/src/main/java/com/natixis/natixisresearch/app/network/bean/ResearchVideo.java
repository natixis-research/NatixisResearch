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
import com.natixis.natixisresearch.app.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ResearchVideo  implements ITimelineItem  {
    @JsonProperty("guid")
    private String videoId;
    @JsonProperty("EncryptedId")
    private String encryptedId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("link")
    private String videoFile;

    @JsonProperty("source")
    private String imageFile;




    @JsonProperty("pubDate")
    private String dateStr;


    @JsonIgnore
    private Date date;



    @JsonIgnore
    public Date getDatetime() {
        if (date == null && dateStr != null) {
            date = Utils.convertDotNetDate(this.dateStr);
        }
        return date;
    }


    public String getFormattedDateSmall() {
        Date date= getDatetime();

        SimpleDateFormat format =null;
        if(DateUtils.isToday(date.getTime())){
            format = new SimpleDateFormat("HH:mm");
        }else{
            format = new SimpleDateFormat("dd/MM/yyyy");
        }

        return format.format(date);
    }


    public String getVideoId() {
        return videoId;
    }


    public void setVideoId(String videoId) {
        this.videoId = videoId;
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

    public String getVideoLink() {
        return videoFile;
    }

    public void setVideoLink(String videoLink) {
        this.videoFile = videoLink;
    }

    public String getImageLink() {
        return imageFile;
    }

    public void setImageLink(String imageLink) {
        this.imageFile = imageLink;
    }



}
