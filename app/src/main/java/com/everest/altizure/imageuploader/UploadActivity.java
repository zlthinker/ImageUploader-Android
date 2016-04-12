package com.everest.altizure.imageuploader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;


/**
 * Created by zl on 2015/9/2.
 */
public class UploadActivity extends AppCompatActivity {
    private static final String TAG = UploadActivity.class.getSimpleName();

    private String ip;
    private String url;
    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private ImageButton btnUpload;
    private ImageButton btnReturn;
    private String message = null;
    private String[] imagesUrl;
    private int totalSize;
    private int cnt_success = 0;
    private int cnt_failure = 0;
    private Vector fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        btnUpload = (ImageButton) findViewById(R.id.btnUpload);
        btnReturn = (ImageButton) findViewById(R.id.btnReturn);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtPercentage = (TextView) findViewById(R.id.progressText);

        // Changing action bar background color
    //    getActionBar().setBackgroundDrawable(
    //            new ColorDrawable(getResources().getColor(
    //                    R.color.action_bar)));

        // Receiving the data from previous activity
        Intent i = getIntent();


        // image or video path that is captured in previous activity
        imagesUrl = new String[25];
        imagesUrl = i.getStringArrayExtra("filePath");
        fileList = new Vector(Arrays.asList(imagesUrl));
        ip = i.getStringExtra("ip");
        url = "http://"+ ip + "/~Larry/AndroidFileUpload/fileUpload1.php";
        // boolean flag to identify the media type, image or video
        // boolean isImage = i.getBooleanExtra("isImage", true);

 /*       if (filePath != null) {
            // Displaying the image on the screen
            previewMedia();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }*/

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
     //           new FileUploadAsyncTask(UploadActivity.this).execute(url, imagesUrl[0],
     //                   imagesUrl[1], imagesUrl[2], imagesUrl[3], imagesUrl[4], imagesUrl[5], imagesUrl[6], imagesUrl[7]);
                totalSize = fileList.size();
                cnt_success = 0;
                cnt_failure = 0;
                txtPercentage.setText(cnt_success+"/"+totalSize);
                progressBar.setProgress(0);
                AsyncHttpClient client = new AsyncHttpClient();
                String path = null;
                for(Object obj:fileList) {
                    path = (String)obj;
                    File myFile = new File(path);
                    RequestParams para = new RequestParams();
                    try {
                        para.put("image", myFile);

                        client.post(url, para, new MyAsyncHttpResponseHandler(path) {

                            @Override
                            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                                // TODO Auto-generated method stub
                                super.onSuccess(arg0, arg1, arg2);
                                fileList.remove(this.getFilePath());
                                cnt_success ++;
                                txtPercentage.setText(cnt_success+"/"+totalSize);
                                progressBar.setProgress((int)(100 * cnt_success/(float)totalSize ));
                                if(cnt_success + cnt_failure >= totalSize) {
                                    if(cnt_failure > 0)
                                        Toast.makeText(getApplicationContext(), "The upload process is over. Please try again.", Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(getApplicationContext(), "The upload process is over. All successful!", Toast.LENGTH_LONG).show();
                                }
                            }

                            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                                  Throwable arg3) {
                                super.onFailure(arg0, arg1, arg2, arg3);
                                cnt_failure ++;
                                if(cnt_success + cnt_failure >= totalSize) {
                                    if(cnt_failure > 0)
                                        Toast.makeText(getApplicationContext(), "The upload process is over. Please try again.", Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(getApplicationContext(), "The upload process is over. All successful!", Toast.LENGTH_LONG).show();
                                }
                            }

                        });
                    } catch(FileNotFoundException e) {
                        Log.d(TAG, "File not found!!!" + path);
                    }
                }


                /*
                File[] files = {new File(imagesUrl[0]), new File(imagesUrl[1]), new File(imagesUrl[2]), new File(imagesUrl[3]),
                        new File(imagesUrl[4]), new File(imagesUrl[5]), new File(imagesUrl[6]), new File(imagesUrl[7])};

                AsyncHttpClient client = new AsyncHttpClient();
                File myFile = new File(imagesUrl[0]);
                RequestParams params = new RequestParams();
                try {
                    params.put("images[]", files);

                    client.post(url, params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            super.onStart();
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                              Throwable arg3) {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), "failure...!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                            // TODO Auto-generated method stub
                            String response = new String(arg2);
                            Log.i(TAG, response);
                            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                        }

                    });
                } catch(FileNotFoundException e) {
                    Log.d(TAG, "File not found!!!" + imagesUrl[0]);
                }
            */

            }
        });
 //
        btnReturn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            //    getActivity().finish();
                Intent i = new Intent(UploadActivity.this, MainActivity.class);
                i.putExtra("ip", ip);
                startActivity(i);
            }

        });
    }

    private AppCompatActivity getActivity(){
        return this;
    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia() {
        // Checking whether captured media is image or video
        imgPreview.setVisibility(View.VISIBLE);
        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        imgPreview.setImageBitmap(bitmap);
    }

    public String uploadFile(String uploadUrl, String srcPath) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        String message = null;
        boolean ret = true;
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
            Log.e("UploadFile", e.toString());
            e.printStackTrace();
        } catch (SocketTimeoutException e){
            message = e.toString();
            Log.e("UploadFile", e.toString());
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

    private String AsyncUpload(final String uploadUrl, String srcPath, Looper looper) {
        AsyncHttpClient client = new AsyncHttpClient();
        File file = new File(srcPath);
        RequestParams params = new RequestParams();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(looper) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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

