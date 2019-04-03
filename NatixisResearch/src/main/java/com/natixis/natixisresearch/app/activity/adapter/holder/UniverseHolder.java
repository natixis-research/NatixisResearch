package com.natixis.natixisresearch.app.activity.adapter.holder;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.natixis.natixisresearch.app.R;

/**
 * Created by Thibaud on 23/04/2017.
 */
public class UniverseHolder {
    Drawable icon;
    private TextView tvName;
    Drawable expandedDrawable=null;
    Drawable collapsedDrawable =null;
    private boolean expandable=true;
    boolean selected=false;

    public UniverseHolder(View v) {
         tvName = (TextView) v.findViewById(R.id.tv_nom_categorie);

    }
    public TextView getName() {
        return tvName;
    }

    public void setSelected(boolean selected){
        this.selected=selected;
        if(selected) {
            getName().setBackgroundResource(R.color.natixis_blue);
        }else{

            getName().setBackgroundResource(0);
        }
    }
    public void  setIconResource(int drawable)
    {

        getName().setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);

    }

    public void setDrawableExpanded(Drawable d){
        expandedDrawable=d;
    }
    public void setDrawableCollapsed(Drawable d){
        collapsedDrawable=d;
    }

    public void setExpandable(boolean expandable) {
        this.expandable=expandable;
        setExpanded(false);

    }
    public void setExpanded(boolean expanded) {
        if(expandable) {
            Drawable[] drawables = getName().getCompoundDrawables();

            if (drawables[2] != null) {
                if (expanded) {
                    drawables[2] = expandedDrawable;
                } else {
                    drawables[2] = collapsedDrawable;
                }
            }
            getName().setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
        else{
            Drawable[] drawables = getName().getCompoundDrawables();
           drawables[2] = null ;
            getName().setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
        }

    }
}
