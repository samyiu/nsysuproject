package tw.edu.nsysu.morsenser_morfeeling;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import tw.edu.nsysu.dataManage.DataTransform;
import tw.edu.nsysu.fragment.CircleHeartbeat;


public class GoalApplication extends AppCompatActivity {

    private RadioButton warmup;
    private RadioButton cardio;
    private RadioButton intense;
    private RadioGroup rgroup;
    static ImageView click;
    static LinearLayout content;
    private TextView goal;
     static private TextView heartrate;
    static Button test;
    EditText editage;
    int age=0;
    int max_heartrate = 220 - age;


    int base_heartrate = 75;
    int strength=0;
    int goal_bpm;
    boolean checked = false;
    static FragmentActivity mn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.spo2_goal);
        warmup = (RadioButton) findViewById(R.id.warmup);
        cardio = (RadioButton) findViewById(R.id.cardio);
        intense = (RadioButton) findViewById(R.id.intense);
        rgroup = (RadioGroup) findViewById(R.id.rgroup);

        rgroup.setOnCheckedChangeListener(listener);

        goal = (TextView) findViewById(R.id.goal);
        //heartrate = (TextView) v.findViewById(R.id.heartrate);
        test = (Button)findViewById(R.id.test);
        editage = (EditText)findViewById(R.id.edittext);
        click = (ImageView)findViewById(R.id.click);
        content = (LinearLayout)findViewById(R.id.content);
        content.setVisibility(View.GONE);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(editage.getText().toString().trim())) {
                    Toast.makeText(GoalApplication.this, "請輸入年齡", Toast.LENGTH_LONG).show();
                } else {
                    MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_FILE_DATA_SIZE); //SEND_MORSENSOR_FILE_DATA_SIZE
                    test.setEnabled(false);
                    Intent intent = new Intent(GoalApplication.this, result.class);
                    Bundle b = new Bundle();
                    b.putString("from", "goal");
                    b.putInt("goal", (max_heartrate - base_heartrate) * strength / 100 + base_heartrate);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }


            }
        });


        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle_contents(content);
            }
        });

	}
    public void toggle_contents(View v){
        if(content.isShown()){
            slide_up(GoalApplication.this, content);
            content.setVisibility(View.GONE);
        }
        else{
            content.setVisibility(View.VISIBLE);
            slide_down(GoalApplication.this, content);
        }

    }
    public static void slide_down(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }
    public static void slide_up(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }
    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            if("".equals(editage.getText().toString().trim())) {
                Toast.makeText(GoalApplication.this, "Please Enter your age", Toast.LENGTH_LONG).show();
            }
            else{
                age = Integer.parseInt(editage.getText().toString());
                base_heartrate= Math.round(CircleHeartbeat.average);
                max_heartrate=220-age;
                switch (checkedId) {
                    case R.id.warmup:
                        strength = 50;
                        goal.setText(String.valueOf((max_heartrate - base_heartrate) * strength / 100 + base_heartrate) + "bpm");
                        break;
                    case R.id.cardio:
                        strength = 60;
                        goal.setText( String.valueOf((max_heartrate - base_heartrate) * strength / 100 + base_heartrate)+ "bpm");
                        break;
                    case R.id.intense:
                        strength = 70;
                        goal.setText( String.valueOf((max_heartrate - base_heartrate) * strength / 100 + base_heartrate)+ "bpm");
                        break;

                }
            }


        }

    };
    static float data[] = new float[6];
    public static void haha(){
        data = DataTransform.getData();
        heartrate.setText("目前心率 : " + String.valueOf(data[5]) + " bpm");
        if(data[6] == 1024){
            test.setEnabled(true);
        }
    }
    /*@Override
    public void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                while(true) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            haha();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent returnIntent = new Intent(GoalApplication.this, SPO2.class);
        startActivity(returnIntent);
        finish();

        return super.onOptionsItemSelected(item);
    }




}
