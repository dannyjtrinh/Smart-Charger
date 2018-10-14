package com.danny.batt_query;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class BattService extends Service {
    public int counter=0;
    public static String BATT_LEVEL = "";
    public static InetAddress address;
    public static boolean restart_flag = true;


    public BattService(Context applicationContext) {
        super();
        Log.i("Service creation", "created");
    }

    public BattService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(myBatteryReceiver);
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        if(restart_flag) {
            Intent broadcastIntent = new Intent(getApplicationContext(), BattRestarterBroadcastReceiver.class);
            sendBroadcast(broadcastIntent);
        }
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //Register battery receiver
        registerReceiver(this.myBatteryReceiver,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 2000, 2000); //
    }

    /**
     * it sets the timer to print the counter every x seconds and send batt level
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                new MyTask().execute(BATT_LEVEL);
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void set_restart_flag(boolean flag) {
        restart_flag = flag;
    }

    public BroadcastReceiver myBatteryReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int bLevel = arg1.getIntExtra("level", 0); // gets the battery level
            BATT_LEVEL = Integer.toString(bLevel);
        }
    };

    //Asynchronous task to send bat level
    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                //Send battery level to multicast address and port specified
                address = InetAddress.getByName("224.0.0.1");
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                byte[] sendData = params[0].getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, 10001);
                socket.send(sendPacket);
                System.out.println("Broadcast packet sent to: " + address.getHostAddress() + " Level:" + BATT_LEVEL);
                socket.close();
                return "";
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return "";
            } catch (SocketException e) {
                e.printStackTrace();
                return "";
            } catch (IOException e)
            {
                e.printStackTrace();;
                return "";
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}