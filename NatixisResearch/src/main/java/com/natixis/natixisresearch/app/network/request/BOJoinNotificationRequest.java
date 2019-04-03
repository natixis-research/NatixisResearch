package com.natixis.natixisresearch.app.network.request;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.ResearchVideoResultList;
import com.natixis.natixisresearch.app.network.generic.NatixisJsonObjectParser;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.natixis.natixisresearch.app.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import roboguice.util.temp.Ln;

/**
 * Created by Thibaud on 06/04/2017.
 */
public class BOJoinNotificationRequest extends BOBaseRequest<String> {
    private final static String service = "join-notification";


    private String mUsername;
    private String mToken;


    public BOJoinNotificationRequest(String username, String token) {
        super(String.class);
        mUsername = username;
        mToken=token;

    }



    @Override
    public String loadDataFromNetwork() throws Exception {

       String baseUrl = NatixisResearchApp.getInstance().getBackOfficeBaseUrl() + service;// + "/" + language.toString();

        Ln.d("Call web service " + baseUrl);
        Map<String, String> json = new HashMap<String, String>();
        json.put("username", mUsername);
        json.put("token", mToken);
        json.put("notificationtype", "1");

        final HttpContent content = new JsonHttpContent(new JacksonFactory(), json);
        Log.d("JoinRequest","Send join request "+ content.toString());
        HttpRequest request = getHttpRequestFactory()
                .buildPostRequest(new GenericUrl(baseUrl),content);
        loadHeaders(request);

        OutputStream output = new OutputStream()
        {
            private StringBuilder string = new StringBuilder();
            @Override
            public void write(int b) throws IOException {
                this.string.append((char) b );
            }

            //Netbeans IDE automatically overrides this toString()
            public String toString(){
                return this.string.toString();
            }
        };

        content.writeTo(output);
        Log.d("JoinRequest","Send join request "+ output.toString());
        request.setParser( new NatixisJsonObjectParser());
        HttpResponse response =request.execute();


        //return response.parseAs(getResultType());
        return Utils.convertStreamToString(response.getContent());

    }

}
