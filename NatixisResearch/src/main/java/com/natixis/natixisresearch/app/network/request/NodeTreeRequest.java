package com.natixis.natixisresearch.app.network.request;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.NodeTreeResult;
import com.natixis.natixisresearch.app.network.generic.NatixisJsonObjectParser;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.natixis.natixisresearch.app.network.request.types.NodeTreeType;

import roboguice.util.temp.Ln;

/**
 * Created by Thibaud on 06/04/2017.
 */
public class NodeTreeRequest extends BaseRequest<NodeTreeResult> {
    private String service = "getfulltree";
    private NodeTreeType type;
    private RequestLanguage language;

    public NodeTreeRequest(String token, NodeTreeType type, RequestLanguage language) {
        super(NodeTreeResult.class, token);
        this.type = type;
        this.language = language;
    }

    @Override
    public NodeTreeResult loadDataFromNetwork() throws Exception {

        String baseUrl = NatixisResearchApp.getInstance().getWebserviceBaseUrl() + service + "/" + language.toString() + "/" + type.toString();
        Ln.d("Call web service " + baseUrl);
        HttpRequest request = getHttpRequestFactory()
                .buildGetRequest(new GenericUrl(baseUrl));
        loadHeaders(request);

        request.setParser( new NatixisJsonObjectParser());
        HttpResponse response =request.execute();
        return response.parseAs(getResultType());

    }

}
