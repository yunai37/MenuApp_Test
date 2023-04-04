package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {
    private static String ADDRESS = "http://52.78.72.175/account/signup";
    private static String TAG = "jointest";
    private TextInputEditText email, password, password2, nickname, age, gender;
    private Button idcheck, pwcheck, namecheck, next;
    private TextView txt_result;
    //private Spinner ageS, genderS;
    //private String age, gender;
    //String[] ageitems = getResources().getStringArray(R.array.출생연도);
    //String[] genderitems = getResources().getStringArray(R.array.성별);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        email = findViewById(R.id.join_id);
        password = findViewById(R.id.join_pw);
        password2 = findViewById(R.id.join_pw2);
        nickname = findViewById(R.id.join_name);
        idcheck = findViewById(R.id.btn_join_id);
        pwcheck = findViewById(R.id.btn_join_pw);
        namecheck = findViewById(R.id.btn_join_name);
        next = findViewById(R.id.btn_join_1);
        // ageS = findViewById(R.id.spinner_year);
        // genderS = findViewById(R.id.spinner_gender);
        age = findViewById(R.id.join_age);
        gender = findViewById(R.id.join_gender);
        txt_result = findViewById(R.id.txt_result);

        pwcheck.setOnClickListener(v -> {
            if(password.getText().toString().equals(password2.getText().toString())){
                Toast.makeText(JoinActivity.this, "비밀번호가 일치합니다.", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(JoinActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
            }
        });
/*
        // spinner
        ArrayAdapter<String> yearAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, ageitems);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageS.setAdapter(yearAdapter);

        ArrayAdapter<CharSequence> genderAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genderitems);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderS.setAdapter(genderAdapter);

        ageS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                age = ageS.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                age = ageS.getSelectedItem().toString();
            }
        });

        genderS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = genderS.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                gender = genderS.getSelectedItem().toString();
            }
        });*/

        next.setOnClickListener(view -> {

            String Email = ""; String Password = ""; String Nickname = ""; String Gender = ""; String Age = "";
            Email = email.getText().toString();
            Password = password.getText().toString();
            Nickname = nickname.getText().toString();
            Gender = gender.getText().toString();
            Age = age.getText().toString();

            Intent intent = new Intent(getApplicationContext(), JoinAllergieActivity.class);
            intent.putExtra("email", Email);
            intent.putExtra("password", Password);
            intent.putExtra("nickname", Nickname);
            intent.putExtra("gender", Gender);
            intent.putExtra("age", Age);
            startActivity(intent);
        });
    }

    /*class InsertSignup extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(JoinActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            txt_result.setText(result);
            Log.d(TAG, "POST response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String Email = params[1];
            String Password = params[2];
            String Nickname = params[3];
            String Gender = params[4];
            String Age = params[5];

            String serverURL = params[0];
            String postParameters = "email=" + Email + "&password=" + Password + "&nickname=" + Nickname + "&gender=" + Gender + "&age=" + Age ;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.connect();

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = conn.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == conn.HTTP_OK) {
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
                Log.d(TAG, "InsertData : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }*/

}