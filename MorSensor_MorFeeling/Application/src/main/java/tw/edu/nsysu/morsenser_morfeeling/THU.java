package tw.edu.nsysu.morsenser_morfeeling;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import me.relex.circleindicator.CircleIndicator;
import tw.edu.nsysu.dataManage.DataTransform;

public class THU extends AppCompatActivity {
    ViewPager pager;
    ViewPagerAdapter adapter;
    public static boolean show_comfort = false;
    public static boolean show_uv= false;
    public static boolean show_sunstroke = false;

    public static boolean isActive = false;
    public static String comfort_status = "";
    public static String uv_status= "";
    public static int sunstroke_status = 0;

    static ProgressBar ComfortProgressBar;
    static ProgressBar UVProgressBar;
    static ProgressBar HeatstrokeProgressBar;
    static TextView comfortnumber ;
    static TextView UVstatus;
    static TextView HeatstrokeStatus;
    static Toolbar toolbar;
    static LinearLayout content;
    static LinearLayout contentuv;
    static LinearLayout contentsunstroke;
    static RelativeLayout click;
    static RelativeLayout clickuv;
    static RelativeLayout clicksunstroke;
    static float data[] = new float[3];
    static THU mn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_thu);
        mn = this;
        comfortnumber = (TextView)findViewById(R.id.comfortNumber);
        ComfortProgressBar = (ProgressBar) findViewById(R.id.ComfortProgressBar);
        UVProgressBar = (ProgressBar) findViewById(R.id.UVProgressBar);
        UVstatus = (TextView)findViewById(R.id.myUVStatus);
        HeatstrokeProgressBar = (ProgressBar) findViewById(R.id.HeatstrokeProgressBar);
        HeatstrokeStatus = (TextView)findViewById(R.id.HeatstrokeStatus);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);

        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.thu_app_icon_small);

        CircleIndicator myIndicator = (CircleIndicator)findViewById(R.id.indicator_unselected_background);
        // Creating The ViewPagerAdapter and Passing Fragment Manager
        adapter =  new ViewPagerAdapter(getSupportFragmentManager());

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Setting the viewPager for the indicator
        myIndicator.setViewPager(pager);

        click = (RelativeLayout)findViewById(R.id.comfort_expand);
        clickuv = (RelativeLayout)findViewById(R.id.uv_expand);
        clicksunstroke = (RelativeLayout)findViewById(R.id.heatstroke_expand);
        content = (LinearLayout)findViewById(R.id.content);
        contentuv = (LinearLayout)findViewById(R.id.contentUV);
        contentsunstroke = (LinearLayout)findViewById(R.id.content_sunstroke);
        //展開內容隱藏
        content.setVisibility(View.GONE);
        contentuv.setVisibility(View.GONE);
        contentsunstroke.setVisibility(View.GONE);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //點擊圖示開始動畫
                toggle_contents(content);
            }
        });
        clickuv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //點擊圖示開始動畫
                toggle_contents(contentuv);
            }
        });
        clicksunstroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //點擊圖示開始動畫
                toggle_contents(contentsunstroke);
            }
        });
    }
    //展開/收起 動畫
    public void toggle_contents(View v){
        if(v.isShown()){
            slide_up(this, v);
            v.setVisibility(View.GONE);
        }
        else{
            v.setVisibility(View.VISIBLE);
            slide_down(this, v);
        }

    }
    //往下展開動畫
    public static void slide_down(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }
    //往上收動畫
    public static void slide_up(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        isActive = true;
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ComfortCalc();
                            UVCalc();
                            HeatstrokeCalc();
                        }
                    });
                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e)
                    {
                    }
                }
            }
        }).start();
    }
    @Override
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
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
            //finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    public static void ComfortCalc(){
        Log.e("Comfort",comfort_status);
        //讀取溫度、濕度、紫外線指數資料
        data = DataTransform.getData();
        //轉換成華氏
        float f = data[1]*9/5 + 32;
        //計算舒適度指數
        double index = f- ( 0.55 - 0.55*data[2]/100 ) *( f - 58 );
        //comfortnumber.setText(new BigDecimal(index).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "");


        if(index >=86) {
            comfortnumber.setText(R.string.l4);

            ComfortProgressBar.setProgress(100);
            if(show_comfort && !comfort_status.equals(mn.getResources().getString(R.string.l4))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.l4);
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                comfort_status = mn.getResources().getString(R.string.l4);
            }

        }
        else if(index >=80) {
            comfortnumber.setText(R.string.l3);
            ComfortProgressBar.setProgress((int) (100/(88-25)*Math.round(index)));
            if(show_comfort && !comfort_status.equals(mn.getResources().getString(R.string.l3))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.l3);
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                comfort_status = mn.getResources().getString(R.string.l3);
            }

        }
        else if(index >= 76) {
            //顯示警告
            comfortnumber.setText(R.string.l2);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
            if(show_comfort &&  !comfort_status.equals(mn.getResources().getString(R.string.l2))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.l2);
                Intent intent = new Intent();
                intent.setClassName("tw.edu.nsysu.morsenser_morfeeling", "THU");
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                comfort_status = mn.getResources().getString(R.string.l2);
            }

        }
        else if(index >=71) {
            comfortnumber.setText(R.string.l1);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
        }
        else if(index >=59) {
            comfortnumber.setText(R.string.l0);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
        }
        else if(index >=51) {
            comfortnumber.setText(R.string.l_1);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
        }
        else if(index >=39) {
            comfortnumber.setText(R.string.l_2);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
            if(show_comfort &&  !comfort_status.equals(mn.getResources().getString(R.string.l_2))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.l_2);
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                comfort_status = mn.getResources().getString(R.string.l_2);
            }

        }
        else if(index >=26) {
            comfortnumber.setText(R.string.l_3);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
            if(show_comfort &&  !comfort_status.equals(mn.getResources().getString(R.string.l_3))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.l_3);
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                comfort_status = mn.getResources().getString(R.string.l_3);
            }

        }
        else  {
            comfortnumber.setText(R.string.l_4);
            ComfortProgressBar.setProgress(0);
            if(show_comfort  &&  !comfort_status.equals(mn.getResources().getString(R.string.l_4))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.l_4);

                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                comfort_status = mn.getResources().getString(R.string.l_4);
            }

        }

    }

    public static void UVCalc(){
        data = DataTransform.getData();
        //危險級
        if(data[0]>=11  ) {
            UVstatus.setText(R.string.superhigh_status);
            UVProgressBar.setProgress(100);
            if(show_uv && !uv_status.equals(mn.getResources().getString(R.string.superhigh_status))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.superhigh_advice);
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                uv_status = mn.getResources().getString(R.string.superhigh_status);
            }
        }
        //過量級
        else if(data[0]>=8 ) {
            UVstatus.setText(R.string.veryhigh_status);
            UVProgressBar.setProgress(Math.round(100.0f / 11.0f * data[0]));
            if(show_uv && !uv_status.equals(mn.getResources().getString(R.string.veryhigh_status))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.veryhigh_advice);
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                uv_status = mn.getResources().getString(R.string.veryhigh_status);
            }
        }
        //高量級
        else if(data[0]>=6) {
            UVstatus.setText(R.string.high_status);
            UVProgressBar.setProgress(Math.round(100.0f / 11.0f * data[0]));
            if(show_uv && !uv_status.equals(mn.getResources().getString(R.string.high_status))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.high_advice);
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                uv_status = mn.getResources().getString(R.string.high_status);
            }
        }
        //中量級
        else if(data[0]>=3) {
            UVstatus.setText(R.string.medium_status);
            UVProgressBar.setProgress(Math.round(100.0f / 11.0f * data[0]));
            if(show_uv && !uv_status.equals(mn.getResources().getString(R.string.medium_status))) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.medium_advice);
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }
                uv_status = mn.getResources().getString(R.string.medium_status);
            }
        }
        //低量級
        else {
            UVstatus.setText(R.string.low_status);
            UVProgressBar.setProgress(Math.round(100.0f / 11.0f * data[0]));
        }
    }
    public static void HeatstrokeCalc(){

        data = DataTransform.getData();
        Log.e("THU",String.valueOf(sunstroke_status));
        //危險等級
        if(data[1]+data[2]*0.1>=45 ) {
            HeatstrokeStatus.setText(R.string.danger_status);
            HeatstrokeProgressBar.setProgress(100);
            if(show_sunstroke &&  sunstroke_status != 4) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.danger_advice);

                    //说明系统中不存在这个activity
                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }

                sunstroke_status = 4;
            }

        }
        //警戒範圍
        else if(data[1]+data[2]*0.1 >=40 && data[1]+data[2]*0.1 < 45) {
            HeatstrokeStatus.setText(R.string.careful_status);
            HeatstrokeProgressBar.setProgress(((int) Math.round(data[1] + data[2] * 0.1) - 35) * 10);
            if(show_sunstroke &&  sunstroke_status != 3) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mn);
                alertDialog.setTitle("小提醒")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage(R.string.careful_advice);

                if (isActive) {
                    alertDialog.show();
                    Vibrator myVibrator = (Vibrator) mn.getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(1000);

                }

                sunstroke_status = 3;
            }
        }

        //需注意
        else if(data[1]+data[2]*0.1 >=35 && data[1]+data[2]*0.1 < 40) {
            HeatstrokeStatus.setText(R.string.warning_status);
            HeatstrokeProgressBar.setProgress(((int) Math.round(data[1] + data[2] * 0.1) - 35) * 10);
        }
        //安全範圍
        else {
            HeatstrokeStatus.setText(R.string.safe_status);
            HeatstrokeProgressBar.setProgress(0);
        }


    }

    @Override
    public  boolean  onKeyDown ( int  keyCode ,  KeyEvent event )  {
        if  ( keyCode  ==  KeyEvent. KEYCODE_BACK )  {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //創建訊息方塊
            builder.setMessage("確定要離開？");
            builder.setTitle("離開");
            builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //dismiss為關閉dialog,Activity還會保留dialog的狀態
                    if(DeviceScanActivity.mDeviceScanActivity!=null)
                        DeviceScanActivity.mDeviceScanActivity.finish();
                    MainActivity.mMainActivity.finish();
                    finish();
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); //dismiss為關閉dialog,Activity還會保留dialog的狀態
                }
            });
            builder.create().show();
            return  false ;
        }

        return  super.onKeyDown ( keyCode ,  event );
    }

    @Override
    public void onDestroy() {

        Log.e("THU application", "--- ON DESTROY ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
        super.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        isActive = false;
    }
}
