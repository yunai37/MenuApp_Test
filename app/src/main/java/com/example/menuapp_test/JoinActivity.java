package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class JoinActivity extends AppCompatActivity {
    private static String ADDRESS_EMAIL = "http://52.78.72.175/account/checkemail";
    private static String ADDRESS_NICKNAME = "http://52.78.72.175/account/checknickname";
    private static String TAG = "duplicatetest";
    private String duplicate;           // 중복 검사 결과 리턴 변수
    private boolean echeck, pcheck, ncheck;
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

        echeck = false; pcheck = false; ncheck = false;

        idcheck.setOnClickListener(v -> {
            try{
                String Email = email.getText().toString();
                CheckDuplicate checkEmail = new CheckDuplicate();
                checkEmail.execute(ADDRESS_EMAIL, Email, "e");

                duplicate = checkEmail.get();
                if(duplicate.contains("true")) {
                    Toast.makeText(getApplicationContext(), "사용할 수 있는 이메일입니다.", Toast.LENGTH_LONG).show();
                    echeck = true ;
                }
                else {
                    Toast.makeText(getApplicationContext(), "이미 사용 중인 이메일입니다.", Toast.LENGTH_LONG).show();
                    email.setText("");
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        namecheck.setOnClickListener(v -> {
            try{
                String Nickname = nickname.getText().toString();
                CheckDuplicate checkNickname = new CheckDuplicate();
                checkNickname.execute(ADDRESS_NICKNAME, Nickname, "n");

                duplicate = checkNickname.get();
                if(duplicate.contains("true")) {
                    Toast.makeText(getApplicationContext(), "사용할 수 있는 닉네임입니다.", Toast.LENGTH_LONG).show();
                    ncheck = true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "이미 사용 중인 닉네임입니다.", Toast.LENGTH_LONG).show();
                    nickname.setText("");
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        pwcheck.setOnClickListener(v -> {
            if(password.getText().toString().equals(password2.getText().toString())){
                Toast.makeText(JoinActivity.this, "비밀번호가 일치합니다.", Toast.LENGTH_LONG).show();
                pcheck = true;
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
            if(echeck){
                if(pcheck){
                    if(ncheck){
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
                    }
                    else Toast.makeText(getApplicationContext(), "닉네임 중복을 확인해 주세요.", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(getApplicationContext(), "비밀번호가 확인되지 않았습니다.", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(getApplicationContext(), "이메일 중복을 확인해 주세요.", Toast.LENGTH_LONG).show();
        });
    }

    class CheckDuplicate extends AsyncTask<String, Void, String> {
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
/*
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                duplicate = jsonObject.getBoolean("available");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }*/

        }

        @Override
        protected String doInBackground(String... params) {
            String data = params[1];
            String type = params[2];

            String serverURL = params[0];
            String postParameters = "";

            if(type == "e")
                postParameters = "email=" + data ;
            else postParameters = "nickname=" + data;

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
                Log.d(TAG, "CheckDuplicate : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

}