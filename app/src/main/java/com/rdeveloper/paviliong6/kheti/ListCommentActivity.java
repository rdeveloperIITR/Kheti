package com.rdeveloper.paviliong6.kheti;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ListCommentActivity extends AppCompatActivity {


    String URL_DATABASE="https://kheti.firebaseio.com//.json";
    Firebase rootRef;
    ListView listView;
    ArrayList<String> list_comments;

    String item_id;
    Firebase user;
    static String ID_SIZE="0";
    static int SIZE_COMMENT=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_comment);


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        rootRef.setAndroidContext(this);
        rootRef = new Firebase("https://kheti.firebaseio.com");


        listView=(ListView) findViewById(R.id.Comment_list);
        list_comments=new ArrayList<String>();

        item_id=getIntent().getExtras().getString("ID");

        user = rootRef.child("CommentList " + item_id);

        onListOpen();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void onListOpen(){


        if(isNetworkAvailable()) {
            new DownloadData().execute(URL_DATABASE);
        }else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle("Error!!")
                    .setMessage("No Internet Connection.")
                    .setIcon(R.drawable.nointernet_icon)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(ListCommentActivity.this, MainActivity.class);
                            finish();
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }


    }


    public void onCustomAdaptorCall(){

        listView.setAdapter(new ArrayAdapter<String>(ListCommentActivity.this, android.R.layout.simple_list_item_1, list_comments));

        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
    }
    class DownloadData extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                return fetchData(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            Log.d("ABC", s);
            try {

                JSONObject reader = new JSONObject(s);
                String p = "CommentList " +item_id;
                JSONObject object = reader.getJSONObject(p);
                ID_SIZE=object.getString("ID");

                SIZE_COMMENT= Integer.parseInt(ID_SIZE);

                list_comments.clear();


                final ProgressDialog progressDialog = ProgressDialog.show(ListCommentActivity.this, "Please wait....", "Doing Something", true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            progressDialog.dismiss();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                for(int i=1;i<=SIZE_COMMENT;i++) {

                    String value =  object.getString("comment "+ i);
                    String g=i+". "+value;
                    list_comments.add(g);

                }



                onCustomAdaptorCall();
                Log.d("comment","also fine");


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public String fetchData(String s) throws IOException{

            InputStream in=null;

            URL url=new URL(s);
            URLConnection urlConnection=url.openConnection();
            try{
                HttpURLConnection httpURLConnection=(HttpURLConnection) urlConnection;
                httpURLConnection.setAllowUserInteraction(false);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                in=httpURLConnection.getInputStream();
                return readIt(in,1000000);
            }catch (Exception e){
                throw new IOException("Error Connecting");
            }


        }

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            startActivity(new Intent(this,MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






}
