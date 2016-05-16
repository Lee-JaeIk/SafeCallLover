package com.example.sangwook.jsonproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    public static SharedPreferences home_sp;
    public static SharedPreferences.Editor home_editor;
    public static String pageCheck = "";

    Intent safeNumPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String user_safeNum = "";
        safeNumPage = new Intent(this, MainPageActivity.class);


        home_sp = PreferenceManager.getDefaultSharedPreferences(this);
        home_editor = home_sp.edit();
        pageCheck = home_sp.getString("safeNumber", "");


        // pageCheck 가 "" 이 아니면 안심번호를 가지고 있는 것이므로.
        if (pageCheck.equals("")) {
            startActivity(new Intent(getApplicationContext(), FirstPage.class));
            finish();
        } else {
            user_safeNum = pageCheck;
            safeNumPage.putExtra("safeNum", user_safeNum);
            startActivity(safeNumPage);
            finish();
        }

    }
    ///////////////////// Default Method ////////////////////////


}