package com.everest.altizure.imageuploader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SELECT_FROM_ALBUM_REQUEST_CODE = 300;
    private ImageButton btnSelectFromAlbum;
    private String filePath = null;
    private String ipAddress = "10.89.15.66";
    private EditText edittext;
    private String imagesUrl[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar)));

        Intent i = getIntent();

        if (i.getStringExtra("ip") != null) {
            ipAddress = i.getStringExtra("ip");
        }
        edittext = (EditText)findViewById(R.id.et);
        edittext.setText(ipAddress);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        btnSelectFromAlbum = (ImageButton) findViewById(R.id.btnSelectFromAlbum);
        btnSelectFromAlbum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // select from album
                //      selectFromAlbum();
                imagesUrl = new String[25];
                for (int j = 0; j < 25; j++) {
                    String path = "/storage/sdcard0/" + (j+1) + ".jpg";
                    imagesUrl[j] = new String(path);
                }
                String ip = getIp();
                Intent i = new Intent(MainActivity.this, UploadActivity.class);
                i.putExtra("filePath", imagesUrl);
                i.putExtra("ip", ip);
                //   i.putExtra("isImage", true);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        ipAddress = edittext.getText().toString();
    }

    @Override
    protected void onResume(){
        super.onResume();
        edittext.setText(ipAddress);
    }

    private void selectFromAlbum() {
 /*       Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
     //   intent.setType("image/*");
        startActivityForResult(intent, SELECT_FROM_ALBUM_REQUEST_CODE);*/
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FROM_ALBUM_REQUEST_CODE);

    }

    private String getIp() {
        ipAddress = edittext.getText().toString();
        return ipAddress;
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FROM_ALBUM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Toast.makeText(this, "Data is null!", Toast.LENGTH_LONG).show();
                    return;
                }
                Uri photoUri = data.getData();
                if (photoUri == null) {
                    Toast.makeText(this, "photoUri is null!", Toast.LENGTH_LONG).show();
                    return;
                }
            //    Toast.makeText(this, "photoUri = " + photoUri, Toast.LENGTH_LONG).show();

                String[] pojo = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
                    @SuppressWarnings("deprecation")
                Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                    cursor.moveToFirst();

                    filePath = cursor.getString(columnIndex);
                    cursor.close();
                }
            //    filePath = photoUri.toString();
                Log.i(TAG, "imagePath = " + filePath);
                Toast.makeText(this, "imagePath = " + filePath, Toast.LENGTH_LONG).show();
                if (filePath != null && (filePath.endsWith(".png") || filePath.endsWith(".PNG") || filePath.endsWith(".jpg") || filePath.endsWith(".JPG"))) {
           //         imagesUrl.add(filePath);
             /*       String ip = getIp();
                    Intent i = new Intent(MainActivity.this, UploadActivity.class);
                    i.putExtra("filePath", filePath);
                    i.putExtra("ip", ip);
                 //   i.putExtra("isImage", true);
                    startActivity(i);*/
                } else {
                    Toast.makeText(this, "filePath is null or file is not image", Toast.LENGTH_LONG).show();
                }

            }
        }
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

