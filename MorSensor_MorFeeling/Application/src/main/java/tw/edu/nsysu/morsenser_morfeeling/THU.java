package tw.edu.nsysu.morsenser_morfeeling;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;

import me.relex.circleindicator.CircleIndicator;
import tw.edu.nsysu.dataManage.DataTransform;

public class THU extends AppCompatActivity {
    ViewPager pager;
    ViewPagerAdapter adapter;
    public static boolean show_comfort = false;
    public static boolean show_uv= false;
    public static boolean show_sunstroke = false;
    public static boolean comfort_warning = false;
    public static boolean uv_warning= false;
    public static boolean sunstroke_warning = false;
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
    static ImageView click;
    static ImageView clickuv;
    static ImageView clicksunstroke;
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
        toolbar.setLogo(R.drawable.thu_app_icon_small);

        CircleIndicator myIndicator = (CircleIndicator)findViewById(R.id.indicator_unselected_background);
        // Creating The ViewPagerAdapter and Passing Fragment Manager
        adapter =  new ViewPagerAdapter(getSupportFragmentManager());

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Setting the viewPager for the indicator
        myIndicator.setViewPager(pager);

        click = (ImageView)findViewById(R.id.clickcomfort);
        clickuv = (ImageView)findViewById(R.id.clickuv);
        clicksunstroke = (ImageView)findViewById(R.id.clicksunstroke);
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
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    public static void ComfortCalc(){
        //讀取溫度、濕度、紫外線指數資料
        data = DataTransform.getData();
        //轉換成華氏
        float f = data[1]*9/5 + 32;
        //計算舒適度指數
        double index = f- ( 0.55 - 0.55*data[2]/100 ) *( f - 58 );
        comfortnumber.setText(new BigDecimal(index).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + "");


        if(index >=86) {
            //status.setText(R.string.l4);
            ComfortProgressBar.setProgress(100);
        }
        else if(index >=80) {
            //status.setText(R.string.l3);
            ComfortProgressBar.setProgress((int) (100/(88-25)*Math.round(index)));
        }
        else if(index >=76) {
            //顯示警告
            //status.setText(R.string.l2);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
        }
        else if(index >=71) {
            //status.setText(R.string.l1);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
        }
        else if(index >=59) {
            //status.setText(R.string.l0);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
        }
        else if(index >=51) {
            //status.setText(R.string.l_1);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
        }
        else if(index >=39) {
            //status.setText(R.string.l_2);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
        }
        else if(index >=26) {
            //status.setText(R.string.l_3);
            ComfortProgressBar.setProgress((int) (100 / (88 - 25) * Math.round(index)));
        }
        else  {
            //status.setText(R.string.l_4);
            ComfortProgressBar.setProgress(0);
        }

    }
    public static void UVCalc(){
        data = DataTransform.getData();
        //危險級
        if(data[0]>=11) {
            UVstatus.setText(R.string.superhigh_status);
            UVProgressBar.setProgress(100);
        }
        //過量級
        else if(data[0]>=8) {
            UVstatus.setText(R.string.veryhigh_status);
            UVProgressBar.setProgress(Math.round(100.0f / 11.0f * data[0]));
        }
        //高量級
        else if(data[0]>=6) {
            UVstatus.setText(R.string.high_status);
            UVProgressBar.setProgress(Math.round(100.0f / 11.0f * data[0]));
        }
        //中量級
        else if(data[0]>=3) {
            UVstatus.setText(R.string.medium_status);
            UVProgressBar.setProgress(Math.round(100.0f / 11.0f * data[0]));
        }
        //低量級
        else {
            UVstatus.setText(R.string.low_status);
            UVProgressBar.setProgress(Math.round(100.0f / 11.0f * data[0]));
        }
    }
    public static void HeatstrokeCalc(){
        String message = "";
        data = DataTransform.getData();
        //危險等級
        if(data[1]+data[2]*0.1>40) {
            HeatstrokeStatus.setText(R.string.danger_status);
            HeatstrokeProgressBar.setProgress(100);

        }
        //警戒範圍
        else if(data[1]+data[2]*0.1 >=35) {
            HeatstrokeStatus.setText(R.string.careful_status);
            HeatstrokeProgressBar.setProgress(((int)Math.round(data[1]+data[2]*0.1) - 30)*10);
        }

        //需注意
        else if(data[1]+data[2]*0.1 >=30) {
            HeatstrokeStatus.setText(R.string.warning_status);
            HeatstrokeProgressBar.setProgress(((int) Math.round(data[1] + data[2] * 0.1) - 30) * 10);
        }
        //安全範圍
        else {
            HeatstrokeStatus.setText(R.string.safe_status);
            HeatstrokeProgressBar.setProgress(0);
        }
        

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
           this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("THU application", "--- ON DESTROY ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
    }
}
