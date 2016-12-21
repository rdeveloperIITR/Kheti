package com.rdeveloper.paviliong6.kheti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    String LOCALE_HINDI = "hi";
    String LOCALE_ENGLISH = "en";
    Locale mLocale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocale = new Locale(LOCALE_HINDI);
        Locale.setDefault(mLocale);
        Configuration config = new Configuration();
        config.locale = mLocale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_main);



        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
    }


    public void onClickWeather(View v){

        startActivity(new Intent(this, WeatherActivity.class));
    }

    public void onClickExpert(View v){

         startActivity(new Intent(this, PostActivity.class));
    }


    public void onClickProfile(View v){

        startActivity(new Intent(this,ProfileActivity.class));
    }


    public void onAddMeasure(View v){

        startActivity(new Intent(this,MapsActivity.class));

    }

    public void onCropInfo(View v){

        startActivity(new Intent(this,CropInfoActivity.class));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    protected void exitByBackKey() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setTitle("Exit")
                .setMessage("Are you sure want to exit?")
                .setIcon(R.drawable.nointernet_icon)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_language_english) {

            mLocale = new Locale(LOCALE_ENGLISH);
            Locale.setDefault(mLocale);
            Configuration config = new Configuration();
            config.locale = mLocale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            MainActivity.this.setContentView(R.layout.activity_main);
            return true;
        }
        else if(id==R.id.action_language_hindi){

            mLocale = new Locale(LOCALE_HINDI);
            Locale.setDefault(mLocale);
            Configuration config = new Configuration();
            config.locale = mLocale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            MainActivity.this.setContentView(R.layout.activity_main);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
