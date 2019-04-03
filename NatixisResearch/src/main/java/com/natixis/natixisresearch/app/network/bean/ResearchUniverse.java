package com.natixis.natixisresearch.app.network.bean;

/**
 * Created by Thibaud on 06/04/2017.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.natixis.natixisresearch.app.R;

import java.util.ArrayList;


public class ResearchUniverse implements Parcelable {
    @JsonProperty("Id")
    private String universeId;

    @JsonProperty("Text")
    private String title;


    @JsonProperty("ImageId")
    private int imageId;

    @JsonProperty("Universes")
    private ArrayList<ResearchUniverse> universes = new ArrayList<ResearchUniverse>();

    public ResearchUniverse() {

    }

    public ResearchUniverse(Parcel in) {
        universeId = in.readString();
        title = in.readString();
        imageId = in.readInt();
        in.readTypedList(universes, ResearchUniverse.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(universeId);
        parcel.writeString(title);
        parcel.writeInt(imageId);
        parcel.writeTypedList(universes);
    }

    public static final Parcelable.Creator<ResearchUniverse> CREATOR = new Parcelable.Creator<ResearchUniverse>() {
        public ResearchUniverse createFromParcel(Parcel s) {
            return new ResearchUniverse(s);
        }

        public ResearchUniverse[] newArray(int size) {
            return new ResearchUniverse[size];
        }
    };

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }


    public String getUniverseId() {
        return universeId;
    }

    public void setUniverseId(String universeId) {
        this.universeId = universeId;
    }

    public String getTitle(Context context) {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getResource(Context c) {
        return c.getResources().getDrawable(R.drawable.ic_contact);
    }

    public ArrayList<ResearchUniverse> getSubUniverses() {
        if (universes != null) {
            return universes;
        } else {
            return new ArrayList<ResearchUniverse>();
        }
    }

    public boolean supportPagination() {
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    // cal.getTime().tot

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ResearchUniverse && ((ResearchUniverse) o).getUniverseId() != null && getUniverseId() != null) {
            return getUniverseId().equalsIgnoreCase(((ResearchUniverse) o).getUniverseId());
        }
        return super.equals(o);
    }

public boolean canBeFavoriteUniverse(){
    return true;
}
    /*
    public Boolean isDocumentDownloaded(SpiceManager cache) {
        try {
            Future<Boolean> dataInCacheFuture = cache.isDataInCache(ResearchDocument.class, this.getFilename(), DurationInMillis.ALWAYS_RETURNED);
            final Boolean dataInCache = dataInCacheFuture.get();
            return dataInCache;
        } catch (CacheCreationException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }*/
}
