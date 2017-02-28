package com.example.ehab.movies_app;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import com.android.volley.toolbox.StringRequest;
import com.example.ehab.movies_app.adapter.ImageAdapter;
import com.example.ehab.movies_app.app.AppController;
import com.example.ehab.movies_app.app.Config;
import com.example.ehab.movies_app.model.Movies_Model;



public class MainActivityFragment extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    static GridView gridview;
    static int width;
    String returned_url;
    ImageAdapter imageAdapter;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    String sortKind;
    Movies_Model Movies;
    private DBHelper mHelper;
    private SQLiteDatabase dataBase;
    public ArrayList<Movies_Model> Movies_List = new ArrayList<>();
    MovieListener movieListener;
    public String Movieid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

          pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
          editor = pref.edit();
            gridview = (GridView) rootView.findViewById(R.id.Grid_view_movies);
            gridview.setColumnWidth(Setwidth());

        save_url();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String PosterPath = Movies_List.get(position).getPosterPath();
                String title = Movies_List.get(position).getTitle();
                Log.d(TAG, title);
                String release_date = Movies_List.get(position).getReleaseDate();
                String vote_average = Movies_List.get(position).getVoteAverage();
                String overview = Movies_List.get(position).getOverview();
                 Movieid = Movies_List.get(position).getId();
                MovieListener.Movies movies = new MovieListener.Movies();
                movies.id = Movieid;
                movies.overview = overview;
                movies.posterPath = PosterPath;
                movies.releaseDate = release_date;
                movies.title = title;
                movies.voteAverage = vote_average;

                movieListener.Moviesupdate(movies);

            }
        });
        return rootView;
    }

    String url_Sort_By_Builder(String sort_by) {
        String url ;
        if (sort_by.equals(Config.POPULAR)) {
            url = Config.MOVIES_BASE_URL+Config.POPULAR+"?api_key="+Config.API_KEY;
        }
        else  if (sort_by.equals(Config.TOP_RATED))
            url = Config.MOVIES_BASE_URL+Config.TOP_RATED+"?api_key="+Config.API_KEY;
        else {url=null;
        }


        return url;
    }
   public int Setwidth()
    {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (Config.isTablet(getContext())) {
            width = size.x / 4;
        }
        else width = size.x / 2;
        return  width;
    }
Config config=new Config();


    void save_url()
    {
        if (config.isConnected(getContext())) {
            if (Boolean.valueOf(pref.getString("changed", "null"))) {
                sortKind = pref.getString(Config.SORTING_PREF, "null");


                if(sortKind.equals(Config.FAV))
                {
                    displayData();

                }
                else
                {returned_url = url_Sort_By_Builder(sortKind);
                    sortKind = pref.getString(Config.SORTING_PREF, "null");

                    Movies_JSON(returned_url);
                }
            } else
            {
                sortKind = pref.getString(Config.SORTING_PREF, "null");

                returned_url = url_Sort_By_Builder(Config.POPULAR);
                Movies_JSON(returned_url);


            }
        }
        else
        {
            Config.buildDialog(getContext()).show();

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.topRated) {
            Config.CHANGED = true;
            editor.putString(Config.SORTING_PREF, Config.TOP_RATED);
            editor.putString("changed", String.valueOf(Config.CHANGED));

            editor.commit();
            save_url();
            return true;
        }
        if (id == R.id.Popular) {
            Config.CHANGED = true;
            editor.putString(Config.SORTING_PREF, Config.POPULAR);
            editor.putString("changed", String.valueOf(Config.CHANGED));
            editor.commit();
            save_url();
            return true;
        }
        if (id == R.id.Favo) {
            Config.CHANGED = true;
            editor.putString(Config.SORTING_PREF, Config.FAV);
            editor.putString("changed", String.valueOf(Config.CHANGED));
            editor.commit();
            save_url();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }
    public void SetMovieListener(MovieListener setmovieListener) {
        movieListener=setmovieListener;
    }






    private void Movies_JSON(String movies_url) {
        StringRequest strReq = new StringRequest(Request.Method.GET,movies_url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Movies_List=new ArrayList<>();
                imageAdapter = new ImageAdapter(getActivity(), Movies_List, Setwidth());

                JSONObject JSONString = null;
                try {
                    JSONString = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray moviesArray = null;
                try {
                    moviesArray = JSONString.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.d(TAG, returned_url.toString());
                for (int i = 0; i < moviesArray.length(); i++) {
                    try {
                        JSONObject movie = moviesArray.getJSONObject(i);
                        Movies = new Movies_Model();
                        Movies.setPosterPath(movie.getString("poster_path"));
                        Movies.setAdult(movie.getString("adult"));
                        Movies.setId(movie.getString("id"));
                        Movies.setOriginalLanguage(movie.getString("original_language"));
                        Movies.setOriginalTitle(movie.getString("original_title"));
                        Movies.setTitle(movie.getString("title"));
                        Movies.setPopularity(movie.getString("popularity"));
                        Movies.setVoteCount(movie.getString("vote_count"));
                        Movies.setVideo(movie.getString("original_language"));
                        Movies.setVoteAverage(movie.getString("vote_average"));
                        Movies.setOverview(movie.getString("overview"));
                        Movies.setReleaseDate(movie.getString("release_date"));
                        Movies_List.add(Movies);
                        imageAdapter.notifyDataSetChanged();
                        gridview.setAdapter(imageAdapter);
                        gridview.setVisibility(GridView.VISIBLE);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);

    }
    public void displayData() {
        mHelper = new DBHelper(getActivity());

        String selectQuery = "SELECT  * FROM " + DBHelper.TABLE_NAME;

        dataBase = mHelper.getWritableDatabase();

        Cursor cursor = dataBase.rawQuery(selectQuery, null);

        Movies_List.clear();
        try{

            if(cursor.getCount()<=0)
            {gridview.setVisibility(View.GONE);

            }
            if (cursor.moveToFirst()) {
                do {
                    imageAdapter=new ImageAdapter(getActivity(), Movies_List, Setwidth());

                    Movies = new Movies_Model();
                    Movies.setPosterPath(cursor.getString(cursor.getColumnIndex(DBHelper.poster_path)));
                    Movies.setId(cursor.getString(cursor.getColumnIndex(DBHelper.Movieid)));
                    Movies.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.title)));
                    Movies.setVoteAverage(cursor.getString(cursor.getColumnIndex(DBHelper.vote_average)));
                    Movies.setOverview(cursor.getString(cursor.getColumnIndex(DBHelper.overview)));
                    Movies.setReleaseDate(cursor.getString(cursor.getColumnIndex(DBHelper.release_date)));
                    Movies_List.add(Movies);
                    imageAdapter.notifyDataSetChanged();
                    gridview.setAdapter(imageAdapter);
                    gridview.setVisibility(GridView.VISIBLE);


                } while (cursor.moveToNext());
            }
        }finally{
            cursor.close();
        }
        dataBase.close();
     }




}
