package com.example.menuapp_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MypageActivity extends AppCompatActivity {
    TextView name, comment, wishlist, record, setting, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        name = findViewById(R.id.name_mypage);
        comment = findViewById(R.id.comment_mypage);
        wishlist = findViewById(R.id.txt_btn_mypage);
        record = findViewById(R.id.txt_btn_mypage2);
        setting = findViewById(R.id.txt_btn_mypage3);
        logout = findViewById(R.id.txt_btn_mypage4);

        /*wishlist.setOnClickListener(v -> {
            Intent intent = new Intent(this, WishlistActivity.class);
            startActivity(intent);
        });

        record.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecordActivity.class);
            startActivity(intent);
        });

        setting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });
        */

        logout.setOnClickListener(v -> {
            Intent intent = new Intent(this, GateActivity.class);
            startActivity(intent);
        });
    }
}

