/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tw.edu.nsysu.morsenser_morfeeling;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import tw.edu.nsysu.mic.audiofilefunc;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class MicViewActivity extends Activity {
    private BluetoothAdapter mBluetoothAdapter;
    private static boolean mScanning,dump=false,timerup=true;
    private String datestr="123";

    private static Handler mHandler;
    private Context main = null;
    private String mDeviceAddress;
    private static final String ITEM_TITLE="Item title";
    private static final String ITEM_SUBTITLE="Item Subtitle";
    private static final String ITEM_BACKGROUNDCOLOR="Item Background";
    private static final int REQUEST_ENABLE_BT = 1;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    public static String GET_DATA_SERVICE = "0001625-1212-efde-1523-785feabcd123";
    public static String WRITE_DATA_SERVICE = "0001525-1212-efde-1523-785feabcd123";
    private ListView mListview;
    private TextView mTextview;
    private ArrayList<HashMap<String, Object>> itemList;
    private ArrayList<HashMap<String, Object>> itemList2;
    private static ArrayList<Integer> lostlist=new ArrayList<Integer>();
    Vibrator mVibrator;
    private HashMap<String, Object> item;
    // Stops scanning after 10 seconds.
    static int useconuter=0,counter_int200=200;
    boolean link=false;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private String temp;
    static int count2=0;
    private Long startTime;
    private static final String TAG = "ActivityDemo";
    private Handler handlerTimer = new Handler();
    private static ArrayAdapter<String> mConversationArrayAdapter;
    private static TextView time,counter_200;
    private static Button start,play;
    static String filename;
    static byte[] wavedata=null;
    static int filesize=0;
    boolean getsize=false;

    public static void updatelist(byte[] data){
        //   useconuter +=data.size();
        update(data);
    };

    private void Updateusercounter(){

        if(counter_int200<=0){
            counter_int200=0;
            counter_200.setTextColor(Color.RED);
        }else
            counter_200.setTextColor(Color.BLACK);
        counter_200.setText(Integer.toString(counter_int200));
    }
    private static void writeDateTOFile() {
        Log.e(TAG, "writeDateTOFile");
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        // byte[] audiodata = new byte[16];
        FileOutputStream fos = null;
        int readsize = 0;
        try {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        // while (isRecord == true) {
        if ( fos!=null) {
            try {
                fos.write(wavedata);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //   }
        }
        try {
            if(fos != null)
                fos.close();// 关闭写入流
            time.setText("Finish");
            play.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_mic_view);
        getActionBar().setTitle(R.string.title_activity_mic_view);
        mListview=(ListView)findViewById(R.id.list);
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mListview.setAdapter(mConversationArrayAdapter);
        filename= audiofilefunc.getWavFilePath();
        mHandler = new Handler();
        handlerTimer.postDelayed(updateTimer,1000);
        main = this;
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        time = (TextView) findViewById(R.id.textTimer);
        start=(Button) findViewById(R.id.button);
//        start.setEnabled(false);
        play=(Button) findViewById(R.id.button2);
        play.setEnabled(false);
        counter_200=(TextView)findViewById(R.id.textCounter);

        itemList=new ArrayList<HashMap<String, Object>>();
        //   openDatabase();
        itemList = new ArrayList<HashMap<String, Object>>();

        Log.e(TAG, "onCreate");
        if (savedInstanceState != null) {
            temp = savedInstanceState.getString("temp");
            System.out.println("onCreate: temp = " + temp);
        }
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                updatelist(value);
                mHandler.postDelayed(mRepeatTask2, 1000);
                // start.setText("PLAY");
                start.setEnabled(false);
                play.setEnabled(false);
            }

        });
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mHandler.postDelayed(mRepeatTask2, 1000);
                // start.setText("PLAY");
                Log.e(TAG, filename);
                MediaPlayer mp = new MediaPlayer();
                try {
//                    mp.reset();
                    mp.setDataSource(filename);
                    mp.prepare();
                    mp.start();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
                start.setEnabled(true);
            }
        });



    }
    static int times = (int) (System.currentTimeMillis());
    int rate=0;
    private static void showdblist(byte[] data){
        Log.e(TAG,"data[0]:"+data[0]+" data[1]:"+data[1]+" data[2]:"+data[2]+" data[3]:"+data[3]);

        if(timerup) {
            timerup=false;
            useconuter++;

            int index;
            int head;
            String logdata = "";
            //  for (int i = 0; i < data.length; i++)
            //     logdata = logdata + String.format("%02x", data[i]);
            head = (((int)data[0] & 0x00ff)<<8 | ((int)(data[1]) & 0x00ff));
            if(head==0xf1a4){
                index = (((int)data[2] & 0x00ff) << 8 | ((int)(data[3]) & 0x00ff));

                if(index>filesize)
                {
                    filesize=index;
                    wavedata=new byte[filesize*16];
                    timerup=true;
                    return;
                }
            }


            if(head==0xf2a4)
            {
                index = (((int)data[2] & 0x00ff) << 8 | ((int)(data[3]) & 0x00ff));

                if(index!=count2)
                {
                    for(int lost=count2;lost<index;lost++)
                    {
                        mConversationArrayAdapter.add(String.format("%d", lost));
                        lostlist.add(lost);
                    }
                    System.arraycopy(data, 4, wavedata, index * 16, 16);
                    count2=index+1;
                }else{
                    System.arraycopy(data, 4, wavedata, count2 * 16, 16);
                    time.setText(String.format("%02d%%", (int) (((float) count2 / (float) filesize) * 100)));
                    count2++;
                }
                if((count2>filesize)||(count2==filesize))
                {
                    count2=0;
                    filesize=0;
                    mHandler.postDelayed(mRepeatTask,500);
                }
            }

            if(head==0x13a4)
            {
                if((int)(data[1]&0x00ff)==0xa4)
                {
                    System.arraycopy(data, 3, wavedata, (lostlist.get(0)) * 16, 16);
                    lostlist.remove(0);
                    //   if (!lostlist.isEmpty())
                    mHandler.postDelayed(mRepeatTask, 10);
                }
            }
            timerup=true;
            Log.e(TAG, "update out");
        }
    }

    private static void update(byte[] data){
        showdblist(data);
        //  counter_200.setText(rate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, ".onResume()");

        temp = "xing";
        System.out.println("onResume: temp = " + temp);
        // 切换屏幕方向会导致activity的摧毁和重建
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            System.out.println("屏幕切换");
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, ".onSaveInstanceState()");
        outState.putString("temp", temp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");

    }

    @Override
    protected void onStop() {
// TODO Auto-generated method stub
        super.onStop();
        Log.e(TAG, "onStop");
        handlerTimer.removeCallbacks(updateTimer);
        //    mBluetoothLeService.disconnect();
//        moveTaskToBack(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    };

    //Lost
    private static Runnable mRepeatTask = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "Lost in");
            byte[] a=new byte[20];
            int lost = 0;
            if(!lostlist.isEmpty())
                lost=lostlist.get(0);
            else
            {
                writeDateTOFile();//write to file
                return;
            }
            mConversationArrayAdapter.remove(String.format("%d", lost));

            a[0]=0x13;
            a[1]=(byte) (0xa4 & 0xff);
            a[2]= (byte) ((lost&0x00000ff00)>>8);
            a[3]= ((byte)(lost&0x0000000ff));
            for(int i=4;i<20;i++)
                a[i]=0x00;

            dump=true;
//            mNotifyCharacteristic2.setValue(a);
//            mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic2);

            MainActivity.lost1 = (byte) ((lost&0x00000ff00)>>8);
            MainActivity.lost2 = ((byte)(lost&0x0000000ff));
            MainActivity.SendMorSensorCommands(106);
            Log.e(TAG, "Lost out");
        }
    };

    //Start
    private Runnable mRepeatTask2 = new Runnable()
    {
        public void run() {
            Log.e(TAG, "Start in");
            byte[] a=new byte[20];
            a[0]=(byte) (0xf1 & 0xff);
            a[1]=(byte) (0xa4 & 0xff);
            for(int i=2;i<20;i++)
                a[i]=0x00;
            dump=true;
//            mNotifyCharacteristic2.setValue((byte[]) a);
//            mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic2);

            MainActivity.SendMorSensorCommands(108);
            Log.e(TAG, "Start out");
        }
    };

    static int jump=1;
    static long  timelest=0,ratetotal=0;
    static int ratecounter=0;
    public Runnable updateTimer;
    {
        updateTimer = new Runnable() {
            public void run() {

                if(timerup) {
                    timerup=false;
                    Calendar cal = Calendar.getInstance();
                    Long timenew=cal.getTimeInMillis();
                    Long rate=useconuter/((timenew-timelest)/1000);
                    ratetotal+=rate;
                    if(ratecounter%10==0) {
                        //    time.setText(String.valueOf(ratetotal / 10));
                        ratetotal=0;
                    }
                    counter_200.setText(String.valueOf(rate));
                    timelest=timenew;
                    jump=1;
                    useconuter = 0;
                    timerup=true;
                    ratecounter++;
                }else
                {
                    ratecounter++;
                    jump++;
                }
                handlerTimer.postDelayed(this, 1000);
            }
        };
    }

}