package com.natixis.natixisresearch.app.activity.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.natixis.natixisresearch.app.NatixisResearchApp;
import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.HomeActivity;
import com.natixis.natixisresearch.app.activity.adapter.ContextMenuAdapter;
import com.natixis.natixisresearch.app.activity.adapter.ResearchDocumentAdapter;
import com.natixis.natixisresearch.app.activity.adapter.holder.DocumentHolder;
import com.natixis.natixisresearch.app.network.bean.CustomUniverse;
import com.natixis.natixisresearch.app.network.bean.ResearchDocument;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.bean.SearchUniverse;
import com.natixis.natixisresearch.app.network.bean.UniversesList;
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
public class TimelineFragment extends BaseFragment implements ContextMenuAdapter.ContextMenuClickListener {
    public final static int REQUEST_DOWNLOAD = 5;
    private static final String HEADER_FRAGMENT = "HEADER";
    private final int AUTOLOAD_THRESHOLD = 4;
    private final int NB_ITEM_PER_PAGE = 10;


    int currentLoadingPage = 0;
    int lastLoadedPage = 0;
    ListView listviewDocument;
    View mPlaceholderView;
    ResearchDocumentAdapter adapter = null;
    private ProgressBar progress;
    SwipeRefreshLayout refreshLayout;
    SwipeRefreshLayout refreshLayoutEmpty;
    LinearLayout emptyLayout;

    RelativeLayout layoutParent;
    View parent;
    boolean viewCreated = false;
    ResearchUniverse mUniverse = null;
    TextView emptyView;
    TextView emptyViewDesc;
    HeaderTimelineFragment mHeaderFragment;
    UniversesList favoritesList = new UniversesList();

    FloatingActionButton favButton = null;
    private RequestLanguage mCurrentRequestLanguage;

    public HeaderTimelineFragment getHeaderFragment() {
        return mHeaderFragment;
    }

    /*
    public void setUniverse(ResearchUniverse universe) {
        mUniverse = universe;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_timeline, container, false);
        parent = v.findViewById(R.id.layout_parent);
        listviewDocument = (ListView) parent.findViewById(R.id.lv_documents);
        listviewDocument.setEmptyView(parent.findViewById(R.id.swipe_layout_empty));

        progress = (ProgressBar) parent.findViewById(R.id.progress_documents);


        layoutParent = (RelativeLayout) parent.findViewById(R.id.layout_parent);
        refreshLayout = (SwipeRefreshLayout) parent.findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeResources(R.color.line_theme,
                android.R.color.darker_gray,
                android.R.color.holo_blue_dark, R.color.bg_title);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadNewDocuments(mUniverse, 1, true);
            }
        });

        refreshLayoutEmpty = (SwipeRefreshLayout) parent.findViewById(R.id.swipe_layout_empty);
        refreshLayoutEmpty.setColorSchemeResources(R.color.line_theme,
                android.R.color.darker_gray,
                android.R.color.holo_blue_dark, R.color.bg_title);
        refreshLayoutEmpty.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadNewDocuments(mUniverse, 1, true);
            }
        });


        mHeaderFragment = (HeaderTimelineFragment) getChildFragmentManager().findFragmentByTag(HEADER_FRAGMENT);

        //HeaderTimelineFragment header = parent.findViewById(R.id.fragment_header);;
        emptyLayout = (LinearLayout) parent.findViewById(R.id.emptyLayout);
        emptyView = (TextView) parent.findViewById(R.id.emptyView);
        emptyViewDesc = (TextView) parent.findViewById(R.id.emptyViewDesc);

        View top = inflater.inflate(R.layout.top_layout, null, false);
        mPlaceholderView = top.findViewById(R.id.placeholder);


        listviewDocument.addHeaderView(top, null, false);
        getHeaderFragment().getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getHeaderFragment().onGlobalLayout();
                getHeaderFragment().getView().getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);

                listviewDocument.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @SuppressLint("NewApi")
                            @SuppressWarnings("deprecation")
                            @Override
                            public void onGlobalLayout() {
                                View header = getHeaderFragment().getView();
                                int headerHeight = header.getMeasuredHeight();
                                mPlaceholderView.setMinimumHeight(headerHeight);
                                refreshLayout.setTop(header.getMeasuredHeight());
                                refreshLayoutEmpty.setTop(header.getMeasuredHeight());
                                parent.requestLayout();
                                onScrollChanged();

                                refreshLayout.setProgressViewOffset(false, (int) header.getBottom() - 50, progress.getTop() - 3);
                                getHeaderFragment().getView().bringToFront();
                                ViewTreeObserver obs = listviewDocument.getViewTreeObserver();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    obs.removeOnGlobalLayoutListener(this);
                                } else {
                                    obs.removeGlobalOnLayoutListener(this);
                                }
                            }
                        });
            }
        });
        listviewDocument.setOnScrollListener(new AbsListView.OnScrollListener() {
            int lastFirstVisiblePos = 0;
            boolean animate = false;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            boolean treatingScroll = false;
            boolean justChanged = false;

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totItemCount) {

                int realTotCount = totItemCount - 1;
                if (!treatingScroll && realTotCount - AUTOLOAD_THRESHOLD <= firstVisibleItem + visibleItemCount) {
                    treatingScroll = true;
                    try {
                        if (currentLoadingPage == 0 && mUniverse != null && mUniverse.supportPagination()) {
                            // mutiple de NB_ITEM_PER_PAGE donc potentiellement d'autres elements dispo
                            if (realTotCount > 0 && realTotCount % NB_ITEM_PER_PAGE == 0) {
                                int lastPage = realTotCount / NB_ITEM_PER_PAGE;
                                downloadNewDocuments(mUniverse, lastPage + 1, false);
                            }
                        }
                    } finally {
                        treatingScroll = false;
                    }
                }
                onScrollChanged();
            }
        });


        favButton = (FloatingActionButton) v.findViewById(R.id.fab);
        if (favButton != null)
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickFavButton(view);

                }
            });

        adapter = new ResearchDocumentAdapter(getBaseActivity(), new ArrayList<ResearchDocument>());
        listviewDocument.setAdapter(adapter);
        listviewDocument.setOnItemClickListener(adapter);
        final RelativeLayout layoutContextMenu = (RelativeLayout) v.findViewById(R.id.layout_contextmenu);
        final ContextMenuAdapter contextMenu = new ContextMenuAdapter(layoutContextMenu, getBaseActivity(), this);
        adapter.setContextMenu(contextMenu);

        refreshLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (contextMenu.isVisible()) {
                    return contextMenu.dispatchTouchEvent(motionEvent);
                }
                return false;
            }
        });
        listviewDocument.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP/* ||motionEvent.getAction()==MotionEvent.ACTION_CANCEL*/) {
                    if (contextMenu.isVisible()) {
                        Log.d("BGC", "HIDE layout");
                        // contextMenu.hide();
                        //return true;
                        return contextMenu.dispatchTouchEvent(motionEvent);
                    }

                } else if (contextMenu.isShowing()) {
                    return true;
                } else if (contextMenu.isVisible()) {
                    return contextMenu.dispatchTouchEvent(motionEvent);
                }
                return false;
            }
        });

        viewCreated = true;

        return v;
    }


    private void onScrollChanged() {
        getHeaderFragment().onMainFragmentListviewScroll(listviewDocument);

    }





    @Override
    public void onStart() {
        super.onStart();
        Log.i("BGC", "Starting TImeline fragment");
        checkFirstLoad();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkFirstLoad();
    }

    public void checkFirstLoad(){
        if (adapter.getCount() == 0 && getActivity()!=null && mUniverse!=null) {
            loadUniverse(mUniverse);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        RequestLanguage realLanguage = getBaseActivity().getResearchRequestLanguage();
        if (mCurrentRequestLanguage != null && realLanguage != null) {
            if (!mCurrentRequestLanguage.toString().equalsIgnoreCase(realLanguage.toString())) {
                reloadUniverse();
            }
        }
    }

    boolean progressBarCancelled = false;

    public void showTimelineProgressBar(boolean visible) {
        if (visible) {
            emptyLayout.setVisibility(View.GONE);
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBarCancelled = true;
                    progress.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);

                    refreshLayoutEmpty.setRefreshing(false);

                    emptyLayout.setVisibility(View.VISIBLE);
                }
            }, 600);
        }
    }


    private void downloadNewDocuments(ResearchUniverse universe, int page, boolean force) {

        // getSpiceManager().cancelAllRequests();
        currentLoadingPage = page;

        mCurrentRequestLanguage = getBaseActivity().getResearchRequestLanguage();
        String token = null;
        if (getNatixisApp().isUserLogged()) {
            token = getNatixisApp().getLoggedUser().getToken();
        }

        showTimelineProgressBar(true);
        String filter = null;
        String universeId = universe != null ? universe.getUniverseId() : "0";

        if (universe instanceof SearchUniverse) {
            filter = universe.getTitle(getActivity());
            if (((SearchUniverse) universe).isExtendResearch()) {
                universeId = "0";
            }
        }


        SearchDocumentRequest request = new SearchDocumentRequest(token, mCurrentRequestLanguage, universeId, NB_ITEM_PER_PAGE, page, filter);
        request.setRetryPolicy(new RetryPolicyNone());

        String cacheKey = "search_" + mCurrentRequestLanguage + "_" + universeId;
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

        getSpiceManager().getFromCacheAndLoadFromNetworkIfExpired(request, cacheKey, expire, new InnerTimelineRequestListener(mCurrentRequestLanguage, cacheKey, page, force));

    }


    private void insertDocuments(ResearchDocumentResultList result) {
        if (result != null) {
            adapter.addMoreDocuments(mUniverse, result);
        }

        adapter.notifyDataSetChanged();
    }


    public void OnTimelineRetrieved(final ResearchDocumentResultList result, final String cacheKey, final int page, boolean forced) {

        Log.i("BGC", "Retrieved document list   : " + cacheKey + " (force:" + forced + ")");
        if (page > 1 && getActivity()!=null) {
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


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (page == 1) {
                    adapter.clearDocuments();
                }
                insertDocuments(result);
                showTimelineProgressBar(false);
                lastLoadedPage = adapter.getCount() / NB_ITEM_PER_PAGE;
                currentLoadingPage = 0;
            }
        });
    }


    public void OnTimelineDownloadFailed(SpiceException e, ResearchRequestError error, boolean forced) {

        Log.i("BGC", "Fail to retrieve document list   : " + e.getLocalizedMessage() + "(" + e.getClass().getName() + ") (force:" + forced + ")");
        showTimelineProgressBar(false);
        currentLoadingPage = 0;
    }


    public void reloadUniverse() {
        loadUniverse(mUniverse);
    }

    public void loadUniverse(ResearchUniverse universe) {
        ResearchUniverse previousUniverse = mUniverse;
        this.mUniverse = universe;

        if (viewCreated && getActivity()!=null) {

            favoritesList = NatixisResearchApp.getInstance().getFavorites();
            if (universe.canBeFavoriteUniverse()) {
                favButton.setVisibility(View.VISIBLE);
                if (favoritesList != null && favoritesList.size() > 0) {
                    if (favoritesList.contains(universe)) {
                        favButton.setImageResource(R.drawable.fab_favoris_on);
                    } else {
                        favButton.setImageResource(R.drawable.fab_favoris_off);
                        ;
                    }
                } else {
                    favButton.setImageResource(R.drawable.fab_favoris_off);
                }

            } else {
                favButton.setVisibility(View.GONE);
                favButton.setImageResource(R.drawable.fab_favoris_off);
            }


            Log.i("BGC", "Loading universe : " + universe.getTitle(getActivity()) + " (" + universe.getUniverseId() + ")");




            getHeaderFragment().changeTitle(mUniverse.getTitle(getActivity()));
            getHeaderFragment().refreshCarousel();
            adapter.clearDocuments();
            adapter.notifyDataSetChanged();
            refreshLayout.setEnabled(true);

            adapter.setSwipeDeleteEnabled(false);
            adapter.setDocumentShareEnabled(false);
            if (universe instanceof CustomUniverse) {

                if (((CustomUniverse) universe).getType() == CustomUniverse.UNIVERSE_DOWNLOADS) {
                    loadDownloadedDocuments();
                    refreshLayout.setEnabled(false);
                    refreshLayoutEmpty.setEnabled(false);
                    adapter.setSwipeDeleteEnabled(true);
                    adapter.setDocumentShareEnabled(true);
                    emptyView.setText(getBaseActivity().getString(R.string.you_have_no_downloads));
                    emptyViewDesc.setText(getBaseActivity().getString(R.string.description_downloads));
                    ((HomeActivity) getActivity()).hideSearchView();
                    return;
                } else if (((CustomUniverse) universe).getType() == CustomUniverse.UNIVERSE_DOWNLOADSFAVORITES) {
                    loadFavoritesDocuments();
                    refreshLayout.setEnabled(false);
                    adapter.setSwipeDeleteEnabled(true);
                    adapter.setDocumentShareEnabled(true);
                    refreshLayoutEmpty.setEnabled(false);
                    emptyView.setText(getBaseActivity().getString(R.string.you_have_no_downloads));
                    emptyViewDesc.setText(getBaseActivity().getString(R.string.description_downloads));
                    ((HomeActivity) getActivity()).hideSearchView();
                    return;
                } else if (((CustomUniverse) universe).getType() == CustomUniverse.UNIVERSE_FAVORITES) {
                    loadFavoritesDocuments();
                    refreshLayout.setEnabled(false);
                    refreshLayoutEmpty.setEnabled(false);

                    adapter.setDocumentShareEnabled(true);
                    adapter.setSwipeDeleteEnabled(true);
                    emptyView.setText(getBaseActivity().getString(R.string.you_have_no_favorites));
                    emptyViewDesc.setText(getBaseActivity().getString(R.string.description_add_favorites));
                    ((HomeActivity) getActivity()).hideSearchView();
                    return;
                } else if (((CustomUniverse) universe).getType() == CustomUniverse.UNIVERSE_SEARCH) {

                    refreshLayout.setEnabled(true);
                    refreshLayoutEmpty.setEnabled(true);
                    ResearchUniverse childUniverse = null;
                    if (previousUniverse instanceof CustomUniverse && ((CustomUniverse) previousUniverse).getType() == CustomUniverse.UNIVERSE_SEARCH) {
                        childUniverse = ((CustomUniverse) previousUniverse).getChildUniverse();
                    } else if (!(childUniverse instanceof CustomUniverse)) {
                        childUniverse = previousUniverse;

                    }

                    if (mUniverse.getTitle(getActivity()).length() == 0) {
                        mUniverse = childUniverse;
                    }

                    getHeaderFragment().changeTitle(getBaseActivity().getString(R.string.search_result_for) + " \"" + mUniverse.getTitle(getActivity()) + "\"");


                    ((CustomUniverse) universe).setChildUniverse(childUniverse);
                    //universe.setUniverseId(previousUniverse.getUniverseId());
                    downloadNewDocuments(mUniverse, 1, true);

                    emptyView.setText("Aucun résultat");
                    emptyViewDesc.setText("Aucun résultat pour" + " \"" + mUniverse.getTitle(getActivity()) + "\"");

                    return;
                }
            }
            downloadNewDocuments(mUniverse, 1, false);
            ((HomeActivity) getActivity()).hideSearchView();
        }else {
            mUniverse = universe;
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
    public void onContextMenuClick(DocumentHolder document, int tag) {
        switch (tag) {
            case (ContextMenuAdapter.ACTION_TYPE_FAVORITE):
                adapter.onClickDocumentFavorite(document);
                break;
            case (ContextMenuAdapter.ACTION_TYPE_DOWNLOAD):
                adapter.onClickDocumentDownload(document);
                break;
            case (ContextMenuAdapter.ACTION_TYPE_EMAIL):
                adapter.onClickDocumentShare(document);
                break;

        }
    }

    public void onClickFavButton(View v) {

        if (favoritesList != null && favoritesList.contains(mUniverse)) {
            favoritesList.remove(mUniverse);
            if (v instanceof FloatingActionButton)
                ((FloatingActionButton) v).setImageResource(R.drawable.fab_favoris_off);
        } else {
            favoritesList.add(mUniverse);
            if (v instanceof FloatingActionButton) {
                ((FloatingActionButton) v).setImageResource(R.drawable.fab_favoris_on);
            }
        }

        getNatixisApp().updateFavorites(favoritesList);
    }

    public static TimelineFragment newInstance() {
        return new TimelineFragment();
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

    public void onUserLoggedIn(){
        if(mHeaderFragment!=null && mHeaderFragment.isAdded()){
            mHeaderFragment.onStart();
        }
    }


}
