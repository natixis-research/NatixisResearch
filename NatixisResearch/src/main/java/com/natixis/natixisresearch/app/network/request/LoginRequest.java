package com.natixis.natixisresearch.app.network.request;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.generic.NatixisJsonHttpContent;
import com.natixis.natixisresearch.app.network.generic.NatixisJsonObjectParser;
import com.natixis.natixisresearch.app.network.bean.User;
import com.natixis.natixisresearch.app.network.bean.UserLogin;

import roboguice.util.temp.Ln;

/**
 * Created by Thibaud on 06/04/2017.
 */
public class LoginRequest extends BaseRequest<User> {
    private String service="login";
    private UserLogin person=null;

    public LoginRequest(UserLogin person) {
        super(User.class,null);
        this.person=person;
    }

    @Override
    public User loadDataFromNetwork() throws Exception {

        String baseUrl = NatixisResearchApp.getInstance().getWebserviceBaseUrl()+service;

        Ln.d("Call web service " + baseUrl);
        NatixisJsonHttpContent content = new NatixisJsonHttpContent(person);
        GenericUrl genericUrl= new GenericUrl(baseUrl);
        HttpRequest request = getHttpRequestFactory().buildPostRequest(genericUrl, content);
        loadHeaders(request);
        request.setParser( new NatixisJsonObjectParser());
        return request.execute().parseAs(getResultType());

    }

}
