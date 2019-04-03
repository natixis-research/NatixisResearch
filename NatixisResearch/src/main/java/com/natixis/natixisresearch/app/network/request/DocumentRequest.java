package com.natixis.natixisresearch.app.network.request;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.octo.android.robospice.request.simple.BigBinaryRequest;

import java.io.File;

/**
 * Created by Thibaud on 08/04/2017.
 */
public class DocumentRequest extends BigBinaryRequest {
    public DocumentRequest(ResearchDocument document) {
        super(document.getUrl(),getCacheFile(document) );


    }

    private static File getCacheFile(ResearchDocument document){
        return new File(document.getFilepath());
    }


}
