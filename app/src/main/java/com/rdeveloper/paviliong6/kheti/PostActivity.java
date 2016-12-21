package com.rdeveloper.paviliong6.kheti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {


    String URL_DATABASE="https://blistering-inferno-8959.firebaseio.com//.json";
    String URL_LISTSIZE="https://listsize.firebaseio.com//.json";
    Firebase rootRef,rootRef1;

    private static int RESULT_LOAD_IMAGE = 1;
    EditText query_title,query_description;
    ImageButton imageButton;



    // Images to be stored in database
    String profile_image;
    String name;
    String query_image=null;
    Bitmap btp=null;

    static final String MyPREFERENCES = "MyPrefs";
    // data base sizes
    static int  listsize=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        rootRef.setAndroidContext(this);
        rootRef1.setAndroidContext(this);
        rootRef = new Firebase("https://blistering-inferno-8959.firebaseio.com");
        rootRef1= new Firebase("https://listsize.firebaseio.com");


        query_title=(EditText) findViewById(R.id.post_edittext1);
        query_description=(EditText) findViewById(R.id.post_edittext2);


        SharedPreferences preferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        name=preferences.getString("Name", "Your Name");

        profile_image=preferences.getString("ProfileImage", "");

    }


    public void onUpload(View v) {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {




        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d("TAG", picturePath);

            btp=BitmapFactory.decodeFile(picturePath);
            Bitmap scaled = getResizedBitmap(btp, 50, 50);
            query_image= encodeToBase64(scaled, Bitmap.CompressFormat.JPEG, 30);
            Toast.makeText(PostActivity.this,"Photo Uploaded",Toast.LENGTH_SHORT).show();

            if(btp!=null)
            {
                btp.recycle();
                btp=null;
            }

            if(scaled!=null){
                scaled.recycle();
                scaled=null;
            }
        }


    }


//-----------------------------------------------------------------------------------------------------------------------------------

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    //--------------------------------------------------------------------------------------------------------------------------------
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void onSave(View v){

      if(isNetworkAvailable()) {
          new DownloadData().execute(URL_LISTSIZE);
      }else{

          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setCancelable(false)
                  .setTitle("Error!!")
                  .setMessage("No Internet Connection.")
                  .setIcon(R.drawable.nointernet_icon)
                  .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          Intent i = new Intent(PostActivity.this, MainActivity.class);
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


    public void onAnswer(View v){

        if(isNetworkAvailable()) {
            new DownloadSize().execute(URL_LISTSIZE);
        }else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle("Error!!")
                    .setMessage("No Internet Connection.")
                    .setIcon(R.drawable.nointernet_icon)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(PostActivity.this, MainActivity.class);
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





    // to find size of data base

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

            try {

                JSONObject reader = new JSONObject(s);
                JSONObject object = reader.getJSONObject("listsize");
                String size_list = object.getString("datasize");

                listsize = Integer.parseInt(size_list);
                Log.d("run1", String.valueOf(listsize));
                listsize = listsize + 1;
                Log.d("run2", String.valueOf(listsize));

                Firebase listup = rootRef1.child("listsize");
                listup.child("datasize").setValue(String.valueOf(listsize));


                Firebase user = rootRef.child("User " + listsize);
                user.child("Name").setValue(name);
                user.child("image_profile").setValue(profile_image);
                user.child("image_query").setValue(query_image);
                user.child("title").setValue(query_title.getText().toString());
                user.child("query").setValue(query_description.getText().toString());

                //Toast.makeText(this,"Upload success", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "SUCCESS UPLOAD");
                Intent j = new Intent(PostActivity.this, SocialActivity.class);
                j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(j);


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
                return readIt(in,10000);
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

            try {

                JSONObject reader = new JSONObject(s);
                JSONObject object = reader.getJSONObject("listsize");
                String size_list = object.getString("datasize");

                listsize = Integer.parseInt(size_list);

                Intent j = new Intent(PostActivity.this, SocialActivity.class);
                j.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(j);


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
                return readIt(in,1000);
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


    public static int getSize(){
        return listsize;
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

