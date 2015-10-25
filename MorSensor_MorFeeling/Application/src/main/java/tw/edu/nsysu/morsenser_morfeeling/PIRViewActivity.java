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


public class PIRViewActivity extends Activity {

    static TextView tv_PIR, tv_PIR_Result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_pirview);

        tv_PIR = (TextView) findViewById(R.id.tv_PIR);
        tv_PIR_Result = (TextView) findViewById(R.id.tv_PIR_Result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pirview, menu);
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

    static float data[] = new float[1];
    static String mColor = "";
    public static void DisplayPIRData(){
        data = DataTransform.getData();
        tv_PIR.setText(new BigDecimal(data[0]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //PIR

        if(data[0] > 3) tv_PIR_Result.setText("More than 10cm");
        else tv_PIR_Result.setText("Less than 10cm");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("PIRViewActivity", "--- ON DESTROY ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
    }
}
