package com.example.menuapp_test;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewWriteActivity extends AppCompatActivity {
    private static String ADDRESS_POST = "http://52.78.72.175/data/review";
    private ImageButton imageButton;
    private ImageView rimg;
    private TextView rname, menu, date;
    private RatingBar ratingbar;
    private ImageView imageView;
    private EditText review;
    private Button image, good, soso, bad, fast, god, save, cancel;
    private float rating;
    private String menuid, rid, Mname, Rname, imagePath;
    private String token, comment;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        menuid = getIntent.getStringExtra("Mid");
        rid = getIntent.getStringExtra("Rid");
        Mname = getIntent.getStringExtra("Mname");
        Rname = getIntent.getStringExtra("Rname");

        imageButton = findViewById(R.id.imgbtn_review);
        rimg = findViewById(R.id.rimg_review);
        image = findViewById(R.id.btn_image);
        rname = findViewById(R.id.rname_review);
        rname.setText(Rname);
        menu = findViewById(R.id.menu_review);
        menu.setText(Mname);
        date = findViewById(R.id.date_review);
        ratingbar = findViewById(R.id.review_rating);
        review = findViewById(R.id.edit_review);
        good = findViewById(R.id.btn_good);
        soso = findViewById(R.id.btn_soso);
        bad = findViewById(R.id.btn_bad);
        fast = findViewById(R.id.btn_fast);
        god = findViewById(R.id.btn_god);
        save = findViewById(R.id.btn_save);
        cancel = findViewById(R.id.btn_cancel);
        imageView = findViewById(R.id.img_review_write);
        rating = 0;
        comment = "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        date.setText(format.format(mDate));

        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float r, boolean b) {
                rating = r;
            }
        });

        good.setOnClickListener(v -> {
            comment += good.getText().toString() + " ";
            review.setText(comment);
        });
        soso.setOnClickListener(v -> {
            comment += soso.getText().toString() + " ";
            review.setText(comment);
        });
        bad.setOnClickListener(v -> {
            comment += bad.getText().toString() + " ";
            review.setText(comment);
        });
        fast.setOnClickListener(v -> {
            comment += fast.getText().toString() + " ";
            review.setText(comment);
        });
        god.setOnClickListener(v -> {
            comment += god.getText().toString() + " ";
            review.setText(comment);
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ReviewWriteActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
            }
        });

        cancel.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
        });

        save.setOnClickListener(v -> {
            if(rating>0){
                if(!review.getText().toString().equals("리뷰 내용을 입력해 주세요.") && !review.getText().toString().equals("")){
                    String Content = review.getText().toString();
                    String Rating = String.valueOf(rating);
                    String Menuid = String.valueOf(menuid);
                    String Rid = String.valueOf(rid);
                    String Image = "";
                    if(uri.equals("")) Image = "null";
                    else {
                        Image = imagePath;
                    }
                    PostReview postReview = new PostReview();
                    postReview.execute(ADDRESS_POST, Rating, Content, Menuid, Rid, Image, token);
                    /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("token", token);
                    startActivity(intent);*/
                }
                else Toast.makeText(getApplicationContext(), "리뷰 내용을 작성해주세요.", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(getApplicationContext(), "별점을 매겨주세요.", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            // Image 상대경로를 가져온다
            uri = data.getData();
            imageView.setImageURI(uri);
            // Image의 절대경로를 가져온다
            imagePath = getFilePathFromUri(uri);
        }
        // 사진 선택 취소
        else if (requestCode == 1 && resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "사진 선택 취소", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFilePathFromUri(Uri imageUri) { // 이미지 uri를 절대경로로 변환하는 함수
        String imagePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            imagePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return imagePath;
    }

    public class PostReview extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ReviewWriteActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("PostReview", "POST response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String Rating = params[1];
            String Content = params[2];
            String Menu = params[3];
            String Restaurant = params[4];
            String Image = params[5];
            String Token = params[6];

            final String serverURL = params[0];

            final String twoHyphens = "--";
            String[] dataName = {"rating", "content", "menu", "restaurant", "image"};
            String resp = null;

            String lineEnd = "\r\n";
            String boundary = "androidupload";
            File targetFile = new File(params[5]);

            byte[] buffer;
            int maxBufferSize = 5 * 1024 * 1024;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Authorization", "TOKEN " + Token);
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                String delimiter = twoHyphens + boundary + lineEnd; // --androidupload\r\n

                StringBuilder postDataBuilder = new StringBuilder();
                for (int i = 0; i < 5; i++) {
                    postDataBuilder.append(delimiter);
                    postDataBuilder.
                            append("Content-Disposition: form-data; name=\"").
                            append(dataName[i]).append("\"").
                            append(lineEnd).
                            append(lineEnd).
                            append(params[i + 1]).
                            append(lineEnd);
                }

                postDataBuilder.append(delimiter);
                postDataBuilder.
                        append("Content-Disposition: form-data; name=\"").
                        append(dataName[4]).
                        append("\";filename=\"").
                        append(targetFile.getName()).
                        append("\"").append(lineEnd);
                try {
                    DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
                    ds.write(postDataBuilder.toString().getBytes());

                    ds.writeBytes(lineEnd);
                    FileInputStream fStream = new FileInputStream(targetFile);
                    buffer = new byte[maxBufferSize];
                    int length = -1;
                    while ((length = fStream.read(buffer)) != -1) {
                        ds.write(buffer, 0, length);
                    }
                    ds.writeBytes(lineEnd);
                    ds.writeBytes(lineEnd);
                    ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd); // requestbody end
                    fStream.close();

                    ds.flush();
                    ds.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String line = null;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            resp += line;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return resp;
            } catch (IOException e) {
                Log.d("PostReview", " : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
}








