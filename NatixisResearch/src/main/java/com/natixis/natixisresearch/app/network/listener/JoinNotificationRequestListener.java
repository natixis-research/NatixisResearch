package com.natixis.natixisresearch.app.network.listener;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.activity.BaseActivity;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.utils.Utils;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.InputStream;

/**
 * Created by Thibaud on 14/04/2017.
 */
public class JoinNotificationRequestListener extends BaseRequestListener<String> implements RequestListener<String> {


    Context mContext = null;
String mToken=null;

    public JoinNotificationRequestListener(Context context, String token , boolean open) {
        this.mContext = context;
        mToken=token;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException, ResearchRequestError error) {
        Log.d("NATIXIS", "error");
    }

    @Override
    public void onRequestSuccess(String result) {

    }


}
