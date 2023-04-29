package com.example.menuapp_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class RecommendResActivity extends AppCompatActivity {
    private static String ADDRESS_RECOMMEND = "http://52.78.72.175/recommendation/menurecommend";
    private TextView nickname, menu, restaurant, mprice;            // 닉네임, 추천되는 메뉴, 음식점
    Button info, select, re, back;        // 상세정보 보기, 메뉴 이용하기, 재추천, 돌아가기
    private String token, price, weather, emotion, Nickname, rid, menuid, mname, rname;
    private RecommendItem recommendItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_res);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        /*price = getIntent.getStringExtra("price");
        weather = getIntent.getStringExtra("weather");
        emotion = getIntent.getStringExtra("emotion");*/
        Nickname = getIntent.getStringExtra("nickname");

        nickname = findViewById(R.id.name_recommend_res);
        nickname.setText(Nickname);
        menu = findViewById(R.id.menu_recommend_res);
        info = findViewById(R.id.btn_recommend_nut);
        select = findViewById(R.id.btn_recommend_ok);
        re = findViewById(R.id.btn_recommend_re);
        back = findViewById(R.id.btn_recommend_back);
        restaurant = findViewById(R.id.rname_recommend_res);
        mprice = findViewById(R.id.price_recommend_res);

        GetRecommend getRecommend = new GetRecommend(RecommendResActivity.this);
        //getRecommend.execute(ADDRESS_RECOMMEND, price, weather, emotion, token);
        getRecommend.execute(ADDRESS_RECOMMEND, token);

        try {
            JSONArray jsonArray = new JSONArray(getRecommend.get());
            int id = jsonArray.getInt(0);
            String name = jsonArray.getString(1);
            int price = jsonArray.getInt(2);
            int Rid = jsonArray.getInt(3);
            String Rname = jsonArray.getString(4);
            //String image = jsonArray.getString(5);
            String image = "null";

            recommendItem = new RecommendItem(id, Rid, name, price, Rname, image);

            menuid = String.valueOf(recommendItem.getId());
            mname = recommendItem.getName();
            menu.setText(mname);
            rname = recommendItem.getRname();
            restaurant.setText("음식점 : " + rname);
            rid = String.valueOf(recommendItem.getRestaurant());
            String Price = String.valueOf(recommendItem.getPrice());
            mprice.setText("가격 : " + Price + "원");

        } catch (Exception e) {
            Log.d("survey", "Error ", e);
        }

        info.setOnClickListener(v -> {                      // 영양 정보 화면으로 이동
            Intent intent = new Intent(this, NutritionActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Mid", menuid);
            intent.putExtra("Mname", mname);
            startActivity(intent);
        });
        select.setOnClickListener(view -> showPop());

        re.setOnClickListener(v -> {                        // 액티비티 재실행
            Intent intent = new Intent(this, RecommendResActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("price", price);
            intent.putExtra("weather", weather);
            intent.putExtra("emotion", emotion);
            intent.putExtra("nickname", Nickname);
            startActivity(intent);
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }
    void showPop() {
        Toast.makeText(getApplicationContext(), "확정되었습니다!", Toast.LENGTH_SHORT).show();
        // 리뷰 작성 팝업창 (예 - 리뷰 작성 페이지 / 아니오 - 메인 화면)
        Intent intent = new Intent(this, PopupReview.class);
        intent.putExtra("token", token);
        intent.putExtra("Mid", menuid);
        intent.putExtra("Rid", rid);
        intent.putExtra("Rname", rname);
        intent.putExtra("Mname", mname);
        startActivity(intent);
    }
}
