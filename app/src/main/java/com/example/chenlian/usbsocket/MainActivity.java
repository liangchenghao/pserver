package com.example.chenlian.usbsocket;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.chenlian.usbsocket.service.AndroidService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private MyReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.wv);

        webView.getSettings().setJavaScriptEnabled(true);//设置使用够执行JS脚本
        webView.getSettings().setBuiltInZoomControls(true);//设置使支持缩放

//        intent = getIntent();
//        if (intent.getStringExtra("location") != null){
//            String webUrl = intent.getStringExtra("location");
//            webView.loadUrl(webUrl);
//        }

        //注册广播接收器
        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("usb.MA.MR");
        MainActivity.this.registerReceiver(receiver,filter);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);// 使用当前WebView处理跳转
                return true;//true表示此事件在此处被处理，不需要再广播
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(MainActivity.this, "Oh no!" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            if (bundle.getString("location") != null){
                String web = bundle.getString("location");
                if (!web.isEmpty()){
                    webView.loadUrl(web);
                    Log.v("webView","---->loadUrl 完成");
                }
            }

            if (bundle.getString("phone_number") != null){
                String phone = bundle.getString("phone_number");
                if (!phone.isEmpty()){
                    sendSMS(phone);
                    Log.v("SMS","---->sendSMS 完成");
                }
            }
        }
    }

    //发送短信
    private void sendSMS(String number){
        SmsManager smsManager = SmsManager.getDefault();
        String context = "测试短信！";
        ArrayList<String> list = smsManager.divideMessage(context);
        for (String str : list){
            smsManager.sendTextMessage(number,null,str,null,null);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
