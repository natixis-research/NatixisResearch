package com.natixis.natixisresearch.app.activity.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;

import java.util.List;

/**
 * Created by Thibaud on 30/04/2017.
 */
public class UniverseSpinnerAdapter extends ArrayAdapter<ResearchUniverse>{

    private Context context;
    private List<ResearchUniverse> itemList;
    public UniverseSpinnerAdapter(Context context, int resource, List<ResearchUniverse> items) {
        super(context, resource, items);
        this.context=context;
        this.itemList=items;
    }
    public void refreshUniverses(List<ResearchUniverse> items){

        this.itemList=items;
        this.clear();
        this.addAll(itemList);
    }

/*
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(yourRowlayout, parent,
                false);
        TextView make = (TextView) row.findViewById(R.id.Make);
        Typeface myTypeFace = Typeface.createFromAsset(context.getAssets(),
                "fonts/gilsanslight.otf");
        v.setTypeface(myTypeFace);
        v.setText(itemList.get(position));
        return row;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = getLayoutInflater();
        View row = inflater.inflate(yourRowlayout, parent,
                false);
        TextView make = (TextView) row.findViewById(R.id.Make);
        Typeface myTypeFace = Typeface.createFromAsset(context.getAssets(),
                "fonts/gilsanslight.otf");
        v.setTypeface(myTypeFace);
        v.setText(itemList.get(position));
        return row;
    }*/
}
