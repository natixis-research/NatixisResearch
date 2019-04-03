package com.natixis.natixisresearch.app.network.bean;

import android.content.Context;
import android.os.Parcel;

/**
 * Created by Thibaud on 30/04/2017.
 */
public class SearchUniverse extends CustomUniverse {
    boolean mExtendResearch;

    public boolean isExtendResearch() {
        return mExtendResearch;
    }

    public void setExtendResearch(boolean mExtendResearch) {
        this.mExtendResearch = mExtendResearch;
    }

    public SearchUniverse(String text, boolean extendResearch) {
        super(CustomUniverse.UNIVERSE_SEARCH, text);
        this.mExtendResearch = extendResearch;
    }

    @Override
    public String getTitle(Context c) {
        return super.getTitle(c);
    }

    public SearchUniverse(Parcel in){
        super(in);
        mExtendResearch=in.readByte()!=0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeByte((byte) (mExtendResearch?1:0));
    }
}
