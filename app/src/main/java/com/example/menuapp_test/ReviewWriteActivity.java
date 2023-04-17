package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReviewWriteActivity extends AppCompatActivity {
    private static String ADDRESS_POST = "http://52.78.72.175/data/review";
    private ImageButton imageButton;
    private ImageView rimg;
    private TextView rname, menu, date;
    private RatingBar ratingbar;
    private EditText review;
    private Button image, good, soso, bad, fast, god, save, cancel;
    private float rating;
    private int menuid, rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        imageButton = findViewById(R.id.imgbtn_review);
        rimg = findViewById(R.id.rimg_review);
        image = findViewById(R.id.btn_image);
        rname = findViewById(R.id.rname_review);
        menu = findViewById(R.id.menu_review);
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
        rating = 0;
        menuid = 1;
        rid = 1;

        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float r, boolean b) {
                rating = r;
            }
        });

        good.setOnClickListener(v -> {
            review.setText(good.getText().toString());
        });
        soso.setOnClickListener(v -> {
            review.setText(soso.getText().toString());
        });
        bad.setOnClickListener(v -> {
            review.setText(bad.getText().toString());
        });
        fast.setOnClickListener(v -> {
            review.setText(fast.getText().toString());
        });
        god.setOnClickListener(v -> {
            review.setText(god.getText().toString());
        });

        save.setOnClickListener(v -> {
            if(!review.equals("")){
                if(rating>0){
                    String Content = review.getText().toString();
                    String Rating = String.valueOf(rating);
                    String Menuid = String.valueOf(menuid);
                    String Rid = String.valueOf(rid);
                    String image = "image.png";
                    PostReview postReview = new PostReview();
                    postReview.execute(ADDRESS_POST, Rating, Content, Menuid, Rid, image, "49e9d8db7d6d31d3623b4af2d3fb97178d6d773e");
                }
            }
        });

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
            review.setText(result);
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

            String serverURL = params[0];
            String postParameters = "rating=" + Rating + "&content=" + Content + "&menu=" + Menu + "&restaurant=" + Restaurant + "&image=" + Image;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Authorization", "TOKEN " + Token);
                conn.setRequestMethod("POST");
                conn.connect();

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = conn.getResponseCode();
                Log.d("SignupTest : ", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == conn.HTTP_OK || responseStatusCode == 201) {
                    inputStream = conn.getInputStream();
                }
                else {
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine())!= null)  {
                    sb.append(line);
                }

                bufferedReader.close();
                return sb.toString();
            }
            catch (Exception e) {
                Log.d("SignupTest : ", "InsertSignup : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

}








