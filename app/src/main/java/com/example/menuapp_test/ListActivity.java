package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {
    private static String ADDRESS = "http://52.78.72.175/data/restaurant";
    private static String TAG = "data";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_CATEGORY_NAME = "category_name";
    private static final String TAG_IMAGE = "image";
    Button pre;
    ListView mlistView;
    ArrayList<HashMap<String, String>> listItems;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        pre = findViewById(R.id.btn_list);
        mlistView = (ListView) findViewById(R.id.listv_list);
        listItems = new ArrayList<>();

        GetData task = new GetData();
        task.execute(ADDRESS);

        pre.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
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

            for (int i = 0; i < jsonArray.length(); i++) {                // 한 그룹{} 씩 읽음
                JSONObject item = jsonArray.getJSONObject(i);       // 해당 그룹의 데이터 하나씩 읽어서 각각의 변수에 저장
                String name = item.getString(TAG_NAME);
                String address = item.getString(TAG_ADDRESS);
                String category_name = item.getString(TAG_CATEGORY_NAME);
                String image = item.getString(TAG_IMAGE);

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_ADDRESS, address);
                hashMap.put(TAG_CATEGORY_NAME, category_name);
                hashMap.put(TAG_IMAGE, image);                      // hashmap에 짝 지어 넣음

                listItems.add(hashMap);                             // 데이터 저장된 최종 변수
            }
            ListAdapter adapter = new SimpleAdapter(
                    ListActivity.this, listItems, R.layout.item_list,
                    new String[]{TAG_NAME, TAG_ADDRESS, TAG_CATEGORY_NAME, TAG_IMAGE},
                    new int[]{R.id.rname_list, R.id.address_item_list, R.id.category_list, R.id.img_list}
            );
            mlistView.setAdapter(adapter);                          // xml에서의 출력을 위한 리스트뷰에 넣어줌


        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}