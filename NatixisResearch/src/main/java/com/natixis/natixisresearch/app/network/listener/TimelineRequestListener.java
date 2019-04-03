package com.natixis.natixisresearch.app.network.listener;

import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Thibaud on 14/04/2017.
 */
public abstract class TimelineRequestListener extends BaseRequestListener<ResearchDocumentResultList> {

   // TimelineFinishListener mListener = null;
    protected RequestLanguage mLanguage;
    protected String mCacheKey;
    protected int mPage;
    protected boolean mForced=false;

    public TimelineRequestListener(RequestLanguage language, String cacheKey, int page, boolean forced) {
     //   this.mListener = listener;
        mLanguage = language;
        mCacheKey=cacheKey;
        mPage=page;
        mForced=forced;

    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {

        mExtraDataException.put("lang",  mLanguage.toString());
        super.onRequestFailure(spiceException);
    }

  /*  @Override
    public void onRequestFailure(SpiceException e, ResearchRequestError error) {
        mListener.OnTimelineDownloadFailed(e,error,mForced);
    }

    @Override
    public void onRequestSuccess(final ResearchDocumentResultList result) {
        mListener.OnTimelineRetrieved(result,mCacheKey,mPage,mForced);
    }


    public interface TimelineFinishListener{
        public void OnTimelineRetrieved(ResearchDocumentResultList result, String cacheKey, int page, boolean forced);
        public void OnTimelineDownloadFailed(SpiceException e,ResearchRequestError error, boolean forced);
    }*/

}
