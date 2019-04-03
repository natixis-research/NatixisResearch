package com.natixis.natixisresearch.app.network.listener;

import com.natixis.natixisresearch.app.network.bean.NodeTreeResult;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.natixis.natixisresearch.app.network.request.types.NodeTreeType;
import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Thibaud on 14/04/2017.
 */
public class ResearchNodesRequestListener extends BaseRequestListener<NodeTreeResult> {

    NodeRequestFinishListener mListener = null;
    NodeTreeType mType;
    RequestLanguage mLanguage;

    public ResearchNodesRequestListener(NodeRequestFinishListener listener, NodeTreeType type, RequestLanguage language) {
        this.mListener = listener;
        mType = type;
        mLanguage = language;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

        mExtraDataException.put("lang",  mLanguage.toString());
        mExtraDataException.put("type", mType.toString() );
        super.onRequestFailure(spiceException);
    }

    @Override
    public void onRequestFailure(SpiceException e, ResearchRequestError error) {
        mListener.OnResearchNodeDownloadFailed(error);
    }

    @Override
    public void onRequestSuccess(final NodeTreeResult result) {
        mListener.OnResearchNodeRetrieved(result);
    }


    public interface NodeRequestFinishListener{
        public void OnResearchNodeRetrieved(NodeTreeResult result );
        public void OnResearchNodeDownloadFailed(ResearchRequestError error );
    }

}
