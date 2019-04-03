package com.natixis.natixisresearch.app.network.listener;

import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.ResearchVideoResultList;
import com.natixis.natixisresearch.app.network.bean.VideosList;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Thibaud on 14/04/2017.
 */
public abstract class TimelineVideoRequestListener extends BaseRequestListener<ResearchVideoResultList> {


    protected RequestLanguage mLanguage;
    protected String mCacheKey;
    protected int mPage;
    protected boolean mForced=false;

    public TimelineVideoRequestListener(RequestLanguage language, String cacheKey, int page, boolean forced) {
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


}
