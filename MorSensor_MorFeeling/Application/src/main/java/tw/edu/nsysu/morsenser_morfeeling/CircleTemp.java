package tw.edu.nsysu.morsenser_morfeeling;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import tw.edu.nsysu.dataManage.DataTransform;

public class CircleTemp extends Fragment {
    static LinearLayout content;
    static float data[] = new float[3];
    static FragmentActivity mFragAtvt;
    static CircleProgressView mCircleView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_circletemp, container, false);
        mFragAtvt = getActivity();
        mCircleView = (CircleProgressView) v.findViewById(R.id.circleView);
        mCircleView.setValue(56);
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
        mCircleView.setValue(data[1]);
    }
}