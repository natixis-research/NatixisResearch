package com.natixis.natixisresearch.app.network.request;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

/**
 * Created by Thibaud on 06/04/2017.
 */
public abstract class BaseRequest<T> extends GoogleHttpClientSpiceRequest<T> {

    //protected final static String WS_PARAM_APPID = "appId";
    protected final static String WS_PARAM_TOKEN = "token";


    protected String mToken = null;

    public BaseRequest(Class<T> c, String token) {
        super(c);
        mToken = token;
    }

    HttpHeaders loadHeaders(HttpRequest request) {
        HttpHeaders headers = new HttpHeaders();
       // headers.put(WS_PARAM_APPID, NatixisResearchApp.getInstance().getWebserviceAppId());
        if (mToken != null) {
            headers.put(WS_PARAM_TOKEN, mToken);
        }
        headers.fromHttpHeaders(request.getHeaders());
       // headers.set("Connection", "Close");
        request.setHeaders(headers);
        return headers;
    }


}
