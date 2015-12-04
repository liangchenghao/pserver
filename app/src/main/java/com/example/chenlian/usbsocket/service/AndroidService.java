package com.example.chenlian.usbsocket.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by chenlian on 11/30/2015.
 */
public class AndroidService extends Service {
    public static final String TAG = "TAG";
    public static Boolean mainThreadFlag = true;
    public static Boolean ioThreadFlag = true;
    ServerSocket serverSocket = null;
    final int SERVER_PORT = 10086;
    File testFile;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, Thread.currentThread().getName() + "---->" + "  onCreate");
        new Thread() {
            public void run() {
                doListen();
            }
        }.start();
    }

    private void doListen() {
        Log.d(TAG, Thread.currentThread().getName() + "---->"
                + " doListen() START");
        serverSocket = null;
        try {
            Log.d(TAG, Thread.currentThread().getName() + "---->"
                    + " doListen() new serverSocket");
            serverSocket = new ServerSocket(SERVER_PORT);

            boolean mainThreadFlag = true;
            while (mainThreadFlag) {
                Log.d(TAG, Thread.currentThread().getName() + "---->"
                        + " doListen() listen");

                Socket client = serverSocket.accept();

                new Thread(new ThreadReadWriterIOSocket(this, client)).start();
            }
        } catch (IOException e1) {
            Log.v(AndroidService.TAG, Thread.currentThread().getName()
                    + "---->" + "new serverSocket error");
            e1.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 关闭线程
        mainThreadFlag = false;
        ioThreadFlag = false;
        // 关闭服务器
        try {
            Log.v(TAG, Thread.currentThread().getName() + "---->"
                    + "serverSocket.close()");
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(TAG, Thread.currentThread().getName() + "---->"
                + "**************** onDestroy****************");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, Thread.currentThread().getName() + "---->" + " onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "  onBind");
        return null;
    }
}
