package com.natixis.natixisresearch.app.activity.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.BaseActivity;
import com.natixis.natixisresearch.app.activity.DownloadDocumentActivity;
import com.natixis.natixisresearch.app.activity.adapter.holder.DocumentHolder;
import com.natixis.natixisresearch.app.network.bean.CustomUniverse;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.generic.NatixisObjectMapper;
import com.natixis.natixisresearch.app.network.listener.DocumentRequestListener;
import com.natixis.natixisresearch.app.network.request.DocumentRequest;
import com.natixis.natixisresearch.app.utils.Utils;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Thibaud on 12/04/2017.
 */
public class ResearchDocumentAdapter extends BaseSwipeAdapter implements AdapterView.OnItemClickListener, ITimelineItemAdapter {

    private BaseActivity mContext;
    ArrayList<ResearchDocument> mDocuments;
    ResearchUniverse mUniverse;
    ContextMenuAdapter contextMenuAdapter;
    boolean mAllowDelete=true;
    MotionEvent lastTouchEvent;
    int lastTouchX = 0;
    int lastTouchY = 0;
    private boolean swipeDeleteEnabled=false;
    private boolean shareDocEnabled=false;

    public ResearchDocumentAdapter(BaseActivity c, ArrayList<ResearchDocument> documents) {
        mContext = c;
        mDocuments = documents;
    }

    public void setContextMenu(ContextMenuAdapter contextMenu) {
        contextMenuAdapter = contextMenu;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mDocuments.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        if (mDocuments.size() > position) {
            return mDocuments.get(position);
        } else return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    //https://github.com/daimajia/AndroidSwipeLayout/wiki/SwipeAdapter
    @Override
    public View generateView(int position, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_research_document_timeline_deletable, null);
        DocumentHolder docHolder = new DocumentHolder(mContext, v);

        v.setTag(docHolder);

        return v;

    }

    @Override
    public void fillValues(final int position, View convertView) {
        View v = convertView;
        final DocumentHolder docHolder= (DocumentHolder) v.getTag();


        docHolder.getSwipeLayout().setSwipeEnabled(swipeDeleteEnabled);

        final ResearchDocument doc = (ResearchDocument) getItem(position);
        if (doc != null) {
            try {
                docHolder.setDocument(doc);
                docHolder.getTitle().setText(doc.getTitle());

                if (doc.getDocumentType() != null) {
                    docHolder.getCategory().setText(doc.getDocumentType().getTitle());
                    docHolder.getCategory().setVisibility(View.VISIBLE);
                } else {
                    docHolder.getCategory().setVisibility(View.GONE);
                    docHolder.getCategory().setText("");
                }
                docHolder.getLine().setMinimumHeight(v.getMeasuredHeight());
                if (position == 0) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) docHolder.getLine().getLayoutParams();
                    params.addRule(RelativeLayout.BELOW, R.id.circle_time);
                    if (android.os.Build.VERSION.SDK_INT >= 17) {
                        params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                    }

                    docHolder.getLine().setLayoutParams(params);
                } else {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) docHolder.getLine().getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    if (android.os.Build.VERSION.SDK_INT >= 17) {
                        params.removeRule(RelativeLayout.BELOW);
                    }

                    docHolder.getLine().setLayoutParams(params);
                }

                docHolder.getTimeSince().setText(doc.getTimeSince(mContext));

                boolean inFavorites = getApp().getFavoritesCache().isInFavorites(doc);
                boolean isRead = getApp().getReadCache().isRead(doc);

                docHolder.changeDocTitleAppearance(mContext, inFavorites, isRead, shareDocEnabled);


                docHolder.getBtDocInfos().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // openContextMenu(docHolder);
                        onClickDocumentFavorite(docHolder);
                    }
                });
                docHolder.getTrashButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // openContextMenu(docHolder);
                        onClickDocumentTrash(docHolder,position);
                    }
                });

                docHolder.getBtSendMail().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // openContextMenu(docHolder);
                        onClickDocumentShare(docHolder);
                    }
                });
                //PINTEREST MENU REMOVED ON V2 01/05/2017
                /*
                docHolder.getBtDocInfos().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(contextMenuAdapter!=null){
                            contextMenuAdapter.show(lastTouchX,lastTouchY, docHolder,lastTouchEvent);
                        }
                        return true;
                    }
                });

                docHolder.getBtDocInfos().setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        //used into long click
                        lastTouchEvent=motionEvent;
                        lastTouchX=(int)motionEvent.getRawX();
                        lastTouchY=(int)motionEvent.getRawY();
                       int[] positions = new int[2];
                        view.getLocationOnScreen(positions);
                        lastTouchX=positions[0]+view.getWidth()/2;
                        lastTouchY=positions[1]+view.getHeight()/2;


                        if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                        {
                            contextMenuAdapter.hide();
                          //  return true;
                        }
                        return false;
                    }
                });
                */
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void openContextMenu(final DocumentHolder doc) {
        String favoris = "";
        int favorisResource = 0;
        if (getApp().getFavoritesCache().isInFavorites(doc.getDocument())) {
            favoris = mContext.getString(R.string.remove_from_favorites);
            favorisResource = R.drawable.ic_favorite_on;
        } else {
            favoris = mContext.getString(R.string.add_to_favorites);
            favorisResource = R.drawable.bt_arc_favorites;
        }
        String download = "";
        int downloadResource = 0;
        if (getApp().getDocumentCache().isInCache(doc.getDocument())) {
            download = mContext.getString(R.string.remove_from_downloads);

            downloadResource = R.drawable.ic_succeed;
        } else {
            download = mContext.getString(R.string.download_document);

            downloadResource = R.drawable.bt_arc_downloads;
        }

        String share = mContext.getString(R.string.share_doc_by_mail);
        String open = mContext.getString(R.string.open_document);

        int shareResource = R.drawable.bt_arc_share;
        final String[] items = {favoris, download, share, open};


        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.what_you_want_to_do_with_doc));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    onClickDocumentFavorite(doc);
                } else if (item == 1) {
                    onClickDocumentDownload(doc);
                } else if (item == 2) {
                    onClickDocumentShare(doc);
                } else if (item == 3) {
                    onClickDocumentOpen(mContext, doc.getDocument());
                }
            }
        });
        AlertDialog alert = builder.create();

        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    public static void onClickDocumentOpen(BaseActivity c, ResearchDocument doc) {
        if (doc != null) {
            if (c.getNatixisApp().getDocumentCache().isInCache(doc)) {
                Utils.openDocument(c, doc);
            } else {
                Intent i = new Intent(c, DownloadDocumentActivity.class);
                NatixisObjectMapper mapper = new NatixisObjectMapper();
                try {
                    String docJson = mapper.writeValueAsString(doc);
                    i.putExtra(DownloadDocumentActivity.JSON_DOCUMENT, docJson);
                    // mContext.startActivityForResult(i,TimelineFragment.REQUEST_DOWNLOAD);
                    c.startActivity(i);

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void onClickDocumentDownload(final DocumentHolder docHolder) {
        final ResearchDocument doc = docHolder.getDocument();
        if (!getApp().getDocumentCache().isInCache(doc)) {
            docHolder.setDownloadProgress(true);
            requestDocument(doc, docHolder, false);

            Toast.makeText(mContext, mContext.getString(R.string.download_in_progress), Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(mContext.getString(R.string.are_you_sure_you_want_to_delete_this_file)).setTitle(mContext.getString(R.string.file_deletion));
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getApp().getDocumentCache().removeDocumentFromCache(doc);
                    //docHolder.markDocInCache(false);
                    Toast.makeText(mContext, mContext.getString(R.string.doc_removed_from_cache), Toast.LENGTH_SHORT).show();
                    if (mUniverse != null && mUniverse instanceof CustomUniverse) {
                        if (((CustomUniverse) mUniverse).getType() == CustomUniverse.UNIVERSE_DOWNLOADS) {
                            mDocuments.remove(doc);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    protected void requestDocument(ResearchDocument document, final DocumentHolder docHolder, boolean open) {

        DocumentRequestListener listener = new DocumentRequestListener(mContext, document, open) {
            @Override
            public void onRequestSuccess(InputStream result) {
                super.onRequestSuccess(result);
                docHolder.setDownloadProgress(false);
                Toast.makeText(mContext, mContext.getString(R.string.document_downloaded), Toast.LENGTH_SHORT).show();

                // docHolder.markDocInCache(true);

                /*
                docHolder.getBtDownload().setVisibility(View.VISIBLE);
                docHolder.getBtDownload().setBackgroundResource(R.drawable.ic_succeed);*/
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                super.onRequestFailure(spiceException);
                docHolder.setDownloadProgress(false);
                //  docHolder.markDocInCache(false);
            }
        };

        if (getApp().getDocumentCache().isInCache(document)) {
            listener.onRequestSuccess(null);
        } else {
            DocumentRequest request = new DocumentRequest(document);
            mContext.getSpiceManager().execute(request, document.getFilename(), DurationInMillis.ALWAYS_RETURNED, listener);
        }

    }

    public void onClickDocumentTrash(DocumentHolder docHolder, int position) {
        ResearchDocument doc = docHolder.getDocument();
        docHolder.getSwipeLayout().close();
        getApp().getFavoritesCache().removeDocumentFromFavorites(doc);
        mDocuments.remove(position);
        notifyDataSetChanged();
    }

    public void onClickDocumentFavorite(DocumentHolder docHolder) {
        ResearchDocument doc = docHolder.getDocument();
        boolean read = getApp().getReadCache().isRead(doc);
        if (!getApp().getFavoritesCache().isInFavorites(doc)) {
            getApp().getFavoritesCache().addDocumentToFavorites(doc);

            docHolder.changeDocTitleAppearance(mContext, true, read,shareDocEnabled);

          if (!getApp().getDocumentCache().isInCache(doc)) {

                docHolder.setDownloadProgress(true);
                requestDocument(doc, docHolder, false);
          }

        }
    }

    public NatixisResearchApp getApp() {
        return (NatixisResearchApp) mContext.getApplicationContext();
    }

    public void onClickDocumentShare(DocumentHolder docHolder) {
        Utils.sendDocument(mContext, docHolder.getDocument());
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DocumentHolder docHolder = (DocumentHolder) view.getTag();
        if (docHolder == null) return;
       /* if (docHolder.getMenu().isExpanded()) {
            docHolder.getMenu().closeMenu(false);
        }*/
        position -= 1; //Remove listview header item
        ResearchDocument doc = (ResearchDocument) getItem(position);
        onClickDocumentOpen(mContext, doc);
    }

    public void addMoreDocuments(ResearchUniverse universe, ResearchDocumentResultList result) {
        mUniverse = universe;
        mDocuments.addAll(result);
    }

    public void clearDocuments() {
        mDocuments.clear();
    }

    public void setSwipeDeleteEnabled(boolean swipeDeleteEnabled) {
        this.swipeDeleteEnabled = swipeDeleteEnabled;
    }

    public void setDocumentShareEnabled(boolean shareDocEnabled) {
        this.shareDocEnabled = shareDocEnabled;
    }

    public void removeDocument(ResearchDocument doc){
        mDocuments.remove(doc);
    }

/*
    private void initArcMenu(final Context c, final DocumentHolder docHolder, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageButton item = new ImageButton(c);
            item.setImageResource(itemDrawables[i]);
            item.setTag(i);
            item.setBackground(null);
            final int position = i;


            if (position == DocumentHolder.MENUITEM_FAVORIS) {
                docHolder.getMenu().addItem(item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(c, "Favoris:" + position, Toast.LENGTH_SHORT).show();
                        onClickDocumentFavorite(docHolder);


                    }
                });
            } else if (position == DocumentHolder.MENUITEM_DOWNLOAD) {
                docHolder.getMenu().addItem(item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(c, "Download:" + position, Toast.LENGTH_SHORT).show();
                        onClickDocumentDownload(docHolder);
                    }
                });
            } else if (position == DocumentHolder.MENUITEM_SHARE) {
                docHolder.getMenu().addItem(item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(c, "Download:" + position, Toast.LENGTH_SHORT).show();
                        onClickDocumentShare(docHolder);
                    }
                });
            }
        }
    }*/
}
