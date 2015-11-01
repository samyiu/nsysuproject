package tw.edu.nsysu.morsenser_morfeeling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * Created by sam on 2015/7/29.
 */
public class History extends AppCompatActivity {

    private ListView listView;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_POST = "posts";
    private List<HashMap<String, Object>> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        listView = (ListView) findViewById(R.id.listview);
        SharedPreferences settings = getSharedPreferences(Login.LOGIN, 0);
        String user = settings.getString(Login.USER, "");
        String pwd = settings.getString(Login.PASS, "");
        get(user);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.row_item, null);
            TextView title = (TextView) convertView.findViewById(R.id.time);
            title.setText(mData.get(position).get("time").toString());
            TextView time = (TextView) convertView.findViewById(R.id.temp);
            time.setText("SpO2" + "\n" + "Heartrate");
            TextView info = (TextView) convertView.findViewById(R.id.info);
            info.setText(mData.get(position).get("spo2").toString()+" %\n"+mData.get(position).get("heartrate").toString() + " bpm");
            ImageView img = (ImageView) convertView.findViewById(R.id.imageView);
            img.setImageResource((Integer) mData.get(position).get("image"));
            return convertView;
        }
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
                nameValuePairs.add(new BasicNameValuePair("all", "yes"));
                //nameValuePairs.add(new BasicNameValuePair("image", String.valueOf(R.drawable.bed)));



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
                            map.put("time",jsonobj.getString("time"));
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
                    MyAdapter adapter = new MyAdapter(History.this);
                    listView.setAdapter(adapter);
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent returnIntent = new Intent(History.this, SPO2.class);
        startActivity(returnIntent);
        finish();

        return super.onOptionsItemSelected(item);
    }
}
