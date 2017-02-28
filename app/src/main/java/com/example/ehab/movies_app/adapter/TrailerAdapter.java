package com.example.ehab.movies_app.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ehab.movies_app.R;
import com.example.ehab.movies_app.model.Trailer_Model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ehab on 8/31/2016.
 */
public class TrailerAdapter  extends BaseAdapter {
    /********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/

        /*********** Declare Used Variables *********/
        private Activity activity;
        private ArrayList data;
        private static LayoutInflater mInflater=null;
    private int width;
    private Context mContext;
    private  static ArrayList<Trailer_Model> trailerItems;



        /* Constructor*/
        public TrailerAdapter(Context c,ArrayList<Trailer_Model> TrailerPath) {

                mContext = c;
                this.trailerItems = TrailerPath;


        }

    public void add(Trailer_Model trailer_model){
        trailerItems.add(trailer_model);
    }
    @Override
    public int getCount() {
        return trailerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return trailerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView textView;
        public ImageView imageView;
       // public ImageView posterpath;
       // public TextView title;


    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.trailer_items, parent, false);
            holder = new ViewHolder();



            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.textView = (TextView) view.findViewById(R.id.trailer_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String yt_thumbnail_url = "http://img.youtube.com/vi/" + trailerItems.get(position).getKey() + "/0.jpg";


        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext)
                .load(yt_thumbnail_url)
                .placeholder(R.drawable.youtube)
                .error(R.drawable.youtube)

                .tag(mContext)
                .into(holder.imageView);


        holder.textView.setText(trailerItems.get(position).getName());

        return view;
    }





}
