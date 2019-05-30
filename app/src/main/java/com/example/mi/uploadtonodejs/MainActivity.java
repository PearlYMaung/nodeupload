package com.example.mi.uploadtonodejs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.Future;



public class MainActivity extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    Button btnSelect,btnUpload;
    ImageView img;
    String path;
    private String mImageUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.image);
        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
        btnSelect = findViewById(R.id.btn_select);
        btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setVisibility(View.INVISIBLE);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                fintent.setType("image/jpeg");
                try {
                    startActivityForResult(fintent, 100);
                } catch (ActivityNotFoundException e) {
                    Log.e("Tag", "Activity not found Exception : " + e.toString());
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log.e("LoG", path);
                //path = getPathFromURI(data.getData());
                File f = new File(path);
                Future uploading = Ion.with(MainActivity.this)
                        .load("http://192.168.1.28:8080/upload")
                        .setMultipartFile("image","image/jpeg", f)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> response) {
                                if(response != null){
                                    if(response.getHeaders().code()==200){
                                        JsonObject jsonObject = new JsonObject();
                                        Toast.makeText(MainActivity.this, "Success"+jsonObject.getAsString(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else
                                    Toast.makeText(MainActivity.this, "Fail Json"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                         });
                         /*.asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                try {
                                    JSONObject jobj = new JSONObject(result.getResult());
                                    Toast.makeText(getApplicationContext(), jobj.getString("response"), Toast.LENGTH_SHORT).show();

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                    *//*e1.getMessage();
                                    Toast.makeText(MainActivity.this, "JSOnError"+e1.getMessage(), Toast.LENGTH_SHORT).show();*//*
                                }

                            }

                        });*/
            }

        });


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null)
            return;
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    //data.getData();currentImageURI
                    path = getPathFromURI(data.getData(),data);
                    img.setImageURI(data.getData());
                    btnUpload.setVisibility(View.VISIBLE);

                }
        }


    }


    @SuppressLint("NewApi")
    private String getPathFromURI(Uri contentUri,Intent data) {
        String picPath = "hiii";
        String[] proj = { MediaStore.Images.Media.DATA};
        /*CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();*/
        Cursor cursor = getApplicationContext().getContentResolver().query(contentUri,   new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if(cursor != null && cursor.moveToFirst() && cursor.getCount()>0){

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //int aa = cursor.getColumnCount();
            picPath = cursor.getString(column_index);
            //Toast.makeText(this, "Path"+picPath, Toast.LENGTH_LONG).show();
            cursor.close();
        }

        return contentUri.getPath();






    }





    }

