package com.everest.altizure.imageuploader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RecoverySystem;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

/**
 * Created by zl on 2015/9/3.
 */
public class FileUploadAsyncTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = FileUploadAsyncTask.class.getSimpleName();
    private String url;
    private Context context;
    private ProgressDialog pd;
    private String response = null;
    private int cnt = 0;

    public FileUploadAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("Uploading....");
        pd.setCancelable(false);
        pd.show();
        Toast.makeText(context.getApplicationContext(),
                "After onPreExecute()",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(String... params) {
        url = params[0];
  //      Toast.makeText(context.getApplicationContext(),
  //              "Before doInBackground()",
  //              Toast.LENGTH_LONG).show();
        // 保存需上传文件信息


 /*       FileBody fileBody = new FileBody(file);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = null;
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart("image", fileBody);
        totalSize = reqEntity.getContentLength();
        httpPost.setEntity(reqEntity);*/

        AsyncHttpClient client = new AsyncHttpClient();
        for(int i=0; i<8; i++) {

            File myFile = new File(params[i+1]);
            RequestParams para = new RequestParams();
            try {
                para.put("image", myFile);

                client.post(url, para, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                        // TODO Auto-generated method stub
                        String response = new String(arg2);
                        Log.i(TAG, response);
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        // TODO Auto-generated method stub
                        String response = new String(arg2);
                        Log.i(TAG, response);
                        cnt ++;
                        publishProgress((int)(100 * cnt/(float)8 ));
                    }

                });
            } catch(FileNotFoundException e) {
                Log.d(TAG, "File not found!!!" + params[i+1]);
            }
        }


//            publishProgress(100);


        return response;

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        pd.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            pd.dismiss();
            Toast.makeText(context.getApplicationContext(),
                    cnt + " images uploaded successfully.",
                    Toast.LENGTH_LONG).show();
        } catch(Exception e) {
        }

    }

    public String uploadFile(String uploadUrl, String srcPath) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        String message = null;
        try
        {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();

            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            httpURLConnection.setConnectTimeout(5000);

            httpURLConnection.connect();
            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\""
                    + srcPath.substring(srcPath.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(srcPath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONObject result = new JSONObject(sb.toString());
            message = result.getString("message");

            Log.i(TAG, "The result is " + result.getString("message") + ".\n");


            dos.close();
            is.close();

        } catch (ConnectException e){
            message = e.toString();
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } catch (SocketTimeoutException e){
            message = e.toString();
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            message = e.toString();
            Log.e("UploadFile", e.toString());
            e.printStackTrace();
            //        setTitle(e.getMessage());
        }
        return message;
    }

    private String AsyncUpload(final String uploadUrl, String srcPath) {
        AsyncHttpClient client = new AsyncHttpClient();
        File file = new File(srcPath);
        RequestParams params = new RequestParams();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                cnt++;
                Log.i(TAG, uploadUrl + "is uploaded successfully!");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i(TAG, uploadUrl + "is uploaded failure!");
            }
        };
        try {
            params.put("image", file);
        } catch(FileNotFoundException e){}
        client.post(uploadUrl, params, handler);
        return null;
    }

}
