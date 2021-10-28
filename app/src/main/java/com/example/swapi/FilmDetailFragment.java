package com.example.swapi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilmDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_film_detail, container, false);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDirector = view.findViewById(R.id.tvDirector);
        TextView tvProducer = view.findViewById(R.id.tvProducer);
        TextView tvReleaseDate = view.findViewById(R.id.tvReleaseDate);
        TextView tvCrawl = view.findViewById(R.id.tvCrawl);

        try {
            String filmJsonString = "";
            if (getArguments() != null) {
                filmJsonString = getArguments().getString("json");
            }
            JSONObject filmDetail = new JSONObject(filmJsonString);
            tvTitle.setText(filmDetail.getString("title"));
            tvDirector.setText(getString(R.string.director) + filmDetail.getString("director"));
            tvProducer.setText(getString(R.string.producer) + filmDetail.getString("producer"));
            tvReleaseDate.setText(getString(R.string.release_date) + filmDetail.getString("release_date"));
            tvCrawl.setText("\n\n\n\n\n\n\n\n\n\n" + filmDetail.getString("opening_crawl"));
            tvCrawl.setMovementMethod(new ScrollingMovementMethod());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}