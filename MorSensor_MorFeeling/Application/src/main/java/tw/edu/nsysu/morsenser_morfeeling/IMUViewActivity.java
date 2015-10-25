package tw.edu.nsysu.morsenser_morfeeling;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.math.BigDecimal;

import tw.edu.nsysu.dataManage.DataTransform;


public class IMUViewActivity extends Activity {
    public static Activity mIMUViewActivity;
    private static final String TAG = "IMUViewActivity";
    private static final boolean D = false;

    static TextView tv_GryoX,tv_GryoY,tv_GryoZ,tv_AccX,tv_AccY,tv_AccZ,tv_MagX,tv_MagY,tv_MagZ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_imu_view);
        if (D) Log.e(TAG, "-- IMUViewActivity --");

        tv_GryoX = (TextView) findViewById(R.id.GryoX);
        tv_GryoY = (TextView) findViewById(R.id.GryoY);
        tv_GryoZ = (TextView) findViewById(R.id.GryoZ);
        tv_AccX = (TextView) findViewById(R.id.AccX);
        tv_AccY = (TextView) findViewById(R.id.AccY);
        tv_AccZ = (TextView) findViewById(R.id.AccZ);
        tv_MagX = (TextView) findViewById(R.id.MagX);
        tv_MagY = (TextView) findViewById(R.id.MagY);
        tv_MagZ = (TextView) findViewById(R.id.MagZ);
    }

    static float data[] = new float[9];
    public static void DisplayIMUData(){
        data = DataTransform.getData();

        tv_GryoX.setText(new BigDecimal(data[0]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Gryo x
        tv_GryoY.setText(new BigDecimal(data[1]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Gryo y
        tv_GryoZ.setText(new BigDecimal(data[2]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Gryo z

        tv_AccX.setText(new BigDecimal(data[3]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Acc x
        tv_AccY.setText(new BigDecimal(data[4]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Acc y
        tv_AccZ.setText(new BigDecimal(data[5]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Acc z

        tv_MagX.setText(new BigDecimal(data[6]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Mag x
        tv_MagY.setText(new BigDecimal(data[7]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Mag y
        tv_MagZ.setText(new BigDecimal(data[8]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue() + ""); //Mag z
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(D) Log.e(TAG, "--- ON DESTROY IMUViewActivity ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();
    }
}

