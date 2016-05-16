package com.example.sangwook.jsonproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

//////////////////////////////////

public class FirstPage extends Activity {

    String TAG = "FirstPage";
    AlertDialog dialog;
    AlertDialog.Builder builder;
    Intent safeNumPage;

    ButtonRectangle btn_regist;
    TextView tv_phoneNum;
    String user_phoneNum;
    public static String user_safeNum = "";
    HomeActivity homeAct;


    public static NfcAdapter nfcAdapter;
    public static PendingIntent pendingIntent;

    public static final int TYPE_URI = 2;

    Intent intent1;
    boolean goProcess = false;
    private int mPreviousVisibleItem;
TextView firstsafe;

    /* ns  add  */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first_page);
        firstsafe = (TextView) findViewById(R.id.firstsafe);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "addi.ttf");
        firstsafe.setTypeface(typeface);
        homeAct = new HomeActivity();

        btn_regist = (ButtonRectangle) findViewById(R.id.btn_regist);
        tv_phoneNum = (TextView) findViewById(R.id.tv_phoneNumber);
        Typeface typefacephone = Typeface.createFromAsset(getAssets(), "addi.ttf");
        tv_phoneNum.setTypeface(typefacephone);

        // Dialog
        builder = new AlertDialog.Builder(this);
        safeNumPage = new Intent(this, MainPageActivity.class);


        // 내 핸드폰 번호 가져오기.
        // 내 핸드폰 번호 가져오기.
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        user_phoneNum = telManager.getLine1Number();
        String num1 = user_phoneNum.substring(0, 3);
        String num2 = user_phoneNum.substring(3, 7);
        String num3 = user_phoneNum.substring(7, 11);
        String perfectnum = num1 + "-" + num2 + "-" + num3;
        tv_phoneNum.setText(perfectnum + "");



        ///////////////////////////////

        // NFC 관련 객체 생성
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("※안심번호 등록")
                        .setMessage("안심번호를 등록하시려면\nNFC 스티커를 태그해주세요.");

                dialog = builder.create();
                dialog.show();



                goProcess = true;
                //getSafeNum();
            }
        });
    }

    public void onRegiBtnClicked(View v) {
        // Thread 생각해보기.


    }

    ////////////////////// User Method ///////////////////////

    public void getSafeNum() {
        // 처음으로 안심번호를 가져옴.
        new AsyncTaskParseJson().execute();

    }

    public void setDataBaseMyNumber(String safeNum, String phoneNum) {

    }

    ///////////////////// Default Method ////////////////////////

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        final String TAG = "MainActivityk";

        // set your json string url here
        String yourJsonStringUrl = "http://58.180.17.47:8088/050ansim/index_assign.jsp?called=" + user_phoneNum + "&term=1&subcode=jimin";


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                // instantiate our json parser
                JsonParser jParser = new JsonParser();

                // get json string from url
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);

                // get the array of users
                user_safeNum = json.getString("RESULT");

                // show the values in our logcat
                Log.e(TAG, "RESULT: " + user_safeNum);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            setDataBaseMyNumber(user_safeNum, user_phoneNum);

            homeAct.home_editor.putString("safeNumber", user_safeNum);
            homeAct.home_editor.commit();

            // 화면전환

            //if(nfcAdapter.ACTION_TAG_DISCOVERED.)
            safeNumPage.putExtra("safeNum", user_safeNum);

            processTag(intent1);// processTag 메소드 호출
            startActivity(safeNumPage);
            finish();
        }
    }

    /**
     * Disable NFC adapter from read mode
     */
    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    /**
     * Enable NFC adapter to read mode
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    // NFC 태그 스캔시 호출되는 메소드
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        intent1 = intent;

        if (intent != null && goProcess) {
            getSafeNum();
            goProcess = false;
        }
    }

    // onNewIntent 메소드 수행 후 호출되는 메소드
    private void processTag(Intent intent) {
        // 감지된 태그를 가리키는 객체
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // 입력받은 값을 감지된 태그에 씀
        NdefMessage message = createTagMessage(user_safeNum, TYPE_URI);
        writeTag(message, detectedTag);
    }


    /**
     * Write a NdefMessage to the detected tag
     *
     * @param message
     * @param tag
     * @return
     */
    // 감지된 태그에 NdefMessage를 쓰는 메소드
    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }

                if (ndef.getMaxSize() < size) {
                    return false;
                }

                ndef.writeNdefMessage(message);

                //ndef.makeReadOnly();

                Toast.makeText(getApplicationContext(), "NFC에 안심번호를 등록했습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "포맷되지 않은 태그이므로 먼저 포맷하고 데이터를 씁니다.", Toast.LENGTH_SHORT).show();

                NdefFormatable formatable = NdefFormatable.get(tag);
                if (formatable != null) {
                    try {
                        formatable.connect();
                        formatable.format(message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            return false;
        }

        return true;
    }

    /**
     * Create a new tag message
     *
     * @param msg
     * @param type
     * @return
     */
    private NdefMessage createTagMessage(String msg, int type) {
        NdefRecord[] records = new NdefRecord[1];
        records[0] = createUriRecord();

        NdefMessage mMessage = new NdefMessage(records);

        return mMessage;
    }

    private NdefRecord createUriRecord() {
        NdefRecord rtdUriRecord = NdefRecord.createUri("tel:" + user_safeNum);
        return rtdUriRecord;
    }


}