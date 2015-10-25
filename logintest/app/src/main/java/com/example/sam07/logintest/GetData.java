package com.example.sam07.logintest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by sam07 on 2015/10/24.
 */
public class GetData extends AppCompatActivity {
    JSONParser jsonParser = new JSONParser();
    //JSONObject json;
    //JSONArray jsonarray;
    String username;
    ListView listview ;
    private List<HashMap<String, Object>> mData;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_POST = "posts";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getdata);
        listview = (ListView) findViewById(R.id.list);
        Intent intent = this.getIntent();
        username = intent.getStringExtra("username");
        get(username);
        insert(username);

    }
    private void get(final String username) {

        class LoginAsync extends AsyncTask<String, Void, String>{

            private Dialog loadingDialog;

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
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                if(result!=null){

                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }
                listview.setAdapter(new MyAdapter(GetData.this));
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username);

    }
    private void insert(final String username) {

        class LoginAsync extends AsyncTask<String, Void, String>{

            private Dialog loadingDialog;

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
                nameValuePairs.add(new BasicNameValuePair("heartrate", String.valueOf(80)));
                nameValuePairs.add(new BasicNameValuePair("spo2", String.valueOf(92)));
                nameValuePairs.add(new BasicNameValuePair("image",String.valueOf(R.drawable.flexions)));
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
        la.execute(username);

    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;// 动态布局映射

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        // 决定ListView有几行可见
        @Override
        public int getCount() {
            return mData.size();// ListView的条目数
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
            convertView = mInflater.inflate(R.layout.list_item, null);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(mData.get(position).get("heartrate").toString());
            TextView message = (TextView) convertView.findViewById(R.id.message);
            message.setText(mData.get(position).get("spo2").toString());
            ImageView img = (ImageView) convertView.findViewById(R.id.image);
            img.setImageResource((Integer) mData.get(position).get("image"));
            return convertView;
        }
    }
    public static String getLocaleDatetime() {
        return String.format(Locale.getDefault(), "%tF  %<tR", new Date());
    }
}

