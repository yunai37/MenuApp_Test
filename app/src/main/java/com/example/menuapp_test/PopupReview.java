package com.example.menuapp_test;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class PopupReview extends Activity {
    private Button ok, skip;
    private String token, mid, rid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_review);

        ok = findViewById(R.id.btn_review);
        skip = findViewById(R.id.btn_skip);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        mid = getIntent.getStringExtra("Mid");
        rid = getIntent.getStringExtra("Rid");

        ok.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewWriteActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Mid", mid);
            intent.putExtra("Rid", rid);
            startActivity(intent);
        });

        skip.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }
}
