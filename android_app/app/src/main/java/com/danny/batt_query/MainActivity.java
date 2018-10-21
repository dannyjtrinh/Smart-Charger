package com.danny.batt_query;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;


public class MainActivity extends AppCompatActivity {

    Intent BattServiceIntent;
    public static final String BAT_LEVEL_APP = "com.batt_level_query";
    public static String BAT_LEVEL = "";
    private com.danny.batt_query.BattService BattService;
    Context mContext;
    public Context getContext(){
        return mContext;
    }
    public CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext= this;
        BattService = new com.danny.batt_query.BattService(getContext());
        BattServiceIntent = new Intent(getContext(), BattService.getClass());
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        updateStatus();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(BattServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

    public void updateStatus(){
        checkBox.setChecked(isMyServiceRunning(BattService.getClass()));
    }
    public void activateService(View view) {
        BattService.set_restart_flag(true);
        if (!isMyServiceRunning(BattService.getClass())) {
            startService(BattServiceIntent);
        }
        updateStatus();
    }

    public void deactivateService(View view) {
        BattService.set_restart_flag(false);
        if (isMyServiceRunning(BattService.getClass())) {
            stopService(BattServiceIntent);
        }
        updateStatus();
    }


}
