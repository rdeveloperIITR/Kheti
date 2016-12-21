package com.rdeveloper.paviliong6.kheti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class SocialActivity extends AppCompatActivity {


    String URL_DATABASE = "https://blistering-inferno-8959.firebaseio.com//.json";
    Firebase rootRef;

    ArrayList<String> list_names, list_profile, list_title;

    ListView listView;

    ImageView p;
    static String DATA = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        rootRef.setAndroidContext(this);
        rootRef = new Firebase("https://blistering-inferno-8959.firebaseio.com");


        list_names = new ArrayList<String>();
        list_profile = new ArrayList<String>();
        list_title = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.ListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position + 1;
                String ID = String.valueOf(pos);
                //Toast.makeText(MainActivity.this,ID, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(SocialActivity.this, DescriptionActivity.class);
                i.putExtra("id", ID);
                startActivity(i);

            }
        });


        final ProgressDialog progressDialog = ProgressDialog.show(SocialActivity.this, "Please wait....", "Doing Something", true);

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
        onListItemOpen();
    }


    public void onListItemOpen() {

        new DownloadData().execute(URL_DATABASE);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public void onCustomAdaptorCall() {


        listView.setAdapter(new CustomAdaptor(list_profile, list_names, list_title, getBaseContext()));
        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();


    }


    class DownloadData extends AsyncTask<String, Void, String> {

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
            Log.d("ABC_SocialActivity", s);
            try {
                DATA = s;
                JSONObject reader = new JSONObject(s);
                int SIZE = PostActivity.getSize();
                Log.d("SIZE", String.valueOf(SIZE));
                for (int i = 1; i <= SIZE; i++) {

                    String p = "User " + i;
                    JSONObject object = reader.getJSONObject(p);

                    String profile = object.getString("image_profile");
                    String title = object.getString("title");
                    String name = object.getString("Name");
                    list_names.add(name);
                    list_profile.add(profile);
                    list_title.add(title);

                }


                onCustomAdaptorCall();
                Log.d("Bool", "Hi");


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public String fetchData(String s) throws IOException {

            InputStream in = null;

            URL url = new URL(s);
            URLConnection urlConnection = url.openConnection();
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.setAllowUserInteraction(false);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                in = httpURLConnection.getInputStream();
                return readIt(in, 10000000);
            } catch (Exception e) {
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


    public String getData() {

        return DATA;
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