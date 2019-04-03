package com.natixis.natixisresearch.app.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.adapter.holder.DocumentHolder;
import com.natixis.natixisresearch.app.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Thibaud on 26/04/2017.
 */
public class ContextMenuAdapter {

    public final static int ACTION_TYPE_FAVORITE = 1;
    public final static int ACTION_TYPE_DOWNLOAD = 2;
    public final static int ACTION_TYPE_EMAIL = 3;

    ContextMenuClickListener clickListener;
    RelativeLayout layoutMenu;

    ImageView viewTouchIndicator;

    ArrayList<ImageView> ivButtons = new ArrayList<ImageView>();
    SparseArray<Boolean> wasInBound = new SparseArray<Boolean>();
    SparseArray<Boolean> isZoomed = new SparseArray<Boolean>();
    SparseArray<Point> translationPoints = new SparseArray<Point>();
    //  ImageView viewDownloadButton;
    //ImageView viewFavoriteButton;
    //ImageView viewEmailButton;

    //boolean wasInBoundFavoriteButton = false;
    // boolean wasInBoundDownloadButton = false;

    Activity context;
    int screenWidth = 0;
    int screenHeight = 0;
    int actionBarSize = 0;

    //  int buttonSize = 0;

    public ContextMenuAdapter(RelativeLayout layout, Activity context,ContextMenuClickListener listener) {
        clickListener=listener;
        layoutMenu = layout;
        viewTouchIndicator = (ImageView) layoutMenu.findViewById(R.id.bt_touch);


        ImageView ivDownload = (ImageView) layoutMenu.findViewById(R.id.bt_download);
        ivDownload.setTag(ACTION_TYPE_DOWNLOAD);
        ivButtons.add(ivDownload);

        ImageView ivFavorite = (ImageView) layoutMenu.findViewById(R.id.bt_favorite);
        ivFavorite.setTag(ACTION_TYPE_FAVORITE);
        ivButtons.add(ivFavorite);

        ImageView ivMail = (ImageView) layoutMenu.findViewById(R.id.bt_mail);
        ivMail.setTag(ACTION_TYPE_EMAIL);
        ivButtons.add(ivMail);

        //viewDownloadButton = (ImageView) layoutMenu.findViewById(R.id.bt_download);
        //viewFavoriteButton = (ImageView) layoutMenu.findViewById(R.id.bt_favorite);
        this.context = context;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        //   buttonSize = (int) Utils.convertDpToPixel(60, context);
    }

    boolean showing = false;

    public void show(final int x, final int y, final DocumentHolder doc, final MotionEvent event) {
        showing = true;
        final int zoom = (int) Utils.convertDpToPixel(10, context);

        for (ImageView button : ivButtons) {
            zoomView(button, zoom, false);
        }


        layoutMenu.setVisibility(View.VISIBLE);

        layoutMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("BGC", "touch layoutMenu" + motionEvent.getX() + " ; " + motionEvent.getY());
                if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    hide();

                    for (ImageView button : ivButtons) {
                        if (isInBound(button, motionEvent)) {
                            Integer tag = (Integer) button.getTag();
                            clickListener.onContextMenuClick(doc,tag);

                            break;
                        }
                    }


                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                    for (ImageView button : ivButtons) {
                        boolean inBound = isInBound(button, motionEvent);
                        Integer tag = (Integer) button.getTag();
                        boolean wasInBoundCurrent = wasInBound.get(tag, false);
                        if (inBound || wasInBoundCurrent) {

                            if (inBound && !wasInBoundCurrent) {
                                Log.d("BGC", "touch viewButton #" + tag + " : " + motionEvent.getX() + " ; " + motionEvent.getY());
                                wasInBound.put(tag, true);
                                zoomView(button, zoom, true);
                            } else if (!inBound && wasInBoundCurrent) {
                                zoomView(button, zoom, false);
                                wasInBound.put(tag, false);

                            }

                            break;
                        }
                    }

                }
                return false;
            }
        });

        layoutMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                //Calculate Touch position!

                //Calculating X position
                int minPosX = screenWidth - (viewTouchIndicator.getWidth());
                int currentPosX = x - (viewTouchIndicator.getWidth() / 2);
                int finalPosX = Math.min(currentPosX, minPosX);

                //Calculate Y position with titlebar+actionbar height
                Rect rect = new Rect();
                Window window = context.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                int minPosY = screenHeight - (viewTouchIndicator.getHeight() / 2);
                int touchPosY = y - actionBarSize - rect.top;
                int currentPosY = touchPosY - (viewTouchIndicator.getHeight() / 2);
                int finalPosY = Math.min(currentPosY, minPosY);


                RelativeLayout.MarginLayoutParams param = (RelativeLayout.MarginLayoutParams) viewTouchIndicator.getLayoutParams();
                param.leftMargin = finalPosX;
                param.topMargin = finalPosY;
                viewTouchIndicator.requestLayout();


                for (ImageView button : ivButtons) {
                    button.setX(finalPosX);
                    button.setY(finalPosY);
                }

                ViewTreeObserver obs = layoutMenu.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }


                layoutMenu.bringToFront();
                layoutMenu.requestFocus();


                translationPoints.clear();
                final int r = (int) Utils.convertDpToPixel(90, context);
                for (ImageView button : ivButtons) {
                    Integer tag = (Integer) button.getTag();
                    wasInBound.put(tag, false);
                    button.setVisibility(View.VISIBLE);

                    //http://math.rice.edu/~pcmi/sphere/degrad.gif
                    if (tag == ACTION_TYPE_EMAIL) {

                        double t = 7 * Math.PI / 12;
                        int x = (int) Math.round(r * Math.cos(t));
                        int y = (int) Math.round(r * Math.sin(t));

                        translationPoints.put(tag, new Point(x, y * -1));
                    } else if (tag == ACTION_TYPE_DOWNLOAD) {

                        double t = 11 * Math.PI / 12;
                        int x = (int) Math.round(r * Math.cos(t));
                        int y = (int) Math.round(r * Math.sin(t));

                        translationPoints.put(tag, new Point(x, y * -1));
                    } else if (tag == ACTION_TYPE_FAVORITE) {
                        double t = 5 * Math.PI / 4;
                        int x = (int) Math.round(r * Math.cos(t));
                        int y = (int) Math.round(r * Math.sin(t));

                        translationPoints.put(tag, new Point(x, y * -1));
                    }
                }


                int animationDuration = 400;
                for (ImageView button : ivButtons) {

                    Integer tag = (Integer) button.getTag();
                    Point translation = translationPoints.get(tag);
                    if (translation != null) {
                        button.animate()
                                .x(button.getX() + translation.x)
                                .y(button.getY() + translation.y)
                                .setDuration(animationDuration)
                                .setInterpolator(new OvershootInterpolator());
                    }
                }
                showing = false;
            }

        });
    }

    public void zoomView(final ImageView v, int zoomLevel, boolean zoom) {
        Integer tag = (Integer) v.getTag();
        if (zoom) {
            Boolean zommed = isZoomed.get(tag, false);
            if (zommed == null || zommed == false) {
                isZoomed.put(tag, true);
                Point translation = translationPoints.get(tag);
                v.setBackgroundResource(R.drawable.circle_menubutton_hover);
                v.animate()
                        .x(viewTouchIndicator.getX()+translation.x + zoomLevel * -1)
                        .y(viewTouchIndicator.getY()+translation.y + zoomLevel * -1)
                        .setDuration(150)
                        .scaleX(1.25f)
                        .scaleY(1.25f)
                        .setInterpolator(new LinearInterpolator());
            }
        } else {
            Boolean zommed = isZoomed.get(tag, false);
            if (zommed != null && zommed == true) {
                isZoomed.put(tag, false);
                Point translation = translationPoints.get(tag);
                v.setBackgroundResource(R.drawable.circle_menubutton);
                v.animate()
                        .scaleX(1)
                        .scaleY(1)
                        .x(viewTouchIndicator.getX()+translation.x)
                        .y(viewTouchIndicator.getY()+translation.y)
                        .setDuration(150)
                        .setInterpolator(new LinearInterpolator());
            }
        }
    }

    public void hide() {
        Log.d("BGC", "hide request");
        layoutMenu.setVisibility(View.GONE);
        for (ImageView button : ivButtons) {
            button.setVisibility(View.GONE);
        }
    }

    public boolean isVisible() {
        return layoutMenu.getVisibility() == View.VISIBLE;
    }

    public boolean isShowing() {
        return showing;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return layoutMenu.dispatchTouchEvent(motionEvent);
    }

    public boolean isInBound(View v, MotionEvent event) {
        Rect rect = new Rect((int) v.getX(), (int) v.getY(), (int) v.getX() + v.getWidth(), (int) v.getY() + v.getHeight());
        return rect.contains((int) event.getX(), (int) event.getY());
    }




    public interface ContextMenuClickListener {
        public void onContextMenuClick(DocumentHolder document,int tag);
    }

}
