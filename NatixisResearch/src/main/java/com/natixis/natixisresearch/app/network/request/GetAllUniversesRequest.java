package com.natixis.natixisresearch.app.network.request;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.UniversesList;
import com.natixis.natixisresearch.app.network.generic.NatixisJsonObjectParser;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;

import roboguice.util.temp.Ln;

/**
 * Created by Thibaud on 06/04/2017.
 */
public class GetAllUniversesRequest extends BaseRequest<UniversesList> {
    private String service = "getalluniverses";

    private RequestLanguage language;

    public GetAllUniversesRequest(String token, RequestLanguage language) {
        super(UniversesList.class, token);
        this.language = language;

    }

    @Override
    public UniversesList loadDataFromNetwork() throws Exception {

       String baseUrl = NatixisResearchApp.getInstance().getWebserviceBaseUrl() + service + "/" + language.toString();

        Ln.d("Call web service " + baseUrl);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(new GenericUrl(baseUrl));
        loadHeaders(request);

        request.setParser( new NatixisJsonObjectParser());
        HttpResponse response =request.execute();
        return response.parseAs(getResultType());

    }

}
