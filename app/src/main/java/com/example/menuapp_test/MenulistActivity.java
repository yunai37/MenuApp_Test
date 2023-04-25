package com.example.menuapp_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MenulistActivity extends AppCompatActivity {
    private static String ADDRESS_MENU = "http://52.78.72.175/data/restaurant/";
    private ListView listView;
    private ImageButton back;
    private MenuItem menuItems;
    private MAdapter adapter;
    private String token, rid, mJsonstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menulist);

        Intent getIntent = getIntent();
        //token = getIntent.getStringExtra("token");
        token = "c3c59d759096918d33e48112477d99e713c57a0d";
        rid = getIntent.getStringExtra("Rid");

        listView = findViewById(R.id.listv_menulist);
        back = findViewById(R.id.imgbtn_menulist);

        GetMenu getMenu = new GetMenu(MenulistActivity.this);
        getMenu.execute(ADDRESS_MENU + rid + "/menu", token);

        try {
            mJsonstring = getMenu.get();
        }catch (ExecutionException | InterruptedException e) {
            Log.d("Menulist", "getMenu : ", e);
        }

        try {
            JSONArray jsonArray = new JSONArray(mJsonstring);       // 전체 데이터를 배열에 저장
            adapter = new MAdapter();

            for (int i = 0; i < jsonArray.length(); i++) {                // 한 그룹{} 씩 읽음
                JSONObject item = jsonArray.getJSONObject(i);       // 해당 그룹의 데이터 하나씩 읽어서 각각의 변수에 저장
                int id = Integer.parseInt(item.getString("id"));
                int restaurant = Integer.parseInt(item.getString("restaurant"));
                String category = item.getString("category");
                String name = item.getString("name");
                String price = item.getString("price");
                String emotion = item.getString("emotion");
                String weather = item.getString("weather");
                boolean checkallergy = Boolean.parseBoolean(item.getString("checkallergy"));
                String image = "";
                if(!item.getString("image").equals("null"))
                    image = item.getString("image");
                else image = "null";
                adapter.addRItem(id, restaurant, category, name, price, emotion, weather, "http://52.78.72.175" + image, checkallergy);
            }

            listView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d("Menulist", "showResult : ", e);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuItem item = (MenuItem) adapter.getItem(i);
                String Mid = String.valueOf(item.getId());
                String Name = item.getName();
                Intent intent = new Intent(adapterView.getContext(), NutritionActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("Mid", Mid);
                intent.putExtra("mname", Name);
                startActivity(intent);
            }
        });

        }
    }
