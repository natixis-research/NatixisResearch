package com.natixis.natixisresearch.app.network.listener;

import com.natixis.natixisresearch.app.activity.fragment.ConnectionFragment;
import com.natixis.natixisresearch.app.network.bean.User;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.octo.android.robospice.persistence.exception.SpiceException;

/**
 * Created by Thibaud on 14/04/2017.
 */
public class LoginRequestListener extends BaseRequestListener<User> {

    ConnectionFragment mContext = null;


    public LoginRequestListener(ConnectionFragment context) {
        this.mContext = context;

    }

    @Override
    public void onRequestFailure(SpiceException e, ResearchRequestError error) {
        mContext.onAuthenticationFailed(error);
    }

    @Override
    public void onRequestSuccess(final User result) {

        if (result != null && result.getId()>0) {
            mContext.onAuthenticationSuccess(result);
        } else {
            mContext.onAuthenticationFailed(null);
        }
    }


    public interface OnAuthenticationFinishedListener {
        public void onAuthenticationSuccess(User user);
        public void onAuthenticationFailed(ResearchRequestError error);
    }


}
