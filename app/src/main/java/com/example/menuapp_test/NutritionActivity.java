package com.example.menuapp_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class NutritionActivity extends AppCompatActivity {
    private static String ADDRESS_NUTRITION = "http://52.78.72.175/data/menu/";
    private TextView name, allergie, gram, kcal, carbo, protein, fat, sodium, potash, chole,
                    satur, unsat, ingredient;
    private String token, mid, mname, mJsonstring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        mid = getIntent.getStringExtra("Mid");
        mname = getIntent.getStringExtra("mname");

        name = findViewById(R.id.name_nutrition);
        name.setText(mname);
        allergie = findViewById(R.id.allergie_nutrition);
        gram = findViewById(R.id.gram_nutrition);
        kcal = findViewById(R.id.kcal_nutrition);
        carbo = findViewById(R.id.carbo_nutrition);
        protein = findViewById(R.id.protein_nutrition);
        fat = findViewById(R.id.fat_nutrition);
        sodium = findViewById(R.id.potash_nutrition);
        chole = findViewById(R.id.chole_nutrition);
        satur = findViewById(R.id.satur_nutrition);
        unsat = findViewById(R.id.unsat_nutrition);
        potash = findViewById(R.id.potash_nutrition);
        ingredient = findViewById(R.id.ingredient_nutrition);

        GetNutrition getNutrition = new GetNutrition(NutritionActivity.this);
        getNutrition.execute(ADDRESS_NUTRITION + mid + "/nutrition", token);

        try {
            mJsonstring = getNutrition.get();
        }catch (ExecutionException | InterruptedException e) {
            Log.d("Menulist", "getMenu : ", e);
        }

        try {
            JSONArray jsonArray = new JSONArray(mJsonstring);
            JSONObject item = jsonArray.getJSONObject(0);
            int Id = Integer.parseInt(item.getString("id"));
            String Name = item.getString("name");
            String Gram = item.getString("gram");
            String Calorie = item.getString("calorie");
            String Carbohydrate = item.getString("carbohydrate");
            String Protein = item.getString("protein");
            String Fat = item.getString("fat");
            String Saturatedfat = item.getString("saturatedfat");
            String Unsaturatedfat = item.getString("unsaturatedfat");
            String Cholesterol = item.getString("cholesterol");
            String Sodium = item.getString("sodium");
            String Potash = item.getString("potash");
            String Ingredient = item.getString("ingredient");
            String Allergy = item.getString("allergy");
            String Menu = item.getString("menu");

            if(!Name.equals("null"))
                name.setText(Name);
            else name.setText("-");
            if(!Gram.equals("null"))
                gram.setText(Gram);
            else gram.setText("-");
            if(!Calorie.equals("null"))
                kcal.setText(Gram);
            else kcal.setText("-");
            if(!Carbohydrate.equals("null"))
                carbo.setText(Gram);
            else carbo.setText("-");
            if(!Protein.equals("null"))
                protein.setText(Gram);
            else protein.setText("-");
            if(!Fat.equals("null"))
               fat.setText(Gram);
            else fat.setText("-");
            if(!Saturatedfat.equals("null"))
                satur.setText(Gram);
            else satur.setText("-");
            if(!Unsaturatedfat.equals("null"))
                unsat.setText(Gram);
            else unsat.setText("-");
            if(!Cholesterol.equals("null"))
                chole.setText(Gram);
            else chole.setText("-");
            if(!Sodium.equals("null"))
                sodium.setText(Gram);
            else sodium.setText("-");
            if(!Potash.equals("null"))
                potash.setText(Gram);
            else potash.setText("-");
            if(!Ingredient.equals("null"))
                ingredient.setText(Ingredient);
            else ingredient.setText("-");
            if(!Allergy.equals("null"))
                allergie.setText(Allergy);
            else allergie.setText("-");

        } catch (JSONException e) {
            Log.d("Menulist", "showResult : ", e);
        }
    }
}
