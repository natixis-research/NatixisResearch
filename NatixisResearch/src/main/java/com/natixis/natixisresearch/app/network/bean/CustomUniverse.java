package com.natixis.natixisresearch.app.network.bean;

import android.content.Context;
import android.os.Parcel;

/**
 * Created by Thibaud on 21/04/2017.
 */
public class CustomUniverse extends ResearchUniverse{
    public static final int UNIVERSE_ALL_PUBLICATIONS=0;
    public static final int UNIVERSE_FAVORITES=1;
    public static final int UNIVERSE_DOWNLOADS=2;
    public static final int UNIVERSE_SEARCH=3;
    public static final int UNIVERSE_VIDEOS=4;
    public static final int UNIVERSE_DOWNLOADSFAVORITES=5;


    ResearchUniverse childUniverse=null;
    int mType;
    int mTitleResource;


    public CustomUniverse(Parcel in) {
        super(in);
        childUniverse = in.readParcelable(ResearchUniverse.class.getClassLoader());
        mType=in.readInt();
        mTitleResource=in.readInt();
      //  setUniverseId("custom_"+mType);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(childUniverse,i);
        parcel.writeInt(mType);
        parcel.writeInt(mTitleResource);

    }

    public ResearchUniverse getChildUniverse() {
        return childUniverse;
    }

    public void setChildUniverse(ResearchUniverse childUniverse) {
        this.childUniverse = childUniverse;
    }

    public CustomUniverse(  int type, int title){
        mType=type;
        mTitleResource=title;

        if(type==UNIVERSE_ALL_PUBLICATIONS) {
            setUniverseId("0");
        }
    }
    public CustomUniverse(  int type, String title){
        mType=type;
        setTitle(title);

        if(type==UNIVERSE_ALL_PUBLICATIONS) {
            setUniverseId("0");
        }
    }
    @Override
    public String getTitle(Context c) {
        if(mTitleResource!=0) {
            return c.getString(mTitleResource);
        }
        else {
            return super.getTitle(c);
        }
    }

    @Override
    public boolean supportPagination() {
        if(getType()==UNIVERSE_DOWNLOADS || getType()==UNIVERSE_FAVORITES || getType()==UNIVERSE_DOWNLOADSFAVORITES){
            return false;
        }
        return super.supportPagination();
    }

    public int getType(){
        return mType;
    }

    @Override
    public String getUniverseId() {
        if(childUniverse!=null){
            return childUniverse.getUniverseId();
        }
        return "0";
    }

    @Override
    public boolean canBeFavoriteUniverse() {
        return false;
    }


}
