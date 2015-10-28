package tw.edu.nsysu.morsenser_morfeeling;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.rey.material.widget.Switch;

/**
 * Created by sam07 on 2015/10/28.
 */
public class Setting extends AppCompatActivity {
    Switch comfort,sunstroke,uv;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        TextView usr = (TextView)findViewById(R.id.username);
        SharedPreferences settings = getSharedPreferences(Login.LOGIN, 0);
        String user = settings.getString(Login.USER, "");
        String pwd = settings.getString(Login.PASS, "");
        usr.setText(user);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        comfort = (Switch)findViewById(R.id.sw_comfort);
        sunstroke = (Switch)findViewById(R.id.sw_sunstroke);
        uv = (Switch)findViewById(R.id.sw_uv);
        logout = (Button)findViewById(R.id.logout);
        comfort.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if(checked)
                    THU.show_comfort = true;
                else
                    THU.show_comfort = false;
            }
        });
        uv.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if(checked)
                    THU.show_uv = true;
                else
                    THU.show_uv = false;
            }
        });
        sunstroke.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if(checked)
                    THU.show_sunstroke = true;
                else
                    THU.show_sunstroke = false;
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(Login.LOGIN, Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.cancel(1);
                Intent intent = new Intent();
                intent.setClass(Setting.this,Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent = new Intent(this, THU.class);
        startActivity(intent);
        finish();




        return super.onOptionsItemSelected(item);
    }


}
