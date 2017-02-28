package com.example.ehab.movies_app;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.ehab.movies_app.app.Config;
import com.example.ehab.movies_app.model.Movies_Model;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements MovieListener {
    public static boolean TABLET = false;
    MainActivityFragment mainActivityFragment;
   public  boolean mtwopane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TABLET=Config.isTablet(getApplicationContext());
        FrameLayout f2 = (FrameLayout) findViewById(R.id.Panal2);
        if (null == f2) {
            mtwopane = false;
        } else {
            mtwopane = true;
        }
        mainActivityFragment = new MainActivityFragment();
            if (savedInstanceState == null) {
                mainActivityFragment.SetMovieListener(this);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frag1, mainActivityFragment)
                        .commit();
            }

    }
    @Override
    public void Moviesupdate(Movies movies) {
        if (TABLET) {
          DetailFragment detailFragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.Panal2, detailFragment).remove(detailFragment);
            Bundle extras = new Bundle();
            extras.putString("title",movies.title);
            extras.putString("id", movies.id);
            extras.putString("PosterPath", movies.posterPath);
            extras.putString("vote_average", movies.voteAverage);
            extras.putString("release_date", movies.releaseDate);
            extras.putString("overview", movies.overview);
            detailFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().add(R.id.Panal2, detailFragment).commit();

        } else {

            Intent intent = new Intent(this, DetailActivity.class).
                    putExtra("title", movies.title).
                    putExtra("id", movies.id).
                    putExtra("PosterPath", movies.posterPath).
                    putExtra("vote_average", movies.voteAverage).
                    putExtra("release_date", movies.releaseDate).
                    putExtra("overview",movies.overview );


            startActivity(intent);


        }
    }
}
