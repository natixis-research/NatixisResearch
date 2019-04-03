package com.natixis.natixisresearch.app.network.request;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.generic.NatixisJsonObjectParser;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;

import roboguice.util.temp.Ln;

/**
 * Created by Thibaud on 06/04/2017.
 */
public class SearchVideoRequest extends BaseRequest<ResearchDocumentResultList> {
    private String service = "search";
    private String mIdUniverse;
    private int mNbItems=10;
    private int mPage=1; //start at 1
    private String mFilter=null;
    private RequestLanguage language;

    public SearchVideoRequest(String token, RequestLanguage language, String idUniverse, int nbItemsPerPage, int page) {
        super(ResearchDocumentResultList.class, token);
        this.language = language;
        mIdUniverse=idUniverse;
        mPage=page;
        mNbItems=nbItemsPerPage;
    }

    public SearchVideoRequest(String token, RequestLanguage language, String idUniverse, int nbItemsPerPage, int page, String filter) {
        this( token,  language, idUniverse, nbItemsPerPage,  page);
        mFilter=filter;
    }
    @Override
    public ResearchDocumentResultList loadDataFromNetwork() throws Exception {

       String baseUrl = NatixisResearchApp.getInstance().getWebserviceBaseUrl() + service + "/" + language.toString();
        baseUrl+="/"+mIdUniverse+"/"+mNbItems+"/"+mPage;
        if(mFilter!=null && mFilter.length()>0){
            baseUrl+="?query="+mFilter;
        }
        Ln.d("Call web service " + baseUrl);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(new GenericUrl(baseUrl));
        loadHeaders(request);

        request.setParser( new NatixisJsonObjectParser());
        HttpResponse response =request.execute();
        return response.parseAs(getResultType());

    }

}
