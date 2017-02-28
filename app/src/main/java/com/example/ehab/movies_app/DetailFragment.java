package com.example.ehab.movies_app;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ehab.movies_app.adapter.ReviewAdapter;
import com.example.ehab.movies_app.adapter.TrailerAdapter;
import com.example.ehab.movies_app.app.AppController;
import com.example.ehab.movies_app.app.Config;
import com.example.ehab.movies_app.model.Review_Model;
import com.example.ehab.movies_app.model.Trailer_Model;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DetailFragment extends Fragment {

        public DetailFragment() {
        }
    private static final String TAG = DetailFragment.class.getSimpleName();

    public  ListView TrailerListview;
    public  ListView ReviewListview;

    Trailer_Model Trailer;
    Review_Model Review;

    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    private SQLiteDatabase dataBase;

     String id;
    String trailer_url;
    String review_url;
    public ArrayList<Trailer_Model> Trailer_List = new ArrayList<>();
    private ArrayList<Review_Model> Review_List = new ArrayList<>();
    Button ReviewButton;
    Button Favorite;
    public View reviewview;
    String poster_path;
    String title;
    String release_date;
    String vote_average;
    String overview;
    private DBHelper mHelper;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mHelper=new DBHelper(getActivity());
            dataBase = mHelper.getWritableDatabase();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if(rootView != null) {
                MainActivity mainActivity=new MainActivity();
                Log.d(TAG, String.valueOf(mainActivity.mtwopane));


            if(Config.isTablet(getContext()))
            {

                 poster_path= getArguments().getString("PosterPath");
                 title=getArguments().getString("title");
                 release_date=getArguments().getString("release_date");
                 vote_average=getArguments().getString("vote_average");
                 overview=getArguments().getString("overview");
                 id=getArguments().getString("id");
            }
                else{
                Intent intent =getActivity().getIntent();

                poster_path= intent.getExtras().getString("PosterPath");
                title=intent.getExtras().getString("title");
                release_date=intent.getExtras().getString("release_date");
                vote_average=intent.getExtras().getString("vote_average");
                overview=intent.getExtras().getString("overview");
                id=intent.getExtras().getString("id");
            }
                TrailerListview=(ListView) rootView.findViewById(R.id.trailer_list);
            trailerAdapter = new TrailerAdapter(getActivity(), Trailer_List);
            reviewAdapter = new ReviewAdapter(getActivity(), Review_List);
            trailer_url="http://api.themoviedb.org/3/movie/"+id+"/videos?api_key=14bb2e410981baf68982e32ec2c4735b";
            review_url="http://api.themoviedb.org/3/movie/"+id+"/reviews?api_key=14bb2e410981baf68982e32ec2c4735b";

            Trailer_json(trailer_url);
            Review_json(review_url);
            getActivity().setTitle("Movies Details");

            View header = inflater.inflate(R.layout.moviesdetails, null);
            TrailerListview.addHeaderView(header);
            if (poster_path!= null) {

                ImageView iv = (ImageView) rootView.findViewById(R.id.poster_path);

                Picasso.with(getActivity()).load(Config.BASE_URL_IMGAE +poster_path).resize(MainActivityFragment.width, (int) (MainActivityFragment.width * 1.5)).into(iv);
            }
            if (title != null) {
                TextView tv = (TextView) rootView.findViewById(R.id.title);
                tv.setText(title);
            }
            if (overview != null) {
                TextView tv = (TextView) rootView.findViewById(R.id.overview);
                tv.setText(overview);

            }

            if (release_date != null) {
                TextView tv = (TextView) rootView.findViewById(R.id.release_date);
                tv.setText(release_date);
            }
            if (vote_average!= null) {
                TextView tv = (TextView) rootView.findViewById(R.id.vote_average);
                tv.setText(vote_average);
            }
            reviewview=  getActivity().getLayoutInflater().inflate(R.layout.reviewlist,null,false);
            ReviewListview=(ListView) reviewview.findViewById(R.id.review_list);

            ReviewButton=(Button) rootView.findViewById(R.id.review_button);
                Favorite=(Button) rootView.findViewById(R.id.favorite);
                ReviewButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   android.app.AlertDialog.Builder alertDialog=new android.app.AlertDialog.Builder(getActivity());
                   alertDialog.setTitle("Reviews");
                   alertDialog.setView(reviewview);
                   alertDialog.create();
                   alertDialog.setCancelable(true).setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                          Trailer_List=new ArrayList<>();
                           Review_List=new ArrayList<>();
                           Fragment frg ;
                           frg = getActivity().getSupportFragmentManager().findFragmentById(R.id.Panal2);
                           final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                           ft.detach(frg);
                           ft.attach(frg);
                           ft.commit();
                       }
                   }).show();
               }
           });
                TrailerListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent viewIntent = new Intent("android.intent.action.VIEW",Uri.parse("https://www.youtube.com/watch?v="+Trailer_List.get(position).getKey()));
                        startActivity(viewIntent);
                    }
                });
                if(!Config.isConnected(getContext()))
                    Config.buildDialog(getContext()).show();
            }
            isfav();
            TrailerListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                    shareTrailerUrl("https://www.youtube.com/watch?v=" + Trailer_List.get(pos).getKey());
                    Log.v("long clicked", "pos: " + pos);

                    return true;
                }
            });
            ReviewListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse(Review_List.get(position).getUrl()));
        startActivity(viewIntent);
    }
});
            SharedPreferences pref;
           pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            String sortKind = pref.getString(Config.SORTING_PREF, "null");
   //         if (sortKind == Config.FAV) {
              //  Favorite.setText("not favorite");

                 //   dataBase.delete(DBHelper.TABLE_NAME, DBHelper.Movieid + "=" + id, null);
                   // Toast.makeText(getContext(), title + " is not a favorite movie.", Toast.LENGTH_SHORT).show();






           // }

            Favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Favorite.getText().equals("not favorite"))
                    {            dataBase = mHelper.getWritableDatabase();

                        dataBase.delete(DBHelper.TABLE_NAME, DBHelper.Movieid + "=" + id, null);
                        //Toast.makeText(getContext(), title + " is not a favorite movie.", Toast.LENGTH_SHORT).show();
                        //MainActivityFragment mainActivityFragment=new MainActivityFragment();
                       // mainActivityFragment.displayData();
                        Log.d(TAG, "onClick:hhhhhhhhhhhhhhhhhhhhhhhhhhhh ");
                        Favorite.setText("Favorite");
                        dataBase.close();


                        // displayData();

                    }
                   else if(Favorite.getText().equals("Favorite")){
                        Favorite.setText("not favorite");
                        dataBase = mHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                    values.put(DBHelper.Movieid, id);
                    values.put(DBHelper.overview, overview);
                    values.put(DBHelper.poster_path, poster_path);
                    values.put(DBHelper.release_date, release_date);
                    values.put(DBHelper.title, title);
                        values.put(DBHelper.vote_average, vote_average);
                        dataBase.insert(DBHelper.TABLE_NAME, null, values);
                        dataBase.close();
                    //Favorite.setVisibility(View.INVISIBLE);

                    //Toast.makeText(getContext(), title + " is a favorite movie.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return rootView;    }

    private void isfav() {
        mHelper = new DBHelper(getActivity());
        String selectQuery = "SELECT  * FROM " + DBHelper.TABLE_NAME;
        dataBase = mHelper.getWritableDatabase();
        Cursor cursor = dataBase.rawQuery(selectQuery, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    if(cursor.getString(cursor.getColumnIndex(DBHelper.Movieid)).equals(id))
                    {
                        Favorite.setText("not favorite");
                    }
                } while (cursor.moveToNext());
            }
        }finally{
            cursor.close();
        }
        dataBase.close();
    }


    private void shareTrailerUrl(String urlshare) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "POP Movies");
        share.putExtra(Intent.EXTRA_TEXT, urlshare);
        startActivity(Intent.createChooser(share, "Share Trailer"));
    }
    private void Trailer_json(final String Trailer_url) {
        StringRequest strReq = new StringRequest(Request.Method.GET,Trailer_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                JSONObject JSONString = null;
                try {
                    JSONString = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray TrailerArray = null;
                try {
                    TrailerArray = JSONString.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, Trailer_url.toString());
                for (int i = 0; i < TrailerArray.length(); i++) {
                    try {
                        JSONObject trailer = TrailerArray.getJSONObject(i);
                        Trailer = new Trailer_Model();
                        Trailer.setName(trailer.getString("name"));
                        Log.d(TAG, Trailer.getName());
                        Trailer.setKey(trailer.getString("key"));
                        Trailer.setId(trailer.getString("id"));
                        Trailer.setSite(trailer.getString("site"));
                        Trailer.setSize(trailer.getString("size"));
                        Trailer.setType(trailer.getString("type"));
                        Trailer.setIso6391(trailer.getString("iso_639_1"));
                        trailerAdapter.add(Trailer);
                        trailerAdapter.notifyDataSetChanged();
                        TrailerListview.setAdapter(trailerAdapter);
                        TrailerListview.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);

    }
    private void Review_json(final String Review_url) {
        StringRequest strReq = new StringRequest(Request.Method.GET,Review_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject JSONString = null;
                try {
                    JSONString = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray ReviewArray = null;
                try {
                    ReviewArray = JSONString.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, Review_url.toString());
                for (int i = 0; i < ReviewArray.length(); i++) {
                    try {
                        JSONObject review = ReviewArray.getJSONObject(i);


                        Review = new Review_Model();
                        Review.setId(review.getString("id"));
                        Review.setAuthor(review.getString("author"));
                        Log.d(TAG, Review.getAuthor());
                        Review.setContent(review.getString("content"));
                        Log.d(TAG, Review.getContent());
                        Review.setUrl(review.getString("url"));
                        Log.d(TAG, Review.getUrl());
                        Review_List.add(Review);
                        reviewAdapter.add(Review);
                        reviewAdapter.notifyDataSetChanged();
                        ReviewListview.setAdapter(reviewAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);

    }
}







