package com.example.sangwook.jsonproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/////////////BT//////////

public class MainPageActivity extends Activity {

    /**/
    Intent intent;

    public static TextView tv_situation;


    public static SharedPreferences shared_text;
    public static SharedPreferences.Editor editor_text;

    public static String text_situation = "Phone Number";


    /**/


    AlertDialog dialog;
    AlertDialog.Builder builder;
    String TAG = "MainPageActivity";

    TextView tv_safeNum;

    public static String user_safeNum, user_phoneNum;
    String yourJsonStringUrl;

    HomeActivity homeAct;
    Typeface myTypeface;

    public static NfcAdapter nfcAdapter;
    public static PendingIntent pendingIntent;

    public static final int TYPE_URI = 2;

    boolean goProcess = false;
    ////////////////////////////BT////////////////////////////
    //사용자 정의 함수로 블루투스 활성 상태의 변경 결과를 A{{으로 알려줄 때 식별자로 사용됨(0보다 커야한다)

    static final int REQUEST_ENABLE_BT = 10;
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    //폰의 블루투스 모듈을 사용하기 위한 오브젝트
    BluetoothAdapter mBluetoothAdapter;


    //BluetoothDevice로 기기의 장치정보를 알아낼 수 있는 메소드 및 상태값 알아내기
    //연결하고자 하는 다른 블루투스 기기의 이름, 주소, 연결상태 등의 정보를 조회하는 클래스
    //현재 기기가 아닌 다른 블루투스 기기와 연결 및 정보를 알아낼 때 사용
    BluetoothDevice mRemoteDevice;

    //스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket
    BluetoothSocket mSocket = null;
    public static OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    public static String mStrDelimiter = "\n";
    char mCharDelimiter = '\n';

    Thread mWorkerThread = null;
    byte[] readBuffer;
    int readBufferPosition;

    EditText mEditReceive, mEditSend;

    public static boolean bluetoothConnect = false;


    /* ******************************** 상욱 추가 ******************************** */
    CharSequence[] items;
    public FloatingActionMenu menu1;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;


    ProgressBarCircularIndeterminate progressBarCircularIndeterminate;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    private Handler mUiHandler = new Handler();

    /* ******************************** 상욱 추가 ******************************** */

    TextView safetoptv;
    boolean flagBluetoothWantSetClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);

        safetoptv = (TextView) findViewById(R.id.safetoptv);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "addi.ttf");
        safetoptv.setTypeface(typeface);

        progressBarCircularIndeterminate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndeterminate);
        progressBarCircularIndeterminate.setVisibility(View.INVISIBLE);

        /**/


        intent = new Intent(getApplicationContext(), UpdateActivity.class);
        tv_situation = (TextView) findViewById(R.id.tv_situation);
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "addi.ttf");
        tv_situation.setTypeface(typeface2);

        shared_text = PreferenceManager.getDefaultSharedPreferences(this);
        editor_text = shared_text.edit();

        text_situation = shared_text.getString("tv_situation", "PHONE NUMBER");
        tv_situation.setText(text_situation);


    /* ******************************** 상욱 추가 ******************************** */

        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        menu1 = (FloatingActionMenu) findViewById(R.id.menu1);

       /* final FloatingActionButton programFab1 = new FloatingActionButton(this);
        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab1.setLabelText("블루투스 연결");
        programFab1.setImageResource(R.drawable.love);
        menu1.addMenuButton(programFab1);
        programFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), programFab1.getLabelText(), Toast.LENGTH_SHORT).show();
            }
        });
*/
        menu1.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu1.isOpened()) {
//                    Toast.makeText(FloatingMenusActivity.this, menu1.getMenuButtonLabelText(), Toast.LENGTH_SHORT).show();
                }
                menu1.toggle(true);
            }
        });
        ContextThemeWrapper context = new ContextThemeWrapper(this, R.style.MenuButtonsStyle);

        menus.add(menu1);

        menu1.hideMenuButton(false);

        int delay = 400;
        for (final FloatingActionMenu menu : menus) {
            mUiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    menu.showMenuButton(true);
                }
            }, delay);
            delay += 150;
        }

        menu1.setClosedOnTouchOutside(true);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);


        /* 색 바꿔주는 부분 */

        fab1.setColorNormal(Color.parseColor("#96BF24"));
        fab2.setColorNormal(Color.parseColor("#96BF24"));
        fab3.setColorNormal(Color.parseColor("#96BF24"));
        fab4.setColorNormal(Color.parseColor("#96BF24"));
        fab1.setColorPressed(Color.parseColor("#CC0CAF0C"));
        fab2.setColorPressed(Color.parseColor("#CC0CAF0C"));
        fab3.setColorPressed(Color.parseColor("#CC0CAF0C"));
        fab4.setColorPressed(Color.parseColor("#CC0CAF0C"));


        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);
        fab4.setOnClickListener(clickListener);


    /* ******************************** 상욱 추가 ******************************** */

        homeAct = new HomeActivity();
        builder = new AlertDialog.Builder(this);

        Intent intent = getIntent();
        user_safeNum = intent.getStringExtra("safeNum");

        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        user_phoneNum = telManager.getLine1Number();


        tv_safeNum = (TextView) findViewById(R.id.tv_safeNumber);
        Typeface typeface3 = Typeface.createFromAsset(getAssets(), "addi.ttf");
        tv_safeNum.setTypeface(typeface3);
        String num1 = user_safeNum.substring(0, 3);
        String num2 = user_safeNum.substring(3, 7);
        String num3 = user_safeNum.substring(7, 11);
        String perfectnum = num1 + "-" + num2 + "-" + num3;
        tv_safeNum.setText(perfectnum + "");


        yourJsonStringUrl = "http://58.180.17.47:8088/050ansim/index.jsp?code=4&caller=" + user_safeNum + "&called=" + user_phoneNum + "&subcode=jimin";

        // NFC 관련 객체 생성
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent reintent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, reintent, 0);

        ////////////////////////////BT///////////////////////
//        mEditReceive = (TextView) findViewById(R.id.et_now);
//        mEditSend = (TextView) findViewById(R.id.et_now);


        //  myTypeface = Typeface.createFromAsset(getAssets(), "addi.ttf");

        //  setFont();
        //블루투스 활성화 사키는 메소드
        checkBluetooth();
    }

    public void setFont() {
        tv_safeNum.setTypeface(myTypeface);
        tv_situation.setTypeface(myTypeface);
    }

    // saveBluetooth 값 불러오기
    private String getPreferences() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        return pref.getString("savedBluetooth", "");

    }

    // saveBluetooth 값 저장하기
    private void savePreferences(String wantSave) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("savedBluetooth", wantSave);
        editor.commit();


    }


    /////////////////// User Method ///////////////////

    public void delDatabaseSafeNum(String safeNum) {
        // 안심번호DB 삭제.


    }


    public void delSafeNum() {
        // 안심번호 삭제.
        new AsyncTaskParseJson().execute();


        homeAct.home_editor.putString("safeNumber", "");
        homeAct.home_editor.commit();

        startActivity(new Intent(getApplicationContext(), FirstPage.class));

    }

    /////////////////// Default Method //////////////////////////

  /*  @Override
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

        final String TAG = "AsyncTaskParseJson";

        // set your json string url here


        @Override
        protected void onPreExecute() {
            Log.e(TAG, "myphonenum: " + user_phoneNum);


        }

        @Override
        protected String doInBackground(String... arg0) {

            try {

                // instantiate our json parser
                JsonParser jParser = new JsonParser();

                // get json string from url
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);

                // show the values in our logcat
                Log.e(TAG, "STATUS: " + json.toString());


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {


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

        if (intent != null && goProcess) {
            processTag(intent);
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

                Toast.makeText(getApplicationContext(), "NFC에 안심번호를 재등록했습니다.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
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

    ///////////////////BT////////////////////
    //브루투스 장치의 이름이 주어졌을때 해당 블루투스 장치 객체를 페어링 된 장치 목록에서 찾아내는 코드
    BluetoothDevice getDeviceFromBondedList(String name) {
        //BluetoothDevice : 페어링 된 기기목록을 얻어옴
        BluetoothDevice selectedDevice = null;

        //getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set형식
        //Set형식에서는 n번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾기
        for (BluetoothDevice deivce : mDevices) {
            // getName() : 단말기의 Bluetooth Adapter 이름을 반환
            if (name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }

    // 문자열 전송하는 함수(쓰레드 사용 x)
    public static void sendData(String msg) {
        msg += mStrDelimiter;  // 문자열 종료표시 (\n)
        try {
            // getBytes() : String을 byte로 변환
            // OutputStream.write : 데이터를 쓸때는 write(byte[]) 메소드를 사용함. byte[] 안에 있는 데이터를 한번에 기록해 준다.
            mOutputStream.write(msg.getBytes());  // 문자열 전송.
        } catch (Exception e) {  // 문자열 전송 도중 오류가 발생한 경우
            Toast.makeText(UpdateActivity.context, "데이터 전송중 오류가 발생", Toast.LENGTH_LONG).show();
            //finish();  // App 종료
        }
    }

    //  connectToSelectedDevice() : 원격 장치와 연결하는 과정을 나타냄.
    //        실제 데이터 송수신을 위해서는 소켓으로부터 입출력 스트림을 얻고 입출력 스트림을 이용하여 이루어 진다.
    void connectToSelectedDevice(String selectedDeviceName) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            // 데이터 송수신을 위한 스트림 얻기.
            // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            // 1. 데이터를 보내기 위한 OutputStrem
            // 2. 데이터를 받기 위한 InputStream
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

        } catch (Exception e) { // 블루투스 연결 중 오류 발생
            //Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            //finish();  // App 종료
        }
    }





    // 블루투스 지원하며 활성 상태인 경우.
    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.


        mDevices = mBluetoothAdapter.getBondedDevices();

        boolean successFlag = mBluetoothAdapter.startDiscovery();


        mPariedDeviceCount = mDevices.size();

        if (mPariedDeviceCount == 0) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            //finish(); // App 종료.
        }
        if (getPreferences().equals("Challengers") && flagBluetoothWantSetClick == false) {

            //전에 연결해본적이 있는경우
            connectToSelectedDevice(getPreferences());
            //Toast.makeText(getApplicationContext(),"items : "+items[item].toString(),Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "연결한 블루투스 : " + getPreferences(), Toast.LENGTH_LONG).show();
            bluetoothConnect = true;

        } else {

            // 페어링된 장치가 있는 경우.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("블루투스 장치 선택");

            // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
            List<String> listItems = new ArrayList<String>();
            for (BluetoothDevice device : mDevices) {
                // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
                listItems.add(device.getName());
            }
            listItems.add("취소");  // 취소 항목 추가.


            // CharSequence : 변경 가능한 문자열.
            // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
            items = listItems.toArray(new CharSequence[listItems.size()]);
            // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
            listItems.toArray(new CharSequence[listItems.size()]);

            builder.setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {
                    // TODO Auto-generated method stub
                    if (item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                        Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                        //finish();
                    } else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
//                        connectToSelectedDevice(items[item].toString());

                        new LoadAssync().execute(item);


                        savePreferences(items[item].toString());
                        bluetoothConnect = true;
                        Toast.makeText(getApplicationContext(), "블루투스가 연결되었습니다.", Toast.LENGTH_LONG).show();

                    }

                }

            });

            builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
            AlertDialog alert = builder.create();
            alert.show();

        }


    }


    void checkBluetooth() {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
         이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            //finish();  // 앱종료
        } else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if (!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)
                /**
                 startActivityForResult 함수 호출후 다이얼로그가 나타남
                 "예" 를 선택하면 시스템의 블루투스 장치를 활성화 시키고
                 "아니오" 를 선택하면 비활성화 상태를 유지 한다.
                 선택 결과는 onActivityResult 콜백 함수에서 확인할 수 있다.
                 */
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else { // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();

            }

        }
    }

    // onDestroy() : 어플이 종료될때 호출 되는 함수.
    //               블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
    @Override
    protected void onDestroy() {
        try {
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    // onActivityResult : 사용자의 선택결과 확인 (아니오, 예)
    // RESULT_OK: 블루투스가 활성화 상태로 변경된 경우. "예"
    // RESULT_CANCELED : 오류나 사용자의 "아니오" 선택으로 비활성 상태로 남아 있는 경우  RESULT_CANCELED

    /**
     * 사용자가 request를 허가(또는 거부)하면 안드로이드 앱의 onActivityResult 메소도를 호출해서 request의 허가/거부를 확인할수 있다.
     * 첫번째 requestCode : startActivityForResult 에서 사용했던 요청 코드. REQUEST_ENABLE_BT 값
     * 두번째 resultCode  : 종료된 액티비티가 setReuslt로 지정한 결과 코드. RESULT_OK, RESULT_CANCELED 값중 하나가 들어감.
     * 세번째 data        : 종료된 액티비티가 인테트를 첨부했을 경우, 그 인텐트가 들어있고 첨부하지 않으면 null
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    selectDevice();
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                    Toast.makeText(getApplicationContext(), "블루투스를 연결해주세요.", Toast.LENGTH_LONG).show();
                    //finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /* ******************************** 상욱 추가 ******************************** */

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = "";

            switch (v.getId()) {

                case R.id.fab1:

                    menu1.close(true);

                    text_situation = shared_text.getString("tv_situation", "");
                    tv_situation.setText(text_situation);


                    startActivity(intent);
                    // tv_situation.setText(update.sp_situation.getSelectedItem().toString());
                    break;
                case R.id.fab2:
                    flagBluetoothWantSetClick = true;
                    menu1.close(true);
                    if (bluetoothConnect == true) {
                        Toast.makeText(getApplicationContext(), "이미 블루투스가 연결되어 있습니다.", Toast.LENGTH_LONG).show();
                    }
                    checkBluetooth();

                    break;
                case R.id.fab3:

                    menu1.close(true);
                    builder.setTitle("※안심번호 재등록")
                            .setMessage("안심번호를 재등록하시려면\nNFC 스티커를 태그해주세요.");


                    dialog = builder.create();
                    dialog.show();
                    goProcess = true;
                    break;
                case R.id.fab4:

                    menu1.close(true);
                    builder.setTitle("※안심번호 삭제")
                            .setMessage("안심번호를 삭제하시겠습니까?")
                            .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // db에서 삭제.
                                    delDatabaseSafeNum(user_safeNum);
                                    delSafeNum();
                                    // 튜토리얼 화면으로 전환.
                                    finish();
                                }
                            });
                    dialog = builder.create();
                    dialog.show();
                    break;
            }

        }
    };

    /* ******************************** 상욱 추가 ******************************** */

    private class LoadAssync extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute");
            progressBarCircularIndeterminate.setVisibility(View.VISIBLE);

        }

        //사이트 접속해서 데이터 추출하는 부분
        @Override
        protected Void doInBackground(Integer... params) {
            int identifier = params[0];
            Log.d(TAG, identifier + " !!!");

            try {
                connectToSelectedDevice(items[identifier].toString());
                bluetoothConnect = true;
                Toast.makeText(getApplicationContext(), items[identifier].toString() + "장치와 연결 되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused) {
            Log.d(TAG, "onPostExecute");
            progressBarCircularIndeterminate.setVisibility(View.INVISIBLE);
        }
    }


}