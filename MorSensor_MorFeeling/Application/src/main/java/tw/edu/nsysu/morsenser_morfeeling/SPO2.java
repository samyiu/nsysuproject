package tw.edu.nsysu.morsenser_morfeeling;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import me.relex.circleindicator.CircleIndicator;

public class SPO2 extends AppCompatActivity {

    static Toolbar toolbar;
    static Button buttonIntense;
    static Button buttonWakeup;
    static Button buttonGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spo2);
        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CircleIndicator myIndicator = (CircleIndicator)findViewById(R.id.indicator_spo2);
        ViewPagerAdapterSpO2 adapter = new ViewPagerAdapterSpO2(getSupportFragmentManager());
        ViewPager pager = (ViewPager)findViewById(R.id.pager_spo2);
        pager.setAdapter(adapter);
        myIndicator.setViewPager(pager);

        buttonGoal = (Button)findViewById(R.id.button_goal);
        buttonWakeup = (Button)findViewById(R.id.button_wakeup);
        buttonIntense = (Button)findViewById(R.id.button_intense);    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spo2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_spo2_setting) {
            /*
            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
            finish();
            */
            return true;
        }
        else if(id == R.id.action_spo2_history){
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        Log.e("SPO2 application", "--- ON DESTROY ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
    }

}
