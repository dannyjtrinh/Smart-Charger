package com.danny.batt_query;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    Intent BattServiceIntent;
    public static final String BAT_LEVEL_APP = "com.batt_level_query";
    public static String BAT_LEVEL = "";
    private com.danny.batt_query.BattService BattService;
    Context mContext;
    public Context getContext(){
        return mContext;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext= this;
        BattService = new com.danny.batt_query.BattService(getContext());
        BattServiceIntent = new Intent(getContext(), BattService.getClass());
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

    public void activateService(View view) {
        if (!isMyServiceRunning(BattService.getClass())) {
            startService(BattServiceIntent);
        }
    }

    public void deactivateService(View view) {
        if (isMyServiceRunning(BattService.getClass())) {
            stopService(BattServiceIntent);
        }
    }


}
