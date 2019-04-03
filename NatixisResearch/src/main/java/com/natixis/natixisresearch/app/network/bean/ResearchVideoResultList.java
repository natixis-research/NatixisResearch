package com.natixis.natixisresearch.app.network.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Thibaud on 14/05/2017.
 */
public class ResearchVideoResultList {


    @JsonProperty("GetVideosResult")
    private VideosList videos;


    public VideosList getVideos() {
        return videos;
    }
}
