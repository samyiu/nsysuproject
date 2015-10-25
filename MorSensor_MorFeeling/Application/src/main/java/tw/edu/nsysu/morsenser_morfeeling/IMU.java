package tw.edu.nsysu.morsenser_morfeeling;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

//import android.support.v7.app.ActionBarActivity;

public class IMU extends Activity {

    int temp[];
    int temp2[];
    private Canvas canvas;
    private Paint p ;
    private int height,width;
    public static int check1=0,check2=0;
    private CheckBox a_x;
    private CheckBox a_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_imu);
        //LinearLayout layout=(LinearLayout) findViewById(R.id.root);
        a_x = (CheckBox)findViewById(R.id.chkA_x);
        a_y = (CheckBox)findViewById(R.id.chkA_y);
        a_x.setOnCheckedChangeListener(chklistener);
        a_y.setOnCheckedChangeListener(chklistener);

        /*final DrawView view=new DrawView(this);

        //view.setMinimumHeight(500);
        //view.setMinimumWidth(300);
        view.invalidate();
        layout.addView(view);
        /*LinearLayout layout1=(LinearLayout) findViewById(R.id.text);
        final Drawtext viewtext=new Drawtext(this);
        viewtext.invalidate();
        layout1.addView(viewtext);*/
    }

    private CheckBox.OnCheckedChangeListener chklistener = new CheckBox.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if(a_x.isChecked())
                check1=1;
            else
                check1=0;
            if(a_y.isChecked())
                check2=1;
            else
                check2=0;
        }

    };
    public static void haha(){

    }


}


