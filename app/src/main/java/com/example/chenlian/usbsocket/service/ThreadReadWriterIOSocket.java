package com.example.chenlian.usbsocket.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.chenlian.usbsocket.MainActivity;
import com.example.chenlian.usbsocket.util.FileHelper;
import com.example.chenlian.usbsocket.util.MyUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by chenlian on 11/30/2015.
 */
public class ThreadReadWriterIOSocket implements Runnable {
    private Socket client;

    private ThreadRespond respond;

    ThreadReadWriterIOSocket(Socket client,ThreadRespond respond) {
        this.client = client;
        this.respond = respond;
    }

    @Override
    public void run() {
        Log.d(AndroidService.TAG, Thread.currentThread().getName() + "---->"
                + "a client has connected to server!");
        BufferedOutputStream out;
        BufferedInputStream in;
        try {
            //PC端发来的数据msg
            String currCMD;
            out = new BufferedOutputStream(client.getOutputStream());
            in = new BufferedInputStream(client.getInputStream());
            // testSocket();// 测试socket方法
            AndroidService.ioThreadFlag = true;
            while (AndroidService.ioThreadFlag) {
                try {
                    if (!client.isConnected()) {
                        break;
                    }

                    //接收PC发来的数据
                    Log.v(AndroidService.TAG, Thread.currentThread().getName()
                            + "---->" + "will read......");
                    //读操作命令
                    currCMD = readCMDFromSocket(in);
                    Log.v(AndroidService.TAG, Thread.currentThread().getName()
                            + "---->" + "**currCMD ==== " + currCMD);

                    //根据命令分别处理数据
                    if (currCMD.contains("*SMS*")) {
                        String number = currCMD.substring(5);
                        Log.v("phone_number", "---->" + number);
                        respond.onPhoneNumber(number);
                        out.write("SMSOK".getBytes());
                        out.flush();
                    } else if (currCMD.contains("#location#")) {
                        String webLocation = currCMD.substring(10);
//                        respond.onWebLocation("http://www.qq.com");
                        Log.v("web_location", "---->" + webLocation);
                        respond.onWebLocation(webLocation);
                        out.write("WEBOK".getBytes());
                        out.flush();
                    } else if (currCMD.equals("3")){
                        out.write("OK".getBytes());
                        out.flush();
                    }else if (currCMD.equals("exit")) {
                        AndroidService.ioThreadFlag = false;
                    }
                } catch (Exception e) {
                    // try {
                    // out.write("error".getBytes("utf-8"));
                    // out.flush();
                    // } catch (IOException e1) {
                    // e1.printStackTrace();
                    // }
                    Log.e(AndroidService.TAG, Thread.currentThread().getName()
                            + "---->" + "read write error111111");
                }
            }
            out.close();
            in.close();
        } catch (Exception e) {
            Log.e(AndroidService.TAG, Thread.currentThread().getName()
                    + "---->" + "read write error222222");
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    Log.v(AndroidService.TAG, Thread.currentThread().getName()
                            + "---->" + "client.close()");
                    client.close();
                }
            } catch (IOException e) {
                Log.e(AndroidService.TAG, Thread.currentThread().getName()
                        + "---->" + "read write error333333");
                e.printStackTrace();
            }
        }
    }

    // 读取命令
    public static String readCMDFromSocket(InputStream in) {
        int MAX_BUFFER_BYTES = 2048;
        String msg = "";
        byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
        try {
            int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
            msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
            tempbuffer = null;
        } catch (Exception e) {
            Log.v(AndroidService.TAG, Thread.currentThread().getName()
                    + "---->" + "readFromSocket error");
            e.printStackTrace();
        }
        // Log.v(Service139.TAG, "msg=" + msg);
        return msg;
    }

    interface ThreadRespond{
        void onWebLocation(String location);
        void onPhoneNumber(String number);
    }
}
