
package com.hhj.circlemenu;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private int i = 0;
    private int booldPressure = 50;
    private TextView mPulseTextView;
    private TextView mDiastolicTextView;
    private TextView mSystolicTextView;
    private CircleMenuView mCircleMenuView;
    private static final String WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSION_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pulse_booldpressure);
        mPulseTextView = (TextView) findViewById(R.id.tv_pulse_value);
        mDiastolicTextView = (TextView) findViewById(R.id.tv_diastolic_blood_pressure_value);
        mSystolicTextView = (TextView) findViewById(R.id.tv_systolic_blood_pressure_value);
        mCircleMenuView = (CircleMenuView) findViewById(R.id.circle_menu_view_boold_pressure);
        mCircleMenuView.setCenterText(getResources().getString(R.string.start));
        mCircleMenuView.setSystolicBloodPressure(90);
        mCircleMenuView.setOnClickListener(new CircleMenuView.CircleMenuClickListener() {

            @Override
            public void onClick(View v, int position) {
                CircleMenuView circleMenuView2 = (CircleMenuView) v;
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
                circleMenuView2.setSystolicBloodPressure((int) (Math.random() * 200));
            }
        });
        
        mPulseTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("debug", "=====mPulseTextView========onClick");
                mCircleMenuView.startAutoDetect();
                mCircleMenuView.setProgressText("80%");
            }
        });
        
        /*RemindFragment remindFragment = RemindFragment.newInstance("提醒");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fTransaction = fragmentManager.beginTransaction();
        fTransaction.replace(R.id.layout_fragment_container, remindFragment);
        fTransaction.addToBackStack(null);  
        fTransaction.commit();*/
        
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        /*String[] permissions = {WRITE_EXTERNAL_STORAGE_PERMISSION};
        if(ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE_PERMISSION) ==
                PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }*/
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            Toast.makeText(this, "授权了", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "未授权了", Toast.LENGTH_LONG).show();
        } 
    }
    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for(int grantResult:grantResults){
            if(grantResult == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
