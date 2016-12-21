package com.rdeveloper.paviliong6.kheti;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class DescriptionActivity extends AppCompatActivity {


    String URL_DATABASE="https://kheti.firebaseio.com//.json";
    Firebase rootRef;

    ImageView post,description;
    TextView text;
    EditText comment;
    ListView listView;

    String item_data=null;
    String item_id=null;


    Firebase user;
    static String ID_SIZE="0";
    static int SIZE_COMMENT=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        rootRef.setAndroidContext(this);


        rootRef = new Firebase("https://kheti.firebaseio.com");


        description=(ImageView) findViewById(R.id.decription_image);
        text=(TextView) findViewById(R.id.description_text);
        comment=(EditText) findViewById(R.id.description_comment);

        SocialActivity c=new SocialActivity();
        item_data=c.getData();
        item_id=getIntent().getExtras().getString("id");

        user = rootRef.child("CommentList "+item_id);

        Log.d("TEST", item_data);
        onParse();
    }


    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public void onParse(){

        try {

            JSONObject reader = new JSONObject(item_data);

            String p = "User "+item_id;
            JSONObject object = reader.getJSONObject(p);
            String image_query= object.getString("image_query");
            String text_query = object.getString("query");
            if(image_query!=null)
            {description.setImageBitmap(decodeBase64(image_query));
              Log.d("fine","pp");}

            Log.d("fine", item_id);
            text.setText(text_query);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    public void onComment(View view){


        if(isNetworkAvailable()) {
            new DownloadSize().execute(URL_DATABASE);
        }else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle("Error!!")
                    .setMessage("No Internet Connection.")
                    .setIcon(R.drawable.nointernet_icon)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(DescriptionActivity.this, MainActivity.class);
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


    class DownloadSize extends AsyncTask<String,Void,String> {

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
                String p = "CommentList " + item_id;
                JSONObject object = reader.getJSONObject(p);
                ID_SIZE=object.getString("ID");

                SIZE_COMMENT= Integer.parseInt(ID_SIZE);


                SIZE_COMMENT=SIZE_COMMENT+1;

                user.child("ID").setValue(String.valueOf(SIZE_COMMENT));

                user.child("comment "+String.valueOf(SIZE_COMMENT)).setValue(comment.getText().toString());

                Toast.makeText(DescriptionActivity.this,"Comment Posted",Toast.LENGTH_SHORT).show();

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
                return readIt(in, 1000000);
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


    public void onListComment(View v){

        Intent i=new Intent(DescriptionActivity.this,ListCommentActivity.class);
        i.putExtra("ID",item_id);

        startActivity(i);
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
