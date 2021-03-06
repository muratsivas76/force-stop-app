package com.psklf.forcestop;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zhuyuanxuan on 13/02/2017.
 * ForceStop
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter
        .MyViewHolder> {
    private ArrayList<AppServiceInfo> mDataSet;
    private OnRecyclerViewItemClickListener mOnItemClick;
    private Handler mHandler;
    private Context mCtx;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public ImageView mIconView;
        private Switch mItemSwitch;

        public MyViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.tv_service_name);
            mIconView = (ImageView) view.findViewById(R.id.imgv_icon);
            mItemSwitch = (Switch) view.findViewById(R.id.switch_item);
        }
    }

    public MyRecyclerViewAdapter(ArrayList<AppServiceInfo> appServiceInfoArray,
                                 Handler handler,
                                 Context ctx) {
        mDataSet = appServiceInfoArray;
        mHandler = handler;
        mCtx = ctx;
    }

    /**
     * Called when RecyclerView needs a new {@link MyViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new MyViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new MyViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(MyViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new MyViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(MyViewHolder, int)
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view,
                parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link MyViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link MyViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(MyViewHolder, int)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The MyViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.mItemSwitch.setChecked(true);
        holder.mItemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    Message msg = mHandler.obtainMessage(PublicConstants.MSG_FORCE_STOP_APP);
                    msg.arg1 = holder.getAdapterPosition();
                    msg.sendToTarget();
                }
            }
        });
        final int pos = position;
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClick.onItemClick(v, mDataSet.get(pos));
            }
        });

        PackageManager packageManager = mCtx.getPackageManager();

        // set app name
        CharSequence label = mDataSet.get(position).getApplicationInfo().loadLabel
                (packageManager);
        holder.mTextView.setText(label);

        // set icon image
        Drawable iconDrawable = mDataSet.get(position).getApplicationInfo().loadIcon
                (packageManager);
        holder.mIconView.setImageDrawable(iconDrawable);
    }

    /**
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void removeData(int pos) {
        Log.i("xx", "pos " + pos + " size" + mDataSet.size());
        // first remove data from the array list
        mDataSet.remove(pos);

        // notify self
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, mDataSet.size());
    }

    public void removeAll() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    public void update(ArrayList<AppServiceInfo> appServiceInfoArrayList) {
        mDataSet = appServiceInfoArrayList;

        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClick = listener;
    }

    /**
     * Interface for click event
     */
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, AppServiceInfo info);
    }
}
