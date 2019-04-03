package com.natixis.natixisresearch.app.network.listener;

import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.bean.UniversesList;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Thibaud on 14/04/2017.
 */
public class UniverseRequestListener extends BaseRequestListener<UniversesList> {

    UniverseFinishListener mListener = null;
    RequestLanguage mLanguage;

    public UniverseRequestListener(UniverseFinishListener listener, RequestLanguage language) {
        this.mListener = listener;
        mLanguage = language;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

        mExtraDataException.put("lang",  mLanguage.toString());
        super.onRequestFailure(spiceException);
    }

    @Override
    public void onRequestFailure(SpiceException e, ResearchRequestError error) {
        mListener.OnUniverseDownloadFailed(error);
    }

    @Override
    public void onRequestSuccess(final UniversesList result) {
        mListener.OnUniverseRetrieved(result);
    }


    public interface UniverseFinishListener{
        public void OnUniverseRetrieved(UniversesList result);
        public void OnUniverseDownloadFailed(ResearchRequestError error);
    }

}
