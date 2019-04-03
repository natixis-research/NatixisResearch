package com.natixis.natixisresearch.app.activity.adapter.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Thibaud on 23/04/2017.
 */
public class DocumentHolder {
    public final static int MENUITEM_FAVORIS = 0;
    public final static int MENUITEM_DOWNLOAD = 1;
    public final static int MENUITEM_SHARE = 2;
    private final TextView tvTitle;
    private final TextView tvCategory;
    private final TextView tvTimeSince;
    private final ImageView ivTimeCircle;
    private final ImageView ivTrash;
    private final SwipeLayout swipeLayout;
    ImageButton btDocInfos;
    ImageButton btSendMail;
    /*  private final ImageButton btDownload;
      private final ImageButton btFavorite;
      private final ImageButton btShare;*/
    //private final ImageButton btMore;
    private final ProgressBar progressDownload;
    // SatelliteMenu menu;
    private final View lineView;
    private ResearchDocument document;

    public DocumentHolder(Context c, View v) {
        tvTitle = (TextView) v.findViewById(R.id.tv_title_document);
        //tvDescription = (TextView) v.findViewById(R.id.tv_description_document);
        tvCategory = (TextView) v.findViewById(R.id.tv_categorie);
        //tvDate = (TextView) v.findViewById(R.id.tv_date);
        tvTimeSince = (TextView) v.findViewById(R.id.tv_time);

        ivTimeCircle = (ImageView) v.findViewById(R.id.circle_time);

        btDocInfos = (ImageButton) v.findViewById(R.id.bt_document_info);
        btDocInfos.setFocusable(false);

        btSendMail = (ImageButton) v.findViewById(R.id.bt_document_mail);
        btSendMail.setFocusable(false);
        // bt.setFocusableInTouchMode(false);
        ivTrash = (ImageView) v.findViewById(R.id.trash);

        swipeLayout = (SwipeLayout) v.findViewById(R.id.swipe_layout);

        //  btMore = (ImageButton) v.findViewById(R.id.bt_more);
        //  if (btMore != null) btMore.setFocusable(false);
     /*   btDownload= (ImageButton) v.findViewById(R.id.bt_download);
        if(btDownload!=null)btDownload.setFocusable(false);
        btFavorite= (ImageButton) v.findViewById(R.id.bt_favorite);
        if(btFavorite!=null)btFavorite.setFocusable(false);
        btShare= (ImageButton) v.findViewById(R.id.bt_share);
        if(btShare!=null)btShare.setFocusable(false);*/
        progressDownload = (ProgressBar) v.findViewById(R.id.progress_download_document);
        lineView = v.findViewById(R.id.line_timeline);

    }


    public SwipeLayout getSwipeLayout() {
        return swipeLayout;
    }

    public View getLine() {
        return lineView;
    }

    public TextView getTitle() {
        return tvTitle;
    }

    public TextView getTimeSince() {
        return tvTimeSince;
    }

    public ImageView getCircleTime() {
        return ivTimeCircle;
    }


    public TextView getCategory() {
        return tvCategory;
    }

    public ImageButton getBtDocInfos() {
        return btDocInfos;
    }

    public ImageButton getBtSendMail() {
        return btSendMail;
    }


    /*
    public ImageButton getBtShare() {
        return btShare;
    }

    public ImageButton getBtDownload() {
        return btDownload;
    }

    public ImageButton getBtFavorite() {
        return btFavorite;
    }


    public ImageButton getBtMore() {
        return btMore;
    }
*/
    public ProgressBar getProgressDownload() {
        return progressDownload;
    }


    public void setDocument(ResearchDocument document) {
        this.document = document;
    }

    public ResearchDocument getDocument() {
        return document;
    }

    /*
        public void replaceFavoriteButton(int resource) {

               getMenu().replaceItemResource(MENUITEM_FAVORIS, resource);

        }

        public void replaceDownloadButton(int resource) {
            getMenu().replaceItemResource(MENUITEM_DOWNLOAD, resource);
        }
    */
   /* public void markDocInCache(boolean inCache) {
        if (inCache) {
            getMenu().replaceItemResource(DocumentHolder.MENUITEM_DOWNLOAD, R.drawable.ic_succeed);
        } else {
            getMenu().replaceItemResource(DocumentHolder.MENUITEM_DOWNLOAD, R.drawable.bt_arc_downloads);
        }
    }*/
    public void changeDocTitleAppearance(Context c, boolean inFavorite, boolean isRead, boolean shareDocEnabled) {

        Date date = document.getDatetime();
        Calendar cal = Calendar.getInstance();
        //cal.set(Calendar.)
        cal.add(Calendar.DAY_OF_MONTH, -1);

        if (shareDocEnabled) {
            getBtDocInfos().setVisibility(View.GONE);
            getBtSendMail().setVisibility(View.VISIBLE);
        } else {

            getBtSendMail().setVisibility(View.GONE);
            getBtDocInfos().setVisibility(View.VISIBLE);
        }

        if (inFavorite) {
            getCircleTime().setImageResource(R.drawable.circle_purple);
            getTimeSince().setTextColor(c.getResources().getColor(R.color.circle_purple_text));
            getBtDocInfos().setImageResource(R.drawable.panette_violet);
        } else if (date != null && date.before(cal.getTime())) {
            getCircleTime().setImageResource(R.drawable.circle_gray);
            getTimeSince().setTextColor(c.getResources().getColor(R.color.circle_gray_text));
            getBtDocInfos().setImageResource(R.drawable.panette_gris);
        } else {
            getCircleTime().setImageResource(R.drawable.circle_blue);
            getTimeSince().setTextColor(c.getResources().getColor(R.color.circle_blue_text));
            getBtDocInfos().setImageResource(R.drawable.panette_bleu);
        }

        if (isRead) {
            getTitle().setTextColor(c.getResources().getColor(R.color.document_title_color));


        } else {
            getTitle().setTextColor(Color.BLACK);
        }

    }

    public void setDownloadProgress(boolean visible) {
        if (visible) {
            getProgressDownload().setVisibility(View.VISIBLE);
            getBtDocInfos().setVisibility(View.INVISIBLE);
        } else {

            getProgressDownload().setVisibility(View.GONE);
            getBtDocInfos().setVisibility(View.VISIBLE);

        }
    }

    public ImageView getTrashButton() {
        return ivTrash;
    }
}
