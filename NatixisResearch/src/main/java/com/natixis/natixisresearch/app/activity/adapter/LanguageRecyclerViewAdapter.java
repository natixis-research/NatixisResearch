package com.natixis.natixisresearch.app.activity.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.fragment.ParameterLanguageFragment.OnListFragmentInteractionListener;

import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Locale} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class LanguageRecyclerViewAdapter extends RecyclerView.Adapter<LanguageRecyclerViewAdapter.ViewHolder> {

    private final List<Locale> mValues;
    private final OnListFragmentInteractionListener mListener;
    Context mContext = null;
    Locale mCurrentLocale =null;

    public LanguageRecyclerViewAdapter(Context context, List<Locale> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mContext = context;
        mCurrentLocale =mContext.getResources().getConfiguration().locale;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fragment_language, parent, false);
        ViewHolder holder= new ViewHolder(view);
        holder.ivCheck.setColorFilter(mContext.getResources().getColor(R.color.natixis_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        StringBuilder displayLanguage = new StringBuilder( holder.mItem.getDisplayLanguage( holder.mItem).toLowerCase());
        displayLanguage.setCharAt(0, Character.toUpperCase(displayLanguage.charAt(0)));


        holder.tvLanguage.setText(displayLanguage.toString());
        String flagName = "flag_"+holder.mItem.getCountry().toLowerCase();
        int flagId = mContext.getResources().getIdentifier(flagName,"drawable", mContext.getPackageName());

        holder.tvLanguage.setCompoundDrawablesWithIntrinsicBounds(flagId,0,0,0);

        if( mCurrentLocale.getLanguage().equalsIgnoreCase(holder.mItem.getLanguage())){
            holder.ivCheck.setVisibility(View.VISIBLE);
        }else{
            holder.ivCheck.setVisibility(View.INVISIBLE);

        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListLanguageInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvLanguage;
        public final ImageView ivCheck;
        public Locale mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvLanguage = (TextView) view.findViewById(R.id.tv_language);
            ivCheck = (ImageView) view.findViewById(R.id.check);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvLanguage.getText() + "'";
        }
    }
}
