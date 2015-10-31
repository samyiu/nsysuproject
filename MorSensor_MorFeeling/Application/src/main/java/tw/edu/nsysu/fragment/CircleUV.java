package tw.edu.nsysu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.edu.nsysu.dataManage.DataTransform;
import tw.edu.nsysu.morsenser_morfeeling.CircleProgressView;
import tw.edu.nsysu.morsenser_morfeeling.R;

public class CircleUV extends Fragment {
    static LinearLayout content;
    static float data[] = new float[3];
    static FragmentActivity mFragAtvt;
    static CircleProgressView mCircleView;
    static TextView textUV;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_circleuv, container, false);
        mFragAtvt = getActivity();
        mCircleView = (CircleProgressView) v.findViewById(R.id.circleView);
        textUV = (TextView)v.findViewById(R.id.textUV);
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
        data = DataTransform.getData();
        textUV.setText(String.valueOf(data[0]));
        mCircleView.setValue(data[0]*9);
    }
}