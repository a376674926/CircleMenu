
package com.hhj.circlemenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 提醒列表
 * @author hhj@20160804
 */
public class RemindFragment extends BasePageFragment{
    private final static String TAG = "SedentaryRemindFragment";

    private String mTitle;
    private TextView mTitleTextView;
    private ListView mRemindListView;
    private RemindListAdapter mRemindListAdapter;
    private int mSelectedPosition;

    public static RemindFragment newInstance(String title) {
        RemindFragment fragment = new RemindFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FRAGMENT_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(KEY_FRAGMENT_TITLE);
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        /*mTitleTextView = (TextView) view.findViewById(R.id.tv_title);
        mTitleTextView.setText(mTitle);*/

        mRemindListView = (ListView) view.findViewById(R.id.listview_remind);
        List<RemindInfo> remindInfos = getData();
        mRemindListAdapter = new RemindListAdapter(mActivity, remindInfos);
        mRemindListView.setAdapter(mRemindListAdapter);
        mRemindListView.setOnItemClickListener(new RemindItemClickListener());
        mRemindListView.setOnItemSelectedListener(new RemindItemSelectedListener());
    }

    private List<RemindInfo> getData() {
        List<RemindInfo> infos = new ArrayList<RemindInfo>();
        RemindInfo remindInfo = new RemindInfo();
        remindInfo.setTime(System.currentTimeMillis());
        remindInfo.setEndTime(System.currentTimeMillis() + 12000);
        remindInfo.setName("女儿卡");
        infos.add(remindInfo);
        return infos;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_voice_remind;
    }

    @Override
    public void fetchData() {

    }

    private class RemindItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        	mSelectedPosition = position;
        	mRemindListAdapter.setSelectedItemPosition(mSelectedPosition);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private class RemindItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	RemindInfo remindInfo = mRemindListAdapter.getData(position);
        	playRemindVoice(remindInfo);
        }
    }   
    
    public void playRemindVoice(RemindInfo remindInfo) {
        Utils.playRemindVoice(mActivity, remindInfo);
	}

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

}
