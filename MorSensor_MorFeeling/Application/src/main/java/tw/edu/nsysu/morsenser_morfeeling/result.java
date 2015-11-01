package tw.edu.nsysu.morsenser_morfeeling;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tw.edu.nsysu.dataManage.DataTransform;


/**
 * Created by sam on 2015/8/20.
 */
public class result extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_POST = "posts";
    static ProgressBar myProgressBar;
    static CircleProgressView mCircleView;
    static float spo2 = 0, heart = 0;
    static Button save;
    static Button retest;
    static int goal;
    static int average;
    static int average_beat;
    static String from;
    static Context mcontext;
    static TextView txt_heart;
    static float data[] = new float[3];
    int choice = 0;
    private ActionBar actionBar;
    private RadioButton normal;
    private RadioButton beforesleep;
    private RadioButton after;
    private RadioGroup rgroup;
    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            switch (checkedId) {
                case R.id.normal:
                    choice = 1;
                    break;
                case R.id.beforesleep:
                    choice = 2;
                    break;
                case R.id.after:
                    choice = 3;
                    break;
                default:
                    choice = 0;

            }

        }

    };

    public static void haha() {
        data = DataTransform.getData();
        mCircleView.setValueAnimated(data[4]);
        if (data[6] == 1024) {
            Toast.makeText(mcontext, "測量完畢!", Toast.LENGTH_LONG).show();
            txt_heart.setText("心率(HR) : " + String.valueOf(Math.round(data[5])) + "bpm");
            mCircleView.setValue(0);
            mCircleView.setValueAnimated(data[4]);
            if (data[5] <= 50)
                myProgressBar.setProgress(0);
            else if (data[5] >= 150)
                myProgressBar.setProgress(100);
            else
                myProgressBar.setProgress(Math.round(data[5] - 50));
            spo2 = data[4];
            heart = data[5];
            retest.setEnabled(true);
            save.setEnabled(true);
            if (from != null && from.equals("goal")) {
                if (heart < goal) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setMessage("您尚未達到目標心率，表示目前的運動強度需要再提升，請繼續加油！");

                    builder.setTitle("Warning");

                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setMessage("恭喜您已經達成目標心率了！請繼續保持。");

                    builder.setTitle("Excellent");

                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            } else if (from != null && from.equals("intense")) {

                if (spo2 < average) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setMessage("您的血氧濃度降低，表示這次運動過於激烈，建議您做適當休息並在下一次運動時降低強度。");

                    builder.setTitle("Warning");

                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setMessage("這次的運動做得不錯唷！請繼續保持。");

                    builder.setTitle("Excellent");

                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            } else if (from != null && from.equals("wakeup")) {

                if ((heart - average_beat) > average_beat * 0.05) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setMessage("您的身體處於疲勞狀態，建議您不要過度勞累，並做適當的休息。");

                    builder.setTitle("Warning");

                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                    builder.setMessage("您的身體處於良好狀態，請繼續保持活力迎接新的一天！");

                    builder.setTitle("Excellent");

                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);


        mcontext = result.this;
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        rgroup = (RadioGroup) findViewById(R.id.rgroup);
        rgroup.setOnCheckedChangeListener(listener);
        save = (Button) findViewById(R.id.save);
        retest = (Button) findViewById(R.id.retest);
        txt_heart = (TextView) findViewById(R.id.heart);
        Bundle b = getIntent().getExtras();
        if (null != b) { //Null Checking
            from = b.getString("from");
            if (from.equals("goal"))
                goal = b.getInt("goal");
            else if (from.equals("intense"))
                average = b.getInt("average");
            else if (from.equals("wakeup"))
                average_beat = b.getInt("average");

        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choice != 0) {
                    //TODO INSERT DATA
                    SharedPreferences settings = getSharedPreferences(Login.LOGIN, 0);
                    String user = settings.getString(Login.USER, "");
                    String pwd = settings.getString(Login.PASS, "");
                    String image = "";
                    if (choice == 1)
                        image = String.valueOf(R.drawable.walking);
                    else if (choice == 2)
                        image = String.valueOf(R.drawable.bed);
                    else
                       image = String.valueOf(R.drawable.flexions);
                    insert(user,String.valueOf(spo2),String.valueOf(heart),image);
                }

                Intent returnIntent = new Intent(result.this, SPO2.class);
                startActivity(returnIntent);
                finish();

            }
        });
        retest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_FILE_DATA_SIZE); //SEND_MORSENSOR_FILE_DATA_SIZE
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(result.this, result.class);
                startActivity(intent);


            }
        });
        retest.setEnabled(false);
        save.setEnabled(false);
        mCircleView.setValue(0);


    }
    private void insert(final String username,final String spo2, final String heartrate, final String image) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private android.app.Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loadingDialog = ProgressDialog.show(result.this, "Please wait", "Loading...");
            }

            @Override
            protected String doInBackground(String... params) {
                int success;
                Log.d("doInBackground", "haha");
                String uname = params[0];
                String spo2 = params[1];
                String heartrate = params[2];
                String image = params[3];

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", uname));
                nameValuePairs.add(new BasicNameValuePair("heartrate", heartrate));
                nameValuePairs.add(new BasicNameValuePair("spo2", spo2));
                nameValuePairs.add(new BasicNameValuePair("image",image));
                nameValuePairs.add(new BasicNameValuePair("time",getLocaleDatetime()));



                try{
                    Log.d("INSERT!", "starting");
                    // getting product details by making HTTP request
                    JSONObject json2 = jsonParser.makeHttpRequest(
                            "http://140.117.192.74:1337/webservice/insertdata.php", "POST", nameValuePairs);

                    // check your log for json response
                    Log.d("INSERT attempt", json2.getString(TAG_MESSAGE));

                    // json success tag
                    success = json2.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        Log.d("INSERT Successful!",json2.getString(TAG_MESSAGE) );


                        return json2.getString(TAG_MESSAGE);
                    }else{
                        Log.d("INSERT Failure!", json2.getString(TAG_MESSAGE));
                        return json2.getString(TAG_MESSAGE);


                    }


                }  catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result){
                Log.d("onPostExecute", "haha");

                //loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                if(result!=null){

                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username,spo2,heartrate,image);

    }
    public static String getLocaleDatetime() {
        return String.format(Locale.getDefault(), "%tF  %<tR", new Date());
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent returnIntent = new Intent(this, SPO2.class);
        startActivity(returnIntent);
        finish();


        return super.onOptionsItemSelected(item);
    }

}
