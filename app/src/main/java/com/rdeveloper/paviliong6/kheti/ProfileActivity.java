package com.rdeveloper.paviliong6.kheti;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    static final String MyPREFERENCES = "MyPrefs";
    ImageButton imageButton;
    TextView name,pin;
    Bitmap btp=null;


    static String profileImage=null;
    ImageView imageView;
    private static int RESULT_LOAD_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        name=(TextView) findViewById(R.id.profile_name);
        pin=(TextView) findViewById(R.id.profile_pin);
        imageView=(ImageView) findViewById(R.id.profile_imageView);


        SharedPreferences preferences=getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        profileImage=preferences.getString("ProfileImage", "");

        if(profileImage!=""){
            imageView.setImageBitmap(decodeBase64(profileImage));
        }
    }


    public void onProfileUpload(View v) {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.d("TAG","fine");
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



            btp= BitmapFactory.decodeFile(picturePath);

            Log.d("TAG", picturePath);
            Bitmap scaled = getResizedBitmap(btp, 50, 50);

            profileImage= encodeToBase64(scaled, Bitmap.CompressFormat.JPEG, 30);

            Toast.makeText(ProfileActivity.this, "Profile Photo Uploaded", Toast.LENGTH_SHORT).show();
            imageView.setImageBitmap(decodeBase64(profileImage));
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

    public void onSave(View v){



        if( (!(name.getText().toString().isEmpty() )) && (!(name.getText().toString().isEmpty() )) && profileImage!=null) {


            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("Name", name.getText().toString());
            editor.putString("ProfileImage", profileImage);
            editor.putString("Pin", pin.getText().toString());
            editor.commit();
            final ProgressDialog progressDialog = ProgressDialog.show(this, "Saving", "Please wait....", true);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        progressDialog.dismiss();
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {


                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }

                }
            }).start();


        }else{
            Toast.makeText(this,"Please fill all position",Toast.LENGTH_SHORT).show();
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
