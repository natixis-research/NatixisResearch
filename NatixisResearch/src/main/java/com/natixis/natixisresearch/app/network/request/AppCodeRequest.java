package com.natixis.natixisresearch.app.network.request;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.AppCodeResult;
import com.natixis.natixisresearch.app.network.generic.NatixisJsonHttpContent;
import com.natixis.natixisresearch.app.network.generic.NatixisJsonObjectParser;
import com.natixis.natixisresearch.app.network.bean.UserLogin;

import roboguice.util.temp.Ln;

/**
 * Created by Thibaud on 06/04/2017.
 */
public class AppCodeRequest extends BaseRequest<AppCodeResult> {
    private String service="getappcode";
    private UserLogin person=null;

    public AppCodeRequest(UserLogin person) {
        super(AppCodeResult.class,null);
        this.person=person;
    }

    @Override
    public AppCodeResult loadDataFromNetwork() throws Exception {

        String baseUrl = NatixisResearchApp.getInstance().getWebserviceBaseUrl()+service;

        Ln.d("Call web service " + baseUrl);

        NatixisJsonHttpContent content = new NatixisJsonHttpContent(person);
        GenericUrl genericUrl= new GenericUrl(baseUrl);
        HttpRequest request = getHttpRequestFactory().buildPostRequest(genericUrl,content);
        loadHeaders(request);
        //request.setParser(new JacksonFactory().createJsonObjectParser());
        request.setParser( new NatixisJsonObjectParser());
        HttpResponse response = request.execute();
        return response.parseAs(getResultType());

    }

}
