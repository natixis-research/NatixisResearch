package com.natixis.natixisresearch.app.activity.component;

import android.content.Context;

import android.widget.ExpandableListView;

import com.natixis.natixisresearch.app.R;

/**
 * Created by Thibaud on 10/04/2017.
 */
public class NatixisExpandableListview extends ExpandableListView {
    public NatixisExpandableListview(Context context) {
        super(context);

       ExpandableListView.LayoutParams params = new ExpandableListView.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        setLayoutParams(params);
/*
        ViewGroup.MarginLayoutParams mlp = new MarginLayoutParams(getLayoutParams());

        int dpValue = 10; // margin in dips
        float d = context.getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d); // margin in pixels
        mlp.setMargins(margin, 0, 0, 0);

        setLayoutParams(mlp);*/

        setGroupIndicator(null);
     //   setChildDivider(getResources().getDrawable(android.R.color.transparent));
        setChildDivider(getResources().getDrawable(R.drawable.child_separator_transparent));
/*
        int dpValue = 1; // margin in dips
        int h = (int)(dpValue * context.getResources().getDisplayMetrics().density); // margin in pixels
        setDividerHeight(h);
*/
       /* ViewTreeObserver vto = getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    setIndicatorBounds(getRight()- 40, getWidth());
                } else {
                    setIndicatorBoundsRelative(getRight()- 40, getWidth());
                }
            }
        });*/

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //widthMeasureSpec = MeasureSpec.makeMeasureSpec(2000,                MeasureSpec.AT_MOST);
     //   widthMeasureSpec= getWidth();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(600,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
            // TODO: Workaround for http://code.google.com/p/android/issues/detail?id=22751
        }
    }
}
