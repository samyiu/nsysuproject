package tw.edu.nsysu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.edu.nsysu.dataManage.DataTransform;
import tw.edu.nsysu.morsenser_morfeeling.CircleProgressView;
import tw.edu.nsysu.morsenser_morfeeling.MainActivity;
import tw.edu.nsysu.morsenser_morfeeling.R;

public class CircleHeartbeat extends Fragment {
    public static float average = 0.0f;
    static float data[] = new float[6];
    static FragmentActivity mFragAtvt;
    static CircleProgressView mCircleView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_circleheartbeat, container, false);
        mFragAtvt = getActivity();
        //MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_FILE_DATA_SIZE);
        mCircleView = (CircleProgressView) v.findViewById(R.id.circleView_heartbeat);
        mCircleView.setValue(average);
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    mFragAtvt.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawCircleProgress();
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }

            }
        }).start();
    }
    public void drawCircleProgress(){
        //data = DataTransform.getData();
        mCircleView.setValue(average);
    }

    public static void update() {
        data = DataTransform.getData();
        if(data[6]==1024){
            try {
                Thread.sleep(1000);
                //MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_FILE_DATA_SIZE);
            } catch (InterruptedException e) {
                // manage error ...
            }
        }
    }
}
