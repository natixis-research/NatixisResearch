package com.natixis.natixisresearch.app.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.adapter.ImageSlideAdapter;
import com.natixis.natixisresearch.app.network.bean.ResearchDocumentResultList;
import com.natixis.natixisresearch.app.network.bean.ResearchRequestError;
import com.natixis.natixisresearch.app.network.listener.TimelineRequestListener;
import com.natixis.natixisresearch.app.network.request.RetryPolicyNone;
import com.natixis.natixisresearch.app.network.request.SearchDocumentRequest;
import com.natixis.natixisresearch.app.network.request.types.RequestLanguage;
import com.natixis.natixisresearch.app.utils.Utils;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HeaderTimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HeaderTimelineFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private final int NB_ITEM_CAROUSEL = 3;

    private ProgressBar progressCarousel;

    LinearLayout ergoLayout;
    LinearLayout header;
    ViewPager carousel;
    ImageSlideAdapter carouselAdapter;

    TextView title;


    public HeaderTimelineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HeaderTimelineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HeaderTimelineFragment newInstance(String param1, String param2) {
        HeaderTimelineFragment fragment = new HeaderTimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_header_timeline, container, false);

        progressCarousel = (ProgressBar) v.findViewById(R.id.progress_carousel);
        header = (LinearLayout) v.findViewById(R.id.layout_header);
        ergoLayout = (LinearLayout) v.findViewById(R.id.layout_header_separator);
        ergoLayout.bringToFront();
        title = (TextView) v.findViewById(R.id.big_title);

        carousel = (ViewPager) v.findViewById(R.id.view_carousel);
        int maxItems = getBaseActivity().getNatixisApp().isUserLogged() ? NB_ITEM_CAROUSEL : 1;
        carouselAdapter = new ImageSlideAdapter(getBaseActivity(), maxItems);


        carousel.setAdapter(carouselAdapter);

        initCarouselRefreshRate();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
refreshCarousel();
    }






    private Handler mHandler;
    public static final int DELAY = 5000;
    Timer swipeTimer = null;

    private void initCarouselRefreshRate() {
        mHandler = new Handler();

        final Runnable Update = new Runnable() {
            public void run() {
                int currentPage = carousel.getCurrentItem();
                if (currentPage == NB_ITEM_CAROUSEL - 1) {
                    currentPage = 0;
                } else {
                    currentPage++;
                }
                carousel.setCurrentItem(currentPage, true);
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mHandler.post(Update);
            }
        }, DELAY, DELAY);
    }

    public void onMainFragmentListviewScroll(ListView listview) {
        View v = listview.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
// This check is needed because when the first element reaches the top of the window, the top values from top are not longer valid.
        if (listview.getFirstVisiblePosition() == 0) {
            header.setTranslationY(top);
            ergoLayout.setTranslationY(top);
        } else if (listview.getFirstVisiblePosition() == 1) {
            int h = header.getHeight() *-1;
            header.setTranslationY(h);
            ergoLayout.setTranslationY(h);
        }
    }


    boolean progressBarCarouselCancelled = false;

    public void showCarouselProgressBar(boolean visible) {
        if (visible) {
            progressBarCarouselCancelled = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!progressBarCarouselCancelled) {
                        progressCarousel.setVisibility(View.VISIBLE);
                        progressCarousel.bringToFront();


                    }
                }
            }, 600);
        } else {
            progressBarCarouselCancelled = true;
            progressCarousel.setVisibility(View.GONE);
        }
    }




    public void OnCarouselDownloadFailed(SpiceException e, ResearchRequestError error, boolean forced) {

        Log.d("BGC", "Fail to retrieve carousel documents   : " + e.getLocalizedMessage() + "(" + e.getClass().getName() + ") (force:" + forced + ")");
        showCarouselProgressBar(false);
    }


    public void OnCarouselRetrieved(final ResearchDocumentResultList result, final String cacheKey, final int page, boolean forced) {

        Log.d("BGC", "Retrieved carousel document list   : " + cacheKey + " (force:" + forced + ")");

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                showCarouselProgressBar(false);
                carouselAdapter.setDocuments(result);
                carouselAdapter.notifyDataSetChanged();
                header.bringToFront();

            }
        });
    }

    private void downloadCarouselDocuments() {
        if (getBaseActivity().getNatixisApp().isUserLogged()) {
            RequestLanguage lang = getBaseActivity().getResearchRequestLanguage();
            String token = null;
            if (getNatixisApp().isUserLogged()) {
                token = getNatixisApp().getLoggedUser().getToken();
            }

            showCarouselProgressBar(true);
            String filter = "research_ideas";
            String universeId = "0";
            int page = 0;


            SearchDocumentRequest request = new SearchDocumentRequest(token, lang, universeId, NB_ITEM_CAROUSEL, page, filter);
            request.setRetryPolicy(new RetryPolicyNone());

            String cacheKey = "carousel_" + lang + "_" + universeId;
            if (filter != null) {
                cacheKey += "_" + filter;
            }
            cacheKey += "_" + page;

            long expire = Utils.calculateMaxAge();

            Log.i("BGC", "Executing request  : " + cacheKey);

            getSpiceManager().getFromCacheAndLoadFromNetworkIfExpired(request, cacheKey, expire, new InnerCarouselRequestListener(lang, cacheKey, page));
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    showCarouselProgressBar(false);
                    carouselAdapter.setDocuments(null);
                    carouselAdapter.notifyDataSetChanged();
                    header.bringToFront();

                }
            });
        }
    }

    public void changeTitle(String newTitle) {
        if(title!=null) {
            title.setText(newTitle);
        }
    }

    public void onGlobalLayout() {

        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        title.measure(0, heightMeasureSpec);
        int size = title.getMeasuredHeight();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        int viewPagerWidth = carousel.getWidth();
        final float viewPagerHeight;

        viewPagerHeight = (float) (size * 2);


        layoutParams.width = viewPagerWidth;
        layoutParams.height = (int) viewPagerHeight;

        carousel.setLayoutParams(layoutParams);


    }

    public void refreshCarousel() {

        downloadCarouselDocuments();
    }


    public final class InnerCarouselRequestListener extends TimelineRequestListener {

        public InnerCarouselRequestListener(RequestLanguage language, String cacheKey, int page) {
            super(language, cacheKey, page, false);
        }

        @Override
        public void onRequestFailure(SpiceException spiceException, ResearchRequestError error) {
            OnCarouselDownloadFailed(spiceException, error, mForced);
        }

        @Override
        public void onRequestSuccess(ResearchDocumentResultList researchDocuments) {
            OnCarouselRetrieved(researchDocuments, mCacheKey, mPage, mForced);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
