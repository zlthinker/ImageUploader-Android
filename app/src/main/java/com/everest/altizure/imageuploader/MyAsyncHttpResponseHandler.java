package com.everest.altizure.imageuploader;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by lzhouai on 11/4/2015.
 */
public class MyAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
    private final String TAG = MyAsyncHttpResponseHandler.class.getSimpleName();
    private int retry = 0;
    private String url;
    private String filePath;
    private AsyncHttpClient client;

    public MyAsyncHttpResponseHandler(String filepath){
        super();
        this.filePath = filepath;
    }

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                          Throwable arg3) {
        // TODO Auto-generated method stub
/*        if(arg2.length>0 && arg2!=null) {
            String response = new String(arg2);
            Log.e(TAG, filePath+": "+response);
        }*/


        Log.e(TAG, filePath+": Failed to upload after 5 try.");

    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
        // TODO Auto-generated method stub
        String response = new String(arg2);
        Log.i(TAG, filePath + ": " + response);
    }

    @Override
    public void onRetry(int retry) {
        Log.d(TAG, filePath + ": now in " + retry + "th retry.");
    }

    public String getFilePath(){
        return filePath;
    }
}
