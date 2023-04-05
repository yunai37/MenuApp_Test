package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinAllergieActivity extends AppCompatActivity {
    private static String ADDRESS_ALLERGIE = "";
    private static String ADDRESS_SIGNUP = "http://52.78.72.175/account/signup";
    private static String TAG_SIGNUP = "signuptest";
    private static String TAG_ALLERGIE = "allergietest";
    private CheckBox egg, milk, wheat, bean, peanut, fish, meat, shellfish, crab;
    private Button end;
    private TextView txt_result;
    private String allergieres, email, password, nickname, gender, age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_allergie);

        egg = (CheckBox) findViewById(R.id.chk_egg);
        milk = (CheckBox) findViewById(R.id.chk_milk);
        wheat = (CheckBox) findViewById(R.id.chk_wheat);
        bean = (CheckBox) findViewById(R.id.chk_bean);
        peanut = (CheckBox) findViewById(R.id.chk_peanut);
        fish = (CheckBox) findViewById(R.id.chk_fish);
        meat = (CheckBox) findViewById(R.id.chk_meat);
        shellfish = (CheckBox) findViewById(R.id.chk_shellfish);
        crab = (CheckBox) findViewById(R.id.chk_crab);

        end = findViewById(R.id.btn_join_end);
        txt_result = findViewById(R.id.txt_result);

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getintent = getIntent();
                email = getintent.getStringExtra("email");
                password = getintent.getStringExtra("password");
                nickname = getintent.getStringExtra("nickname");
                gender = getintent.getStringExtra("gender");
                age = getintent.getStringExtra("age");

                InsertSignup task = new InsertSignup();
                task.execute(ADDRESS_SIGNUP, email, password, nickname, gender, age);

                sendAllergie(egg, milk, wheat, bean, fish, meat, shellfish, crab);

                Toast.makeText(getApplicationContext(), allergieres, Toast.LENGTH_LONG).show();

                //InsertAllergie task2 = new InsertAllergie();
                //task2.execute(ADDRESS_ALLERGIE, allergieres);

                Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
                startActivity(intent);
            }
        });

    }

    private String sendAllergie(CheckBox egg, CheckBox milk, CheckBox wheat, CheckBox bean,
                                CheckBox fish, CheckBox meat, CheckBox shellfish, CheckBox crab){
        String allergie = "";
        if(egg.isChecked())
            allergie += (egg.getText().toString() + ",");
        if(milk.isChecked())
            allergie += (milk.getText().toString() + ",");
        if(wheat.isChecked())
            allergie += (wheat.getText().toString() + ",");
        if(bean.isChecked())
            allergie += (bean.getText().toString() + ",");
        if(fish.isChecked())
            allergie += (fish.getText().toString() + ",");
        if(meat.isChecked())
            allergie += (meat.getText().toString() + ",");
        if(shellfish.isChecked())
            allergie += (shellfish.getText().toString() + ",");
        if(crab.isChecked())
            allergie += (crab.getText().toString());
        String[] hArr = allergie.split(",");            // ,로 분리해서 배열에 넣어줌

        allergieres = "";
        for(int i=0; i<hArr.length; i++){                   // 리턴 변수에 넣어줌
            if(i == hArr.length-1)                          // 배열의 마지막이면 , 안 붙이고 넣음
                allergieres += hArr[i];
            else allergieres += (hArr[i] + ", ");           // 마지막 아니면 뒤에 , 붙여서 넣음
        }
        return allergieres;
    }

    class InsertSignup extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(JoinAllergieActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            txt_result.setText(result);
            Log.d(TAG_SIGNUP, "POST response - " + result);
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
                Log.d(TAG_SIGNUP, "POST response code - " + responseStatusCode);

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
                Log.d(TAG_SIGNUP, "InsertSignup : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

    /*class InsertAllergie extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(JoinAllergieActivity.this, "Please Wait", null, true, true);
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
            String Allergie = params[1];

            String serverURL = params[0];
            String postParameters = "&allergie=" + Allergie ;

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
