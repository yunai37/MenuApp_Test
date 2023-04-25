package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class ListActivity extends AppCompatActivity {
    private static String ADDRESS_LIST = "http://52.78.72.175/data/restaurant";
    private static String ADDRESS_WISH = "http://52.78.72.175/data/favorite";
    private static String TAG = "List";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static String TAG_BUSINESS = "business_hours";
    private static String TAG_PHONE = "phone_number";
    private static final String TAG_CATEGORY_NAME = "category_name";
    private static final String TAG_IMAGE = "image";
    private static String TAG_RATING = "rating";
    private ListView mlistView;
    private TextView location;
    //private ArrayList<HashMap<String, String>> listItems;
    private ListAdapter adapter;
    private String token, address, mJsonString, rid;
    private boolean Wish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        address = getIntent.getStringExtra("address");

        mlistView = (ListView) findViewById(R.id.listv_list);
        location = findViewById(R.id.list_location);
        location.setText(address);

        GetData task = new GetData();
        task.execute(ADDRESS_LIST, token);
        mlistView.setOnItemClickListener((adapterView, view, i, l) -> {
            ListItem item = (ListItem) adapter.getItem(i);
            rid = String.valueOf(item.getId());
            Intent intent = new Intent(this, RecommendRestaurantActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Rid", rid);
            startActivity(intent);
        });

    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ListActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "response : " + result);

            mJsonString = result;               // 서버의 데이터를 문자열로 읽어와서 변수에 저장
            showResult();

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
                Log.d(TAG, "response code : " + responseStatusCode);

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

    private void showResult() {
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);       // 전체 데이터를 배열에 저장
            adapter = new ListAdapter();

            for (int i = 0; i < jsonArray.length(); i++) {                // 한 그룹{} 씩 읽음
                JSONObject item = jsonArray.getJSONObject(i);       // 해당 그룹의 데이터 하나씩 읽어서 각각의 변수에 저장
                int id = Integer.parseInt(item.getString("id"));
                String name = item.getString(TAG_NAME);
                String address = item.getString(TAG_ADDRESS);
                String business = item.getString(TAG_BUSINESS);
                String phone = item.getString(TAG_PHONE);
                String category_name = item.getString(TAG_CATEGORY_NAME);
                String image = ""; String rating = "0";
                if(!item.getString(TAG_IMAGE).equals("null"))
                    image = item.getString(TAG_IMAGE);
                else image = "null";
                if(!item.getString(TAG_RATING).equals("null"))
                    rating = item.getString(TAG_RATING);
                else rating = "0";
                //String distance = item.getString("distance");
                boolean wish = Boolean.parseBoolean(item.getString("favor"));
                String distance = "70";
                adapter.addRItem(id, name, address, business, phone, category_name, "http://52.78.72.175" + image, rating, distance, wish);
            }
            mlistView.setAdapter(adapter);
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
    class ListAdapter extends BaseAdapter {
        private ArrayList<ListItem> listItems = new ArrayList<ListItem>();
        private Bitmap bitmap;
        public ListAdapter(){
        }

        @Override
        public int getCount(){
            return listItems.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public Object getItem(int position) {
            return listItems.get(position);
        }
        @Override
        public View getView(int position, View view, ViewGroup parent){
            Context context = parent.getContext();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list, parent, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.img_list);
            TextView name = (TextView) view.findViewById(R.id.rname_list);
            TextView address = (TextView) view.findViewById(R.id.address_item_list);
            TextView category_name = (TextView) view.findViewById(R.id.category_list);
            TextView rating = view.findViewById(R.id.star_list);
            CheckBox wish = view.findViewById(R.id.imgbtn_list);

            ListItem listItem = listItems.get(position);

            name.setText(listItem.getName());
            address.setText(listItem.getAddress());
            category_name.setText(listItem.getCategory_name());
            rating.setText(listItem.getRating());

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
                    imageView.setImageBitmap(bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 이미 찜한 음식점일 경우
            if(listItem.getWish()) wish.setChecked(true);
            // 찜하기 기능
            wish.setOnClickListener(v -> {
                String Rid = String.valueOf(listItem.getId());
                PostWish postWish = new PostWish(ListActivity.this);
                postWish.execute(ADDRESS_WISH, Rid, token);
            });
            return view;
        }

        void addRItem(int id, String name, String address, String business_hours, String phone_number, String category_name, String image, String rating, String distance, boolean wish){
            ListItem item = new ListItem();
            item.setId(id);
            item.setName(name);
            item.setAddress(address);
            item.setBusiness_hours(business_hours);
            item.setPhone_number(phone_number);
            item.setCategory_name(category_name);
            item.setImage(image);
            item.setRating(rating);
            item.setDistance(distance);
            item.setWish(wish);

            listItems.add(item);
        }

    }
}