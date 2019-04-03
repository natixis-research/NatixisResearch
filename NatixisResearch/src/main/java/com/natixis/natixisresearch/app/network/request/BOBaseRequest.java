package com.natixis.natixisresearch.app.network.request;

import com.google.api.client.http.HttpEncoding;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.natixis.natixisresearch.app.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thibaud on 06/04/2017.
 */
public abstract class BOBaseRequest<T> extends GoogleHttpClientSpiceRequest<T> {

    protected final static String WS_PARAM_SITEID = "siteId";
    protected final static String WS_PARAM_AUTH = "authorization";
    protected final static String WS_PARAM_CONTENTTYPE = "content-type";
    protected final static  List<String> valueAuthorization=new ArrayList<String>(){{add(BuildConfig.BASIC_AUTH); }};
    protected final static  List<String> contentTYpe=new ArrayList<String>(){{add("application/json");}};
    protected final static String siteIdValue="filialeA";


    protected String mToken = null;

    public BOBaseRequest(Class<T> c ) {
        super(c);
    }

    HttpHeaders loadHeaders(HttpRequest request) {
        HttpHeaders headers = new HttpHeaders();

        headers.put(WS_PARAM_AUTH,valueAuthorization);
        headers.put(WS_PARAM_SITEID,siteIdValue);
       // headers.put(WS_PARAM_CONTENTTYPE,contentTYpe);

      //  headers.fromHttpHeaders(request.getHeaders());
       // headers.set("Connection", "Close");

        request.setHeaders(headers);
        return headers;
    }


}
