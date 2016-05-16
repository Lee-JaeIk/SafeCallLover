package com.example.sangwook.jsonproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

public class UpdateActivity extends Activity {
    Spinner sp_situation;

    ArrayAdapter<CharSequence> adspin;
    MainPageActivity main;

    ButtonRectangle okBtn;
    ButtonRectangle cancelBtn;
    public static Context context;

    int sp_position;
    String temp_text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(); // Set Background blur
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_update);


        okBtn = (ButtonRectangle) findViewById(R.id.btn_ok);
        cancelBtn = (ButtonRectangle) findViewById(R.id.btn_cancel);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String perfectnow = null;
                String perfectnum = null;


                if (MainPageActivity.bluetoothConnect == true) {
                    perfectnow = temp_text;
                    while (perfectnow.length() < 16) {
                        perfectnow += " ";
                    }
//        MainPageActivity.sendData(MainPageActivity.text_situation + MainPageActivity.user_safeNum);
                    String num1 = MainPageActivity.user_safeNum.substring(0, 3);
                    String num2 = MainPageActivity.user_safeNum.substring(3, 7);
                    String num3 = MainPageActivity.user_safeNum.substring(7, 11);
                    perfectnum = num1 + "-" + num2 + "-" + num3;
                    MainPageActivity.sendData(perfectnow + perfectnum);
                    Toast.makeText(getApplication(), "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else if (MainPageActivity.bluetoothConnect == false)
                {
                    Toast.makeText(getApplication(), "연결된 장치가 없습니다.", Toast.LENGTH_LONG).show();
                    finish();

                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });


        main = new MainPageActivity();


        String[] car_parking = {"PHONE NUMBER", "PARKING A WHILE", "PARKING"};
        for (int i = 0; i < car_parking.length; i++) {
            if (main.text_situation.equals(car_parking[i].toString())) sp_position = i;
        }
        sp_situation = (Spinner) findViewById(R.id.sp_situation);


        adspin = ArrayAdapter.createFromResource(this, R.array.car_parking, android.R.layout.simple_spinner_item);
        adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_situation.setPrompt("운전자상태를 선택하세요.");
        sp_situation.setAdapter(adspin);

        sp_situation.setSelection(sp_position);
        sp_situation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temp_text = (String) adspin.getItem(position);
                main.tv_situation.setText(temp_text + "");
                main.editor_text.putString("tv_situation", temp_text);
                main.editor_text.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }// main




}
