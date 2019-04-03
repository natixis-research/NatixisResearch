package com.natixis.natixisresearch.app.activity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.natixis.natixisresearch.app.R;
import com.natixis.natixisresearch.app.activity.adapter.holder.UniverseHolder;
import com.natixis.natixisresearch.app.activity.component.NatixisExpandableListview;
import com.natixis.natixisresearch.app.activity.fragment.IUniverseClickListener;
import com.natixis.natixisresearch.app.network.bean.ResearchUniverse;
import com.natixis.natixisresearch.app.network.bean.UniversesList;

/**
 * Created by Thibaud on 22/04/2017.
 */
public class ResearchUniverseExpandableAdapter extends BaseExpandableListAdapter {

    UniversesList mUniverses = null;
    Context mContext = null;
    public LayoutInflater inflater;
    IUniverseClickListener listener;
    ResearchUniverse mSelectedUniverse =null;

    public ResearchUniverseExpandableAdapter(Context context, IUniverseClickListener clickListener) {
        mUniverses=new UniversesList();
        this.mContext = context;
        this.listener=clickListener;

    }
    public void refreshSelectedUniverse( ResearchUniverse selected){
        mSelectedUniverse=selected;
        notifyDataSetChanged();
    }
    public void refreshUniverses(UniversesList universes, ResearchUniverse selected){

        mSelectedUniverse=selected;
        this.mUniverses = universes;
        if(mUniverses==null){
            mUniverses=new UniversesList();
        }
        notifyDataSetChanged();
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount() {
        return mUniverses.size();
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ResearchUniverse) getGroup(groupPosition)).getSubUniverses().size();
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Object getGroup(int groupPosition) {
        return mUniverses.get(groupPosition);
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((ResearchUniverse) getGroup(groupPosition)).getSubUniverses().get(childPosition);
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see
     * {@link #getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }


    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)}.
     *
     * @param groupPosition the position of the group for which the View is
     *                      returned
     * @param isExpanded    whether the group is expanded or collapsed
     * @param v             the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getGroupView(int, boolean, android.view.View, android.view.ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View v, ViewGroup parent) {

        UniverseHolder holder = null;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_research_category, null);
            holder = new UniverseHolder(v);
            holder.setDrawableExpanded(mContext.getResources().getDrawable(R.drawable.arrow_bottom));
            holder.setDrawableCollapsed(mContext.getResources().getDrawable(R.drawable.arrow_top));
            v.setTag(holder);
        } else {
            holder = (UniverseHolder) v.getTag();
        }
        ResearchUniverse universe = (ResearchUniverse) getGroup(groupPosition);
        if(universe.getSubUniverses().size()>0){
            holder.setExpandable(true);
        }else{
            holder.setExpandable(false);
        }
        holder.getName().setText(universe.getTitle(mContext));
        //int id = mContext.getResources().getIdentifier("picto_univers_"+ universe.getImageId(),"drawable",mContext.getPackageName());



        int id = R.drawable.picto_favoris_drawer_off;
        Drawable drawable=null;
        if(id>0){
             drawable = mContext.getResources().getDrawable(id);


        }

        if(drawable==null){
            drawable=mContext.getResources().getDrawable(R.drawable.picto_univers_default);
        }
        if(universe.equals(mSelectedUniverse)){
            holder.setSelected(true);
            holder.setIconResource( R.drawable.picto_favoris_drawer_on);
        }else{
            holder.setSelected(false);
            holder.setIconResource( R.drawable.picto_favoris_drawer_off);
        }

        holder.setExpanded(isExpanded);
        return v;
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *                      returned) within the group
     * @param isLastChild   Whether the child is the last child within the group
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        UniverseHolder universeHolder;
        // if (v == null) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ResearchUniverse universe = (ResearchUniverse) getChild(groupPosition, childPosition);

        if (universe != null && universe.getSubUniverses().size()>0) {
            LinearLayout ll = new LinearLayout(mContext);
            int dpValue = 15; // margin in dips
            int margin = (int) (dpValue * mContext.getResources().getDisplayMetrics().density); // margin in pixels
            ll.setPadding(margin, 0, 0, 0);
            NatixisExpandableListview newListView = new NatixisExpandableListview(mContext);
            final ResearchSubUniverseExpandableAdapter adapter = new ResearchSubUniverseExpandableAdapter(mContext, universe,listener);
            newListView.setAdapter(adapter);
            newListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    listener.onChildUniverseClick((ResearchUniverse) adapter.getChild(groupPosition, childPosition));
                    return true;
                }
            });
            ll.addView(newListView);
            return ll;
        } else {
            v = inflater.inflate(R.layout.item_research_category_child, null);
            universeHolder = new UniverseHolder(v);
            universeHolder.setExpandable(false);
            v.setTag(universeHolder);
            if (universe != null) {
                try {
                    universeHolder.getName().setText(universe.getTitle(mContext));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return v;
        }


    }

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}
