package com.natixis.natixisresearch.app.network.listener;

import com.natixis.natixisresearch.app.network.bean.AppCodeResult;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Thibaud on 14/04/2017.
 */
public class AppCodeRequestListener extends BaseRequestListener<AppCodeResult> {

    OnAuthenticationFirstStepFinishedListener mListener = null;


    public AppCodeRequestListener(OnAuthenticationFirstStepFinishedListener context) {
        this.mListener = context;

    }

    @Override
    public void onRequestFailure(SpiceException e, ResearchRequestError error) {
        mListener.onAppCodeSendFailed();
    }

    @Override
    public void onRequestSuccess(final AppCodeResult result) {
       // Toast.makeText(this.mListener.getBaseActivity(), "AppCode SENT", Toast.LENGTH_SHORT).show();
        if (result != null && result.isResult()) {
            mListener.onAppCodeSent();
        } else {
            mListener.onAppCodeSendFailed();
        }
    }


    public interface OnAuthenticationFirstStepFinishedListener {
        public void onAppCodeSent();
        public void onAppCodeSendFailed();
    }


}
