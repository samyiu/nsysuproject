package tw.edu.nsysu.morsenser_morfeeling;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import java.math.BigDecimal;

import tw.edu.nsysu.dataManage.DataTransform;


public class THUViewActivity extends Activity {


    static TextView tv_Temp,tv_Humi,tv_UV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_thu_view);

        tv_UV = (TextView) findViewById(R.id.tv_UV);
        tv_Temp = (TextView) findViewById(R.id.tv_Temp);
        tv_Humi = (TextView) findViewById(R.id.tv_Humi);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thuview, menu);
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
    }

    static float data[] = new float[3];
    public static void DisplayTHUData(){
        data = DataTransform.getData();
        tv_UV.setText(new BigDecimal(data[0]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //UV
        tv_Temp.setText(new BigDecimal(data[1]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Temp
        tv_Humi.setText(new BigDecimal(data[2]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Humi
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("THUViewActivity", "--- ON DESTROY ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
    }
}
