package tw.edu.nsysu.morsenser_morfeeling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import tw.edu.nsysu.fragment.CircleHeartbeat;
import tw.edu.nsysu.fragment.CircleSpo2;

public class SPO2 extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_POST = "posts";
    private List<HashMap<String, Object>> mData;
    static Toolbar toolbar;
    static Button buttonIntense;
    static Button buttonWakeup;
    static Button buttonGoal;
    static Button test;

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
        buttonGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SPO2.this, GoalApplication.class);
                startActivity(intent);
            }
        });
        buttonWakeup = (Button)findViewById(R.id.button_wakeup);
        buttonWakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SPO2.this, WakeupApplication.class);
                startActivity(intent);
                finish();
            }
        });
        buttonIntense = (Button)findViewById(R.id.button_intense);
        buttonIntense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SPO2.this, IntenseApplication.class);
                startActivity(intent);
                finish();
            }
        });
        test = (Button)findViewById(R.id.start_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_FILE_DATA_SIZE); //SEND_MORSENSOR_FILE_DATA_SIZE
                Intent intent = new Intent(SPO2.this, result.class);
                startActivity(intent);
                finish();
            }
        });


        SharedPreferences settings = getSharedPreferences(Login.LOGIN, 0);
        String user = settings.getString(Login.USER, "");
        String pwd = settings.getString(Login.PASS, "");
        get(user);
    }

    private void get(final String username) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private android.app.Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loadingDialog = ProgressDialog.show(GetData.this, "Please wait", "Loading...");
            }

            @Override
            protected String doInBackground(String... params) {
                int success;
                Log.d("doInBackground","haha");
                String uname = params[0];

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", uname));
                nameValuePairs.add(new BasicNameValuePair("all", "no"));
                nameValuePairs.add(new BasicNameValuePair("image", String.valueOf(R.drawable.walking)));



                try{
                    Log.d("Getdata!", "starting");
                    // getting product details by making HTTP request
                    JSONObject json1 = jsonParser.makeHttpRequest(
                            "http://140.117.192.74:1337/webservice/getdata.php", "POST", nameValuePairs);

                    // check your log for json response
                    Log.d("Getdata attempt", json1.toString());

                    // json success tag
                    success = json1.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        JSONArray jsonarray  = json1.getJSONArray(TAG_POST);
                        Log.d("Getdata Successful!", jsonarray.length() + " ");


                        mData = new ArrayList<HashMap<String, Object>>();
                        HashMap<String, Object> map = null;
                        for (int i = 0; i < jsonarray.length(); i++) {
                            map = new HashMap<String, Object>();
                            JSONObject jsonobj = jsonarray.getJSONObject(i);
                            map.put("heartrate",jsonobj.getString("heartrate"));
                            map.put("spo2", jsonobj.getString("spo2"));
                            map.put("image", jsonobj.getInt("image"));

                            mData.add(map);
                        }
                        return json1.getString(TAG_MESSAGE);
                    }else{
                        Log.d("Getdata Failure!", json1.getString(TAG_MESSAGE));
                        mData = new ArrayList<HashMap<String, Object>>();
                        return json1.getString(TAG_MESSAGE);


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
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                if(result!=null){

                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    float totalspo2= 0.0f;
                    float totalbeat= 0.0f;
                    if(mData.size()>0) {
                        for (int i = 0; i < mData.size(); i++) {
                            totalspo2 += Float.parseFloat(mData.get(i).get("spo2").toString());
                            totalbeat += Float.parseFloat(mData.get(i).get("heartrate").toString());
                        }
                        CircleSpo2.average = totalspo2 / mData.size();
                        CircleHeartbeat.average = totalbeat / mData.size();
                    }
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username);

    }
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

            Intent intent = new Intent(this, Setting_SPO2.class);
            startActivity(intent);
            finish();

            return true;
        }
        else if(id == R.id.action_spo2_history){
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
            finish();
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
        /*Log.e("SPO2 application", "--- ON DESTROY ---");
        for(int i=0;i<3;i++)
            MainActivity.SendMorSensorStop();*/
    }
    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onResume() {
        super.onResume();

    }

}
