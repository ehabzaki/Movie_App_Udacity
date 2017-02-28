package com.example.ehab.movies_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ehab.movies_app.R;
import com.example.ehab.movies_app.model.Review_Model;

import java.util.ArrayList;

/**
 * Created by ehab on 9/9/2016.
 */
public class ReviewAdapter extends BaseAdapter {

    private Context mContext;
    private  static ArrayList<Review_Model> reviewItems;



    /* Constructor*/
    public ReviewAdapter(Context c,ArrayList<Review_Model> reviewcontent) {

        mContext = c;
        this.reviewItems = reviewcontent;


    }

    public void add(Review_Model review_model){
        reviewItems.add(review_model);
    }
    @Override
    public int getCount() {
        return reviewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView authortext;
        public TextView contenttext;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.review_items, parent, false);
            holder = new ViewHolder();

           holder.authortext = (TextView) view.findViewById(R.id.author);
            holder.contenttext = (TextView) view.findViewById(R.id.content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

      holder.contenttext.setText("Review  "+(position+1));
        holder.authortext.setText(reviewItems.get(position).getAuthor());


        //if(reviewItems.get(position).getContent().length()>300) {
           // holder.contenttext.setText(reviewItems.get(position).getContent().substring(0,300));

       // }else
        //{

          //  holder.contenttext.setText(reviewItems.get(position).getContent());

        //}

        return view;
    }





}
