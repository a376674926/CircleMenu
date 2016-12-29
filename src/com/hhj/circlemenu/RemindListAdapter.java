
package com.hhj.circlemenu;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RemindListAdapter extends FPBaseAdapter<RemindInfo> {

    private static final String TAG = RemindListAdapter.class.getSimpleName();
    private int mSelectedPosition = -1;
    private SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss");
    public RemindListAdapter(Context context, List<RemindInfo> datas) {
        super(context, datas);
    }

    @Override
    public View createView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.remind_item_layout, null);
            holder.itemTitle = ((TextView) view.findViewById(R.id.tv_remind_title));
            holder.itemDate = ((TextView) view.findViewById(R.id.tv_remind_date));
            holder.itemTime = ((TextView) view.findViewById(R.id.tv_remind_time));
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        RemindInfo remindInfo = datas.get(position);
        holder.itemDate.setText(simpleDateFormat1.format(remindInfo.getTime()));
        holder.itemTime.setText(simpleDateFormat2.format(remindInfo.getTime()));
        holder.itemTitle.setText(remindInfo.getName());
        if(mSelectedPosition == position){
        	holder.itemTitle.setSelected(true);
        }else{
        	holder.itemTitle.setSelected(false);
        }
        return view;
    }

    private class ViewHolder {
        TextView itemDate;
        TextView itemTime;
        TextView itemTitle;
    }

    public void setSelectedItemPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

}
