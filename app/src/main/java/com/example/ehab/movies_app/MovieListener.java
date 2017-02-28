package com.example.ehab.movies_app;

import com.example.ehab.movies_app.model.Movies_Model;

/**
 * Created by ehab on 9/16/2016.
 */
public interface MovieListener {

    public class Movies {
        public String posterPath;
        public String overview;
        public String releaseDate;
        public String id;

        public String title;

        public String voteAverage;}

    public void Moviesupdate(Movies movies);


}
