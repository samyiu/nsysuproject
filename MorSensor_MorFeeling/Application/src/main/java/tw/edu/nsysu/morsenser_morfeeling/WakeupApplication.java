package tw.edu.nsysu.morsenser_morfeeling;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tw.edu.nsysu.dataManage.DataTransform;


public class WakeupApplication extends AppCompatActivity {

    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_POST = "posts";
    private List<HashMap<String, Object>> mData;
    TextView txt_avg;
    static ImageView click;
    static LinearLayout content;

    static FragmentActivity mn;
    Button test;

    int average=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.beat_wakeup);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        test = (Button) findViewById(R.id.intense_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.SendMorSensorCommands(MainActivity.SEND_MORSENSOR_FILE_DATA_SIZE); //SEND_MORSENSOR_FILE_DATA_SIZE
                Intent intent = new Intent(WakeupApplication.this, result.class);
                Bundle b = new Bundle();
                b.putString("from", "wakeup");
                b.putInt("average", average);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });
        txt_avg = (TextView) findViewById(R.id.beat_average);
        /*itemDAO = new ItemHeart_app(getActivity());
        items = itemDAO.getimage();
        if(items.size() > 0 ){
            //Toast.makeText(getActivity(),String.valueOf(items.size()),Toast.LENGTH_LONG).show();
            for(int i=0;i<items.size();i++) {
                average+= Integer.parseInt(items.get(i).get("info").toString());
            }
            average = Math.round(average / items.size());
            Toast.makeText(this, String.valueOf(average), Toast.LENGTH_LONG).show();
            txt_avg.setText(String.valueOf(average) + " bpm");
        }*/
        SharedPreferences settings = getSharedPreferences(Login.LOGIN, 0);
        String user = settings.getString(Login.USER, "");
        String pwd = settings.getString(Login.PASS, "");
        get(user);

        click = (ImageView)findViewById(R.id.click);
        content = (LinearLayout)findViewById(R.id.content);
        content.setVisibility(View.GONE);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle_contents(content);
            }
        });


	}
    public void toggle_contents(View v){
        if(content.isShown()){
            slide_up(this, content);
            content.setVisibility(View.GONE);
        }
        else{
            content.setVisibility(View.VISIBLE);
            slide_down(this, content);
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

    static float data[] = new float[6];
    public static void haha(){
        data = DataTransform.getData();
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
                Log.d("doInBackground", "haha");
                String uname = params[0];

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", uname));
                nameValuePairs.add(new BasicNameValuePair("all", "no"));
                nameValuePairs.add(new BasicNameValuePair("image", String.valueOf(R.drawable.bed)));



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

                    if(mData.size()>0) {
                        for (int i = 0; i < mData.size(); i++) {
                            average+= Float.parseFloat(mData.get(i).get("heartrate").toString());
                        }
                        average = Math.round(average / mData.size());
                        Toast.makeText(WakeupApplication.this, String.valueOf(average), Toast.LENGTH_LONG).show();
                        txt_avg.setText(String.valueOf(average) + " bpm");
                    }
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent returnIntent = new Intent(WakeupApplication.this, SPO2.class);
        startActivity(returnIntent);
        finish();

        return super.onOptionsItemSelected(item);
    }




}
