package tw.edu.nsysu.morsenser_morfeeling;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rey.material.widget.Switch;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by sam07 on 2015/10/28.
 */
public class Setting_SPO2 extends AppCompatActivity implements com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener{
    Switch alarm;
    Button logout;
    Button switch_sensor;
    TextView time;
    static boolean setalarm = false;
    static int hourofday = 7;
    static int minuteofhour = 0 ;
    static Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_spo2);
        mActivity = this;
        TextView usr = (TextView)findViewById(R.id.username);
        SharedPreferences settings = getSharedPreferences(Login.LOGIN, 0);
        String user = settings.getString(Login.USER, "");
        String pwd = settings.getString(Login.PASS, "");
        usr.setText(user);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alarm = (Switch)findViewById(R.id.alarm);
        logout = (Button) findViewById(R.id.logout);
        switch_sensor = (Button) findViewById(R.id.switch_sensor);
        time  = (TextView) findViewById(R.id.alarm_time);
        time.setText(String.format("%02d", hourofday) + " : " + String.format("%02d", minuteofhour));


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Login.LOGIN, Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.cancel(1);
                for (int i = 0; i < 3; i++)
                    MainActivity.SendMorSensorStop();
                if(DeviceScanActivity.mDeviceScanActivity!=null)
                    DeviceScanActivity.mDeviceScanActivity.finish();


                Intent intent = new Intent();
                intent.setClass(mActivity, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Setting_SPO2.this.finish();
            }
        });
        switch_sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<3;i++)
                    MainActivity.SendMorSensorStop();
                if(DeviceScanActivity.mDeviceScanActivity!=null)
                    DeviceScanActivity.mDeviceScanActivity.finish();
                Intent intent = new Intent(Setting_SPO2.this,DeviceScanActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Setting_SPO2.this.finish();


            }
        });
        if(setalarm)
            alarm.setChecked(true);
        else
            alarm.setChecked(false);
        alarm.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if(checked){
                    setalarm = true;
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog dpd = TimePickerDialog.newInstance(
                            Setting_SPO2.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            false
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
                else{
                    Intent intent = new Intent(Setting_SPO2.this, AlarmReceiver.class);

                    PendingIntent pi = PendingIntent.getBroadcast(Setting_SPO2.this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    am.cancel(pi);
                }

            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent intent = new Intent(Setting_SPO2.this, SPO2.class);
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent = new Intent(Setting_SPO2.this, SPO2.class);
        startActivity(intent);
        Setting_SPO2.this.finish();


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

        Calendar cal = Calendar.getInstance();

        time.setText(String.format("%02d", hourOfDay)+ " : " + String.format("%02d", minute));
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE,minute);
        hourofday = hourOfDay;
        minuteofhour = minute;
        Intent intent = new Intent(Setting_SPO2.this, AlarmReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(Setting_SPO2.this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

}
