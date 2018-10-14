package com.danny.batt_query;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BattRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BattRestarterBroadcastReceiver.class.getSimpleName(), "Service Stopped and restarted");

        context.startService(new Intent(context, BattService.class));;
    }

}
