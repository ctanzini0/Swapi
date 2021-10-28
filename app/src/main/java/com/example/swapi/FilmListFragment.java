package com.example.swapi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilmListFragment extends Fragment {

    private static JSONArray jsonFilms;

    private static final String TAG = "FilmListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_film_list, container, false);
        final ListView listview = view.findViewById(R.id.listview);

        //TODO: Get rid of this deprecated code in the future - possibly use ProgressBar instead
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // Consider moving to a Singleton in the future
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        String url ="https://swapi.dev/api/films/";

        List<String> allFilms = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, allFilms);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("json", jsonFilms.getString(position));

                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, FilmDetailFragment.class, bundle);
                    ft.addToBackStack(null);
                    ft.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Restore from cache if available
        try {
            JSONObject cachedFilms = getCachedFilms();
            jsonFilms = cachedFilms.getJSONArray("results");
            allFilms.clear();
            for (int i=0; i<cachedFilms.getInt("count"); i++) {
                allFilms.add("Star Wars: Episode "
                        + jsonFilms.getJSONObject(i).getInt("episode_id") + " - "
                        + jsonFilms.getJSONObject(i).getString("title"));
            }
            Log.d(TAG, "Restored from cached data - updating adapter");
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        } catch (JSONException e) {
            Log.d(TAG, "No cached data exists");
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Cache the response
                            cacheFilms(response);

                            jsonFilms = response.getJSONArray("results");
                            allFilms.clear();
                            for (int i = 0; i < response.getInt("count"); i++) {
                                allFilms.add("Star Wars: Episode "
                                        + jsonFilms.getJSONObject(i).getInt("episode_id") + " - "
                                        + jsonFilms.getJSONObject(i).getString("title"));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (allFilms.isEmpty()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                            builder.setMessage("Can't reach SWAPI. Are you connected to the internet?")
                                    .setTitle("Uh oh");

                            // Add the button
                            builder.setPositiveButton("Try Again", (dialog, id) -> {
                                // User clicked try again
                                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                ft.replace(R.id.flFragment, new FilmListFragment());
                                ft.commit();
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    }
                });

        requestQueue.add(jsonObjectRequest);

        // Inflate the layout for this fragment
        return view;
    }

    // Consider using Room persistence library in the future
    private void cacheFilms(JSONObject apiResult) {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("all_films", apiResult.toString());
        editor.apply();
        Log.d(TAG, "Successfully cached data");
    }

    private JSONObject getCachedFilms() throws JSONException {
        SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String cachedFilmsJsonString = sharedPref.getString("all_films", "");
        Log.d(TAG, "Retrieved cached data length: " + cachedFilmsJsonString.length());

        return new JSONObject(cachedFilmsJsonString);
    }
}