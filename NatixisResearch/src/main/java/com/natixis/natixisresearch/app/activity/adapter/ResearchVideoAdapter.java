package com.natixis.natixisresearch.app.activity.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.bean.ResearchVideo;
import com.natixis.natixisresearch.app.network.bean.VideosList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.util.List;

public class ResearchVideoAdapter extends ArrayAdapter<ResearchVideo> {


    ResearchUniverse mUniverse;
    DisplayImageOptions dispOptions=null;

    public void addMoreVideos(ResearchUniverse universe, VideosList result) {
        mUniverse = universe;
        mVideos.addAll(result);
    }

    static class ViewHolder {
        TextView title;
        TextView date;
        TextView duration;
        TextView description;
        ImageView vignette;
    }
    private List<ResearchVideo> mVideos=null;


    public ResearchVideoAdapter(Context context, int resource, List<ResearchVideo> items) {
        super(context,resource, items);
        mVideos=items;
    Bitmap mask = BitmapFactory.decodeResource(context.getResources(),R.drawable.masque_video);
        dispOptions = new DisplayImageOptions.Builder()

                .resetViewBeforeLoading(true)  // default
                .delayBeforeLoading(0)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(false)
                .build();
    }
    public void clearVideos() {
        mVideos.clear();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_research_video, null);

            holder = new ViewHolder();
            holder.vignette = (ImageView)  v.findViewById(R.id.imageview_video);
            holder.title = (TextView)  v.findViewById(R.id.tvTitle);
            holder.duration = (TextView)  v.findViewById(R.id.tvDuration);
            holder.description = (TextView)  v.findViewById(R.id.tvDescription);
            holder.date = (TextView) v.findViewById(R.id.tvDate);
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }

        ResearchVideo video = getItem(position);
        if (video != null) {
            if(video.getImageLink()!=null && video.getImageLink().length()>0) {

                ImageLoader.getInstance().displayImage(video.getImageLink(), holder.vignette,dispOptions);

            }

            holder.title.setText(video.getTitle());
            //holder.duration.setText(video.getComments());
            holder.description.setText(video.getDescription());

            /*
            String date = video.getFormattedDateSmall();
            if(date!=null && date.length()>0) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(date);
            }
            else{
                holder.date.setVisibility(View.GONE);
            }*/


        }

        return v;
    }

}
