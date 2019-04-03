package com.natixis.natixisresearch.app.activity.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.BaseActivity;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


/**
 * Created by Thibaud on 14/04/2017.
 */
public class ImageSlideAdapter extends PagerAdapter {

    BaseActivity mContext;
    ResearchDocumentResultList mDocuments;
    ArrayList<Integer> backgroundDrawable = new ArrayList<Integer>();
    int mMaxItems = 0;
    DisplayImageOptions dispOptions;
    public ImageSlideAdapter(BaseActivity context, int maxItems) {
        this.mContext = context;
        this.mMaxItems = maxItems;
/*
        backgroundDrawable.add(R.drawable.bandeau1);
        backgroundDrawable.add(R.drawable.bandeau2);
        backgroundDrawable.add(R.drawable.bandeau3);
        on vire ça*/

        dispOptions = new DisplayImageOptions.Builder()

                .resetViewBeforeLoading(true)  // default
                .delayBeforeLoading(0)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(false)
                .build();
    }

    public void setDocuments(ResearchDocumentResultList documents) {
        mDocuments = documents;

    }

    @Override
    public int getCount() {
        if (mDocuments != null) {
            return Math.max(mDocuments.size(), mMaxItems);
        } else {
            return 1;
        }
    }


    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_document_slider, container, false);

        ImageView imageView = (ImageView) view
                .findViewById(R.id.iv_carousel);
       /* int resPos = position % backgroundDrawable.size();
        mImageView.setImageResource(backgroundDrawable.get(resPos));
ca degage
*/

        TextView title = (TextView) view
                .findViewById(R.id.tv_title_document);

        if (mDocuments!=null && mDocuments.size() > position) {
            final ResearchDocument doc = mDocuments.get(position);

            if (view.getTag() == null) {
                view.setTag(doc);
            }

            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ResearchDocumentAdapter.onClickDocumentOpen(mContext, doc);
                }
            });


            title.setText(doc.getTitle());
            String imageUrl = NatixisResearchApp.getInstance().getImageUrl(doc.getFilename());
            if(imageUrl!=null && imageUrl.length()>0) {

                ImageLoader.getInstance().displayImage(imageUrl, imageView,dispOptions);

            }

        }else{
            title.setVisibility(View.GONE);
        }


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
