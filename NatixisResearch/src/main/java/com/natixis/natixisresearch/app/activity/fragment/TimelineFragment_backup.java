package com.natixis.natixisresearch.app.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.adapter.ResearchDocumentAdapter;
import com.natixis.natixisresearch.app.network.bean.CustomUniverse;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.bean.SearchUniverse;
import com.natixis.natixisresearch.app.network.listener.TimelineRequestListener;
import com.natixis.natixisresearch.app.network.request.RetryPolicyNone;
import com.natixis.natixisresearch.app.network.request.SearchDocumentRequest;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.natixis.natixisresearch.app.utils.Utils;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Thibaud on 22/04/2017.
 */
public class TimelineFragment_backup extends BaseFragment implements  View.OnTouchListener {
    public final static int REQUEST_DOWNLOAD = 5;
    private final int AUTOLOAD_THRESHOLD = 4;
    private final int NB_ITEM_PER_PAGE = 10;

    int currentLoadingPage = 0;
    int lastLoadedPage = 0;
    ListView listviewDocument;
    ResearchDocumentAdapter adapter = null;
    private ProgressBar progress;
    SwipeRefreshLayout refreshLayout;
    SwipeRefreshLayout refreshLayoutEmpty;
    LinearLayout ergoLayout;
    LinearLayout header;
    RelativeLayout layoutParent;
    TextView title;
    CheckBox chkExtendSearch;
    View parent;
    int scrollPosition = 0;
    boolean viewCreated = false;
    boolean touched=false;
    ResearchUniverse mUniverse = null;
    boolean dataInCache = false;

    public void setUniverse(ResearchUniverse universe) {
        mUniverse = universe;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = inflater.inflate(R.layout.fragment_timeline, container, false);
        listviewDocument = (ListView) parent.findViewById(R.id.lv_documents);
        listviewDocument.setEmptyView(parent.findViewById(R.id.swipe_layout_empty));

        progress = (ProgressBar) parent.findViewById(R.id.progress_documents);
        header = (LinearLayout) parent.findViewById(R.id.layout_header);
        ergoLayout = (LinearLayout) parent.findViewById(R.id.layout_header_separator);
        ergoLayout.bringToFront();
        title = (TextView) parent.findViewById(R.id.big_title);
        layoutParent= (RelativeLayout) parent.findViewById(R.id.layout_parent);
        refreshLayout = (SwipeRefreshLayout) parent.findViewById(R.id.swipe_layout);
        refreshLayout.setColorScheme(R.color.line_theme,
                android.R.color.darker_gray,
                android.R.color.holo_blue_dark, R.color.bg_title);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadNewDocuments(mUniverse, 1, true);
            }
        });

        refreshLayoutEmpty = (SwipeRefreshLayout) parent.findViewById(R.id.swipe_layout_empty);
        refreshLayoutEmpty.setColorScheme(R.color.line_theme,
                android.R.color.darker_gray,
                android.R.color.holo_blue_dark, R.color.bg_title);
        refreshLayoutEmpty.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadNewDocuments(mUniverse, 1, true);
            }
        });

        Log.i("BGC", "TImeline fragment view created");

/*
        ViewTreeObserver vto = listviewVideos.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

            }
        });
        */

    /*    LayoutTransition transition = new LayoutTransition();
        transition.setDuration(600);
        Animator disappearingAnimation = ObjectAnimator.ofFloat(null, "translationY", 0, header.getMeasuredHeight() * -1);
        transition.setAnimator(LayoutTransition.DISAPPEARING,disappearingAnimation);

        layoutParent.setLayoutTransition(transition);*/
     /*   RelativeLayout layoutparent = (RelativeLayout) parent.findViewById(R.id.layout_parent);

        Animator appearingAnimation = ObjectAnimator.ofFloat(null, "translationY", header.getMeasuredHeight() * -1, 0);
        //appearingAnimation.setInterpolator(new DecelerateInterpolator());
        Animator disappearingAnimation = ObjectAnimator.ofFloat(null, "translationY", 0, header.getMeasuredHeight() * -1);

        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(1000);
        transition.setAnimator(LayoutTransition.DISAPPEARING,disappearingAnimation);
        transition.setAnimator(LayoutTransition.APPEARING,appearingAnimation);
      //  transition.enableTransitionType(LayoutTransition.DISAPPEARING);
        layoutparent.setLayoutTransition(transition);*/
/*
        transition.setStagger(LayoutTransition.CHANGE_APPEARING, 30);
        transition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 30);
        transition.setDuration(2000);
*/
        boolean touched=false;
        listviewDocument.setOnTouchListener(this);
        listviewDocument.setOnScrollListener(new AbsListView.OnScrollListener() {
            int lastFirstVisiblePos = 0;
            boolean animate = false;


            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                isScrollCompleted(i);
            }
            private void isScrollCompleted(int currentScrollState) {
                if (currentScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int currentFirstVisibleItem = listviewDocument.getFirstVisiblePosition();
                    if (currentFirstVisibleItem>=2 ) {
                        getBaseActivity().getActionBar().hide();

                        if (header.getVisibility() == View.VISIBLE) {

                            header.setVisibility(View.GONE);

                            justChanged=true;
                        }
                    } else if (currentFirstVisibleItem<4 ) {

                        getBaseActivity().getActionBar().show();
                        if (header.getVisibility() != View.VISIBLE) {

                            header.setVisibility(View.VISIBLE);
                            justChanged=true;
                        }
                    }
                }
            }
                boolean treatingScroll = false;
            boolean justChanged=false;
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {


                if (!treatingScroll && totalItemCount - AUTOLOAD_THRESHOLD <= firstVisibleItem + visibleItemCount) {
                    treatingScroll = true;
                    try {
                        if (currentLoadingPage == 0 && mUniverse != null && mUniverse.supportPagination()) {
                            // mutiple de NB_ITEM_PER_PAGE donc potentiellement d'autres elements dispo
                            if (totalItemCount > 0 && totalItemCount % NB_ITEM_PER_PAGE == 0) {
                                int lastPage = totalItemCount / NB_ITEM_PER_PAGE;
                                downloadNewDocuments(mUniverse, lastPage + 1, false);
                            }
                        }
                    } finally {
                        treatingScroll = false;
                    }


                }



                /*
                View c = listviewVideos.getChildAt(0);
                if (c != null){
                    scrollPosition = -c.getTop() + listviewVideos.getFirstVisiblePosition() * c.getHeight();
                }
               // header.setTop(  );

                header.offsetTopAndBottom(scrollPosition * -1);
                ergoLayout.offsetTopAndBottom(scrollPosition * -1);
               // header.setTop(scrollPosition * -1);
               // listviewVideos.offsetTopAndBottom(scrollPosition * -1);
                listviewVideos.setTop(scrollPosition * -1);
                parent.requestLayout();
*/

            }
        });


        adapter = new ResearchDocumentAdapter(getBaseActivity(), new ArrayList<ResearchDocument>());
        listviewDocument.setAdapter(adapter);
        listviewDocument.setOnItemClickListener(adapter);


        viewCreated = true;


        return parent;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("universe", mUniverse);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //probably orientation change
            mUniverse = (ResearchUniverse) savedInstanceState.getParcelable("universe");
        }

        // loadUniverse(mUniverse);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("BGC", "Starting TImeline fragment");
        if (adapter.getCount() == 0) {
            loadUniverse(mUniverse);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
      /* if (adapterFavorites != null)
            adapterFavorites.notifyDataSetChanged();*/
    }

    boolean progressBarCancelled = false;

    public void showProgressBar(boolean visible) {
        if (visible) {
            progressBarCancelled = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!progressBarCancelled) {
                        progress.setVisibility(View.VISIBLE);
                    }
                }
            }, 600);
        } else {
            progressBarCancelled = true;
            progress.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
            refreshLayoutEmpty.setRefreshing(false);
        }
    }

    private void downloadNewDocuments(ResearchUniverse universe, int page, boolean force) {

        // getSpiceManager().cancelAllRequests();
        currentLoadingPage = page;

        RequestLanguage lang = getBaseActivity().getResearchRequestLanguage();
        String token = null;
        if (getNatixisApp().isUserLogged()) {
            token = getNatixisApp().getLoggedUser().getToken();
        }

        showProgressBar(true);
        String filter = null;
        String universeId = universe != null ? universe.getUniverseId() : "0";

        if (universe instanceof SearchUniverse) {
            filter = universe.getTitle(getActivity());
            if (((SearchUniverse) universe).isExtendResearch()) {
                universeId = "0";
            }
        }


        SearchDocumentRequest request = new SearchDocumentRequest(token, lang, universeId, NB_ITEM_PER_PAGE, page, filter);
        request.setRetryPolicy(new RetryPolicyNone());

        String cacheKey = "search_" + lang + "_" + universeId;
        if (filter != null) {
            cacheKey += "_" + filter;
        }
        cacheKey += "_" + page;

        long expire = Utils.calculateMaxAge();
        if (page > 1 || force) {
            //If not page 1 mark always expirerd because it wil be cached manually into page 0 cachekey
            expire = DurationInMillis.ALWAYS_EXPIRED;

        }
        Log.i("BGC", "Executing request  : " + cacheKey);

        getSpiceManager().getFromCacheAndLoadFromNetworkIfExpired(request, cacheKey, expire, new InnerTimelineRequestListener(lang, cacheKey, page, force));

    }

    private void insertDocuments(ResearchDocumentResultList result) {
        if (result != null) {
            adapter.addMoreDocuments(mUniverse,result);
        }

        adapter.notifyDataSetChanged();
    }


    public void OnTimelineRetrieved(final ResearchDocumentResultList result, final String cacheKey, final int page, boolean forced) {

        Log.i("BGC", "Retrieved document list   : " + cacheKey + " (force:" + forced + ")");
        if (page > 1) {
            //If not page 1 we put documents into page 0 cache to be sure that all pages will be reloaded if page 0 is refreshed.
            int pos = cacheKey.lastIndexOf("_");
            String cacheKeyWithoutPage = cacheKey.substring(0, pos + 1);
            String cacheKeyFirstPage = cacheKeyWithoutPage + "1";
            ResearchDocumentResultList cached = null;
            try {

                Future<ResearchDocumentResultList> cachedFuture = getSpiceManager().getDataFromCache(ResearchDocumentResultList.class, cacheKeyFirstPage);
                cached = cachedFuture.get();
                if (cached != null && result != null) {
                    cached.addAll(result);
                    getSpiceManager().putDataInCache(cacheKeyFirstPage, cached);
                }
            } catch (CacheLoadingException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (CacheCreationException e) {
                e.printStackTrace();
            } catch (CacheSavingException e) {
                e.printStackTrace();
            }

        }
              /*  if (page == 1) {
                    adapterFavorites.clearDocuments();
                    if (cacheKey != null && lastLoadedPage > 1) {
                        int pos = cacheKey.lastIndexOf("_");
                        String cacheKeyWithoutPage = cacheKey.substring(0, pos + 1);
                        for (int i = 2; i <= lastLoadedPage; i++) {
                            getSpiceManager().removeDataFromCache(ResearchDocumentResultList.class, cacheKeyWithoutPage + i);
                        }

                    }
                }*/

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (page == 1) {
                    adapter.clearDocuments();
                }
                insertDocuments(result);
                showProgressBar(false);
                lastLoadedPage = adapter.getCount() / NB_ITEM_PER_PAGE;
                currentLoadingPage = 0;
            }
        });
    }


    public void OnTimelineDownloadFailed(SpiceException e, ResearchRequestError error, boolean forced) {

        Log.i("BGC", "Fail to retrieve document list   : " + e.getLocalizedMessage() + "(" + e.getClass().getName() + ") (force:" + forced + ")");
        showProgressBar(false);

        currentLoadingPage = 0;
      /*  if (((!dataInCache && e != null) || (forced)) && e instanceof NoNetworkException) {
            Toast.makeText(getBaseActivity(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        } else if (error == null && e != null) {
            Toast.makeText(getBaseActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } else if (error != null) {
            Toast.makeText(getBaseActivity(), error.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }*/

    }


    public void loadUniverse(ResearchUniverse universe) {
        Log.i("BGC", "Loading universe : " + universe.getTitle(getBaseActivity()) + " (" + universe.getUniverseId() + ")");

        ResearchUniverse previousUniverse = mUniverse;
        this.mUniverse = universe;

        if (viewCreated) {

            if (this.title != null) {
                this.title.setText(mUniverse.getTitle(getBaseActivity()));
            }

            adapter.clearDocuments();
            adapter.notifyDataSetChanged();
            refreshLayout.setEnabled(true);

            if (universe instanceof CustomUniverse) {
                if (((CustomUniverse) universe).getType() == CustomUniverse.UNIVERSE_DOWNLOADS) {
                    loadDownloadedDocuments();
                    refreshLayout.setEnabled(false);

                    return;
                } else if (((CustomUniverse) universe).getType() == CustomUniverse.UNIVERSE_FAVORITES) {
                    loadFavoritesDocuments();
                    refreshLayout.setEnabled(false);
                    return;
                } else if (((CustomUniverse) universe).getType() == CustomUniverse.UNIVERSE_SEARCH) {
                    ResearchUniverse childUniverse = null;
                    if (previousUniverse instanceof CustomUniverse && ((CustomUniverse) previousUniverse).getType() == CustomUniverse.UNIVERSE_SEARCH) {
                        childUniverse = ((CustomUniverse) previousUniverse).getChildUniverse();
                    } else if (!(childUniverse instanceof CustomUniverse)) {
                        childUniverse = previousUniverse;

                    }

                    if (mUniverse.getTitle(getActivity()).length() == 0) {
                        mUniverse = childUniverse;
                    }

                    if (this.title != null) {
                        this.title.setText(mUniverse.getTitle(getActivity()));
                    }

                    ((CustomUniverse) universe).setChildUniverse(childUniverse);
                    //universe.setUniverseId(previousUniverse.getUniverseId());
                    downloadNewDocuments(mUniverse, 1, true);
                    return;
                }
            }
            downloadNewDocuments(mUniverse, 1, false);
        }

        getBaseActivity().getActionBar().show();
        if (header!=null && header.getVisibility() != View.VISIBLE) {

            header.setVisibility(View.VISIBLE);
        }
    }

    private void loadFavoritesDocuments() {
        currentLoadingPage = 1;
        ResearchDocumentResultList list = new ResearchDocumentResultList();
        list.addAll(getNatixisApp().getFavoritesCache().getDocuments());
        Collections.sort(list, new ResearchDocumentResultList.ResearchDocumentComparator());
        OnTimelineRetrieved(list, null, 1, false);
    }

    private void loadDownloadedDocuments() {
        currentLoadingPage = 1;
        ResearchDocumentResultList list = new ResearchDocumentResultList();
        list.addAll(getNatixisApp().getDocumentCache().getDocuments());
        Collections.sort(list, new ResearchDocumentResultList.ResearchDocumentComparator());
        OnTimelineRetrieved(list, null, 1, false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("BGC", "Destroying TImeline fragment");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getAction();

                if (action==MotionEvent.ACTION_UP)
                {
                    touched=true;
                }
                if (action==MotionEvent.ACTION_DOWN)
                {
                    touched=false;
                }

        return false;
    }


    public final class InnerTimelineRequestListener extends TimelineRequestListener {

        public InnerTimelineRequestListener(RequestLanguage language, String cacheKey, int page, boolean forced) {
            super(language, cacheKey, page, forced);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException, ResearchRequestError error) {
            OnTimelineDownloadFailed(spiceException, error, mForced);
        }

        @Override
        public void onRequestSuccess(ResearchDocumentResultList researchDocuments) {
            OnTimelineRetrieved(researchDocuments, mCacheKey, mPage, mForced);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
