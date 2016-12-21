package com.rdeveloper.paviliong6.kheti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pavilion g6 on 14-03-2016.
 */
public class CustomAdaptor extends BaseAdapter {

    ArrayList<String> profile;
    ArrayList<String> names;
    ArrayList<String> title;
    Context base;

    TextView text_name,text_title;
    ImageView image_profile;
    public CustomAdaptor(ArrayList<String> prof, ArrayList<String> nam, ArrayList<String> tit, Context context){
        this.base=context;
        this.profile=prof;
        this.names=nam;
        this.title=tit;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        LayoutInflater layoutInflater=(LayoutInflater) base.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.custom_layout,parent,false);


        image_profile=(ImageView) view.findViewById(R.id.custom_image);
        text_name=(TextView) view.findViewById(R.id.custom_text_name);
        text_title=(TextView) view.findViewById(R.id.custom_text_title);



        image_profile.setImageBitmap( decodeBase64(profile.get(position)));
        text_name.setText(names.get(position));
        text_title.setText(title.get(position));

        return view;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


}
