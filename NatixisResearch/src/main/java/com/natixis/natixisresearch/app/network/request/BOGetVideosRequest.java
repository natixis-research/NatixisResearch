package com.natixis.natixisresearch.app.network.request;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.ResearchVideoResultList;
import com.natixis.natixisresearch.app.network.bean.UniversesList;
import com.natixis.natixisresearch.app.network.bean.VideosList;
import com.natixis.natixisresearch.app.network.generic.NatixisJsonObjectParser;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;

import roboguice.util.temp.Ln;

/**
 * Created by Thibaud on 06/04/2017.
 */
public class BOGetVideosRequest extends BOBaseRequest<ResearchVideoResultList> {
    private final static  String service = "videos";


    private String mIdUniverse;
    private int mNbItems=10;
    private int mPage=1; //start at 1
    private String mFilter=null;
    private RequestLanguage language;

    public BOGetVideosRequest(RequestLanguage language,String idUniverse, int nbItemsPerPage, int page) {
        super(ResearchVideoResultList.class);
        this.language = language;
        mIdUniverse=idUniverse;
        mPage=page;
        mNbItems=nbItemsPerPage;
    }

    public BOGetVideosRequest(RequestLanguage language,String idUniverse,int nbItemsPerPage,   int page, String filter) {
        this(   language, idUniverse, nbItemsPerPage,  page);
        mFilter=filter;
    }

    @Override
    public ResearchVideoResultList loadDataFromNetwork() throws Exception {

       String baseUrl = NatixisResearchApp.getInstance().getBackOfficeBaseUrl() + service;// + "/" + language.toString();

        Ln.d("Call web service " + baseUrl);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(new GenericUrl(baseUrl));
        loadHeaders(request);

        request.setParser( new NatixisJsonObjectParser());
        HttpResponse response =request.execute();


        return response.parseAs(getResultType());

    }

}
