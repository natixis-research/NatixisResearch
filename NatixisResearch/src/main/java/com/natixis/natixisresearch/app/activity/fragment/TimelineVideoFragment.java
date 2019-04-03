package com.natixis.natixisresearch.app.activity.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.HomeActivity;
import com.natixis.natixisresearch.app.activity.VideoPlayerActivity;
import com.natixis.natixisresearch.app.activity.adapter.ResearchVideoAdapter;
import com.natixis.natixisresearch.app.network.bean.CustomUniverse;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.bean.ResearchVideo;
import com.natixis.natixisresearch.app.network.bean.ResearchVideoResultList;
import com.natixis.natixisresearch.app.network.bean.VideosList;
import com.natixis.natixisresearch.app.network.listener.TimelineVideoRequestListener;
import com.natixis.natixisresearch.app.network.request.BOGetVideosRequest;
import com.natixis.natixisresearch.app.network.request.RetryPolicyNone;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.natixis.natixisresearch.app.utils.Utils;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Thibaud on 22/04/2017.
 */
public class TimelineVideoFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    public final static int REQUEST_DOWNLOAD = 5;
    private static final String HEADER_FRAGMENT = "HEADER";
    private final int AUTOLOAD_THRESHOLD = -1;
    private final int NB_ITEM_PER_PAGE = 10;


    int currentLoadingPage = 0;
    int lastLoadedPage = 0;
    ListView listviewVideos;
    View mPlaceholderView;
    ResearchVideoAdapter adapter = null;
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
    //UniversesList favoritesList = new UniversesList();

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
        View v = inflater.inflate(R.layout.fragment_video_timeline, container, false);
        parent = v.findViewById(R.id.layout_parent);
        listviewVideos = (ListView) parent.findViewById(R.id.lv_videos);
        listviewVideos.setEmptyView(parent.findViewById(R.id.swipe_layout_empty));

        progress = (ProgressBar) parent.findViewById(R.id.progress_videos);


        layoutParent = (RelativeLayout) parent.findViewById(R.id.layout_parent);
        refreshLayout = (SwipeRefreshLayout) parent.findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeResources(R.color.line_theme,
                android.R.color.darker_gray,
                android.R.color.holo_blue_dark, R.color.bg_title);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadNewVideos(mUniverse, 1, true);
            }
        });

        refreshLayoutEmpty = (SwipeRefreshLayout) parent.findViewById(R.id.swipe_layout_empty);
        refreshLayoutEmpty.setColorSchemeResources(R.color.line_theme,
                android.R.color.darker_gray,
                android.R.color.holo_blue_dark, R.color.bg_title);
        refreshLayoutEmpty.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadNewVideos(mUniverse, 1, true);
            }
        });


        mHeaderFragment = (HeaderTimelineFragment) getChildFragmentManager().findFragmentByTag(HEADER_FRAGMENT);

        //HeaderTimelineFragment header = parent.findViewById(R.id.fragment_header);;
        emptyLayout = (LinearLayout) parent.findViewById(R.id.emptyLayout);
        emptyView = (TextView) parent.findViewById(R.id.emptyView);
        emptyViewDesc = (TextView) parent.findViewById(R.id.emptyViewDesc);

        View top = inflater.inflate(R.layout.top_layout, null, false);
        mPlaceholderView = top.findViewById(R.id.placeholder);


        listviewVideos.addHeaderView(top, null, false);
                        getHeaderFragment().getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                getHeaderFragment().onGlobalLayout();
                                getHeaderFragment().getView().getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);

                                listviewVideos.getViewTreeObserver().addOnGlobalLayoutListener(
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

                                                refreshLayout.setProgressViewOffset(false, (int) header.getBottom()-50, progress.getTop() - 3);
                                                getHeaderFragment().getView().bringToFront();
                                                ViewTreeObserver obs = listviewVideos.getViewTreeObserver();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                    obs.removeOnGlobalLayoutListener(this);
                                                } else {
                                                    obs.removeGlobalOnLayoutListener(this);
                                                }
                            }
                        });
                    }
                });
        listviewVideos.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                if (AUTOLOAD_THRESHOLD>0 && !treatingScroll && realTotCount - AUTOLOAD_THRESHOLD <= firstVisibleItem + visibleItemCount) {
                    treatingScroll = true;
                    try {
                        if (currentLoadingPage == 0 && mUniverse != null && mUniverse.supportPagination()) {
                            // mutiple de NB_ITEM_PER_PAGE donc potentiellement d'autres elements dispo
                            if (realTotCount > 0 && realTotCount % NB_ITEM_PER_PAGE == 0) {
                                int lastPage = realTotCount / NB_ITEM_PER_PAGE;
                                downloadNewVideos(mUniverse, lastPage + 1, false);
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


                }
            });

        adapter = new ResearchVideoAdapter(getBaseActivity(),  R.layout.item_research_video,new ArrayList<ResearchVideo>());
        listviewVideos.setAdapter(adapter);
        listviewVideos.setOnItemClickListener(this);

        viewCreated = true;


        return v;
    }


    private void onScrollChanged() {
        getHeaderFragment().onMainFragmentListviewScroll(listviewVideos);

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
        if (adapter.getCount() == 0 && getActivity()!=null&& mUniverse!=null) {
            loadUniverse(mUniverse);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        RequestLanguage realLanguage= getBaseActivity().getResearchRequestLanguage();
        if(mCurrentRequestLanguage!=null && realLanguage!=null){
            if(!mCurrentRequestLanguage.toString().equalsIgnoreCase(realLanguage.toString())){
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


    private void downloadNewVideos(ResearchUniverse universe, int page, boolean force) {

        // getSpiceManager().cancelAllRequests();
        currentLoadingPage = page;

       mCurrentRequestLanguage = getBaseActivity().getResearchRequestLanguage();


        showTimelineProgressBar(true);
        String filter = null;
        String universeId = universe != null ? universe.getUniverseId() : "0";

        BOGetVideosRequest request = new BOGetVideosRequest( mCurrentRequestLanguage, universeId, NB_ITEM_PER_PAGE, page, filter);
        request.setRetryPolicy(new RetryPolicyNone());

        String cacheKey = "search_" + mCurrentRequestLanguage + "_videos";
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

        getSpiceManager().getFromCacheAndLoadFromNetworkIfExpired(request, cacheKey, expire, new InnerTimelineVideoRequestListener(mCurrentRequestLanguage, cacheKey, page, force));

    }


    private void insertVideos(VideosList result) {
        if (result != null) {
            adapter.addMoreVideos(mUniverse, result);
        }

        adapter.notifyDataSetChanged();
    }


    public void OnTimelineVideoRetrieved(final VideosList result, final String cacheKey, final int page, boolean forced) {

        Log.i("BGC", "Retrieved document list   : " + cacheKey + " (force:" + forced + ")");
        if (page > 1 &&  getActivity()!=null) {
            //If not page 1 we put documents into page 0 cache to be sure that all pages will be reloaded if page 0 is refreshed.
            int pos = cacheKey.lastIndexOf("_");
            String cacheKeyWithoutPage = cacheKey.substring(0, pos + 1);
            String cacheKeyFirstPage = cacheKeyWithoutPage + "1";
            VideosList cached = null;
            try {

                Future<VideosList> cachedFuture = getSpiceManager().getDataFromCache(VideosList.class, cacheKeyFirstPage);
                cached =cachedFuture.get();
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
                    adapter.clearVideos();
                }
                insertVideos(result);
                showTimelineProgressBar(false);
                lastLoadedPage = adapter.getCount() / NB_ITEM_PER_PAGE;
                currentLoadingPage = 0;
            }
        });
    }


    public void OnTimelineVideoDownloadFailed(SpiceException e, ResearchRequestError error, boolean forced) {

        Log.i("BGC", "Fail to retrieve document list   : " + e.getLocalizedMessage() + "(" + e.getClass().getName() + ") (force:" + forced + ")");
        showTimelineProgressBar(false);
        currentLoadingPage = 0;
    }


    public void reloadUniverse(){
        loadUniverse(mUniverse);
    }
    public void loadUniverse(ResearchUniverse universe) {

        ResearchUniverse previousUniverse = mUniverse;
        this.mUniverse = universe;

        if (viewCreated && getActivity()!=null)
        {
            Log.i("BGC", "Loading universe : " + universe.getTitle(getActivity()) + " (" + universe.getUniverseId() + ")");

            getHeaderFragment().changeTitle(mUniverse.getTitle(getActivity()));

            adapter.clearVideos();
            adapter.notifyDataSetChanged();
            refreshLayout.setEnabled(true);


            if (universe instanceof CustomUniverse) {

                if (((CustomUniverse) universe).getType() == CustomUniverse.UNIVERSE_VIDEOS) {
                    refreshLayout.setEnabled(true);
                    refreshLayoutEmpty.setEnabled(true);
                    emptyView.setText("Il n'y a pas de vidéos");
                    emptyViewDesc.setText("");
                    ((HomeActivity) getActivity()).hideSearchView();
                }
            }
            downloadNewVideos(mUniverse, 1, false);
            ((HomeActivity) getActivity()).hideSearchView();
        }
    }

    public static TimelineVideoFragment newInstance() {

        return new TimelineVideoFragment();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ResearchVideo video = adapter.getItem(i);;
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.PARAMETER_FILENAME, video.getVideoLink());
        intent.putExtra(VideoPlayerActivity.PARAMETER_TITLE, video.getTitle());
        startActivity(intent);
    }


    public final class InnerTimelineVideoRequestListener extends TimelineVideoRequestListener{

        public InnerTimelineVideoRequestListener(RequestLanguage language, String cacheKey, int page, boolean forced) {
            super(language, cacheKey, page, forced);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException, ResearchRequestError error) {
            OnTimelineVideoDownloadFailed(spiceException, error, mForced);
        }

        @Override
        public void onRequestSuccess(ResearchVideoResultList researchVideos) {
            VideosList listRes=researchVideos!=null?researchVideos.getVideos():null;
            OnTimelineVideoRetrieved(listRes, mCacheKey, mPage, mForced);
        }

    }


}
