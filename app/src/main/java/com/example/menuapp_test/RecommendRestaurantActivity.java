package com.example.menuapp_test;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RecommendRestaurantActivity extends AppCompatActivity {
    private static String ADDRESS_RESTAURANT = "http://52.78.72.175/data/restaurant";
    private static String ADDRESS_WISH = "http://52.78.72.175/data/favorite";
    private ImageView restaurant;
    private CheckBox wish;
    private TextView name, category, phone, address, time, rating;
    private RecyclerView recyclerView;
    private Button menu, review;
    private String token, rid, mJsonString;
    private ListItem listItem = new ListItem();
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        rid = getIntent.getStringExtra("Rid");

        restaurant = findViewById(R.id.img_restaurant);
        wish = findViewById(R.id.imgbtn_restaurant);
        name = findViewById(R.id.rname_restaurant);
        category = findViewById(R.id.category_restaurant);
        phone = findViewById(R.id.rphone_restaurant);
        address = findViewById(R.id.address_restaurant);
        time = findViewById(R.id.time_restaurant);
        rating = findViewById(R.id.rating_restaurant);
        recyclerView = findViewById(R.id.recyclerv_restaurant);
        menu = findViewById(R.id.menu_restaurant);
        review = findViewById(R.id.review_restaurant);

        GetRestaurant getRestaurant = new GetRestaurant();
        getRestaurant.execute(ADDRESS_RESTAURANT, token);
        try {
            mJsonString = getRestaurant.get();
            int Resid = Integer.parseInt(rid) - 1;
            JSONArray jsonArray = new JSONArray(mJsonString);
            JSONObject item = jsonArray.getJSONObject(Resid);
            int id = Integer.parseInt(item.getString("id"));
            String name = item.getString("name");
            String address = item.getString("address");
            String business = item.getString("business_hours");
            String phone = item.getString("phone_number");
            String category_name = item.getString("category_name");
            String image = ""; String rating = "0";
            if(!item.getString("image").equals("null"))
                image = item.getString("image");
            else image = "null";
            if(!item.getString("rating").equals("null"))
                rating = item.getString("rating");
            else rating = "0";
            //String distance = item.getString("distance");
            boolean Wish = Boolean.parseBoolean(item.getString("favor"));
            //String distance = "70";

            listItem.setId(id); listItem.setName(name); listItem.setBusiness_hours(business);
            listItem.setPhone_number(phone); listItem.setCategory_name(category_name); listItem.setImage("http://52.78.72.175"+image);
            listItem.setWish(Wish); listItem.setRating(rating); listItem.setAddress(address);
        } catch (JSONException e) {
            Log.d("Restaurant", "showResult : ", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(listItem.getWish()) wish.setChecked(true);

        wish.setOnClickListener(v -> {
            String Rid = String.valueOf(listItem.getId());
            PostWish postWish = new PostWish(RecommendRestaurantActivity.this);
            postWish.execute(ADDRESS_WISH, Rid, token);
        });
        if(!listItem.getImage().equals("http://52.78.72.175null")){
            Thread thread = new Thread() {
                @Override
                public void run(){
                    try {
                        URL url = new URL(listItem.getImage());
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
                restaurant.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        name.setText(listItem.getName());
        category.setText(listItem.getCategory_name());
        phone.setText(listItem.getPhone_number());
        address.setText(listItem.getAddress());
        time.setText(listItem.getBusiness_hours());
        rating.setText(listItem.getRating());

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ImageAdapter(images);
        recyclerView.setAdapter(adapter);*/

        menu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenulistActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Rid", rid);
            startActivity(intent);
        });
        review.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewShowActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Rid", rid);
            startActivity(intent);
        });
    }
    private class GetRestaurant extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(RecommendRestaurantActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("Restaurant", "response : " + result);

        }

        @Override
        protected String doInBackground(String... params){
            String serverURL = params[0];
            String Token = params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Authorization", "TOKEN " + Token);
                conn.connect();

                int responseStatusCode = conn.getResponseCode();
                Log.d("Restaurant", "response code : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == conn.HTTP_OK){         // 연결 성공 시
                    inputStream = conn.getInputStream();
                }
                else {                                          // 연결 실패 시
                    inputStream = conn.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                conn.disconnect();

                return sb.toString().trim();

            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

}
