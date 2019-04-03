package com.natixis.natixisresearch.app.activity.fragment;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.activity.BaseActivity;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by Thibaud on 16/04/2017.
 */
public class BaseFragment extends Fragment {



    public BaseActivity getBaseActivity(){
        return (BaseActivity) getActivity();
    }

    public SpiceManager getSpiceManager(){
        return getBaseActivity().getSpiceManager();
    }

    public NatixisResearchApp getNatixisApp(){
        return getBaseActivity().getNatixisApp();
    }
}
