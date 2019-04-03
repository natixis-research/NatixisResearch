package com.natixis.natixisresearch.app.activity.fragment;

import com.natixis.natixisresearch.app.network.bean.ResearchNode;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;

/**
 * Created by Thibaud on 14/04/2017.
 */
public interface IUniverseClickListener {
    public void onChildUniverseClick(ResearchUniverse universe);
}
