package lokavidya.iitb.com.lvcreate.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import lokavidya.iitb.com.lvcreate.R;
import lokavidya.iitb.com.lvcreate.adapter.HomeRecyclerAdapter;
import lokavidya.iitb.com.lvcreate.model.HomeItem;
import lokavidya.iitb.com.lvcreate.util.Master;

public class HomeFragment extends Fragment {

    // Global fields
    RecyclerView homeItemList;
    HomeRecyclerAdapter adapter;
    View rootLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootLayout = inflater.inflate(R.layout.fragment_home, container, false);

        homeItemList = rootLayout.findViewById(R.id.home_recycler);

        // Add temp data for test
        ArrayList<HomeItem> data = new ArrayList<>();
        data.add(new HomeItem("Title of Video 1", "Description for Video 1", null));
        data.add(new HomeItem("Title of Video 2", "Description for Video 2", null));
        data.add(new HomeItem("Title of Video 3", "Description for Video 3", null));
        data.add(new HomeItem("Title of Video 4", "Description for Video 4", null));
        data.add(new HomeItem("Title of Video 5", "Description for Video 5", null));
        data.add(new HomeItem("Title of Video 6", "Description for Video 6", null));

        // Adapter for RecyclerView
        adapter = new HomeRecyclerAdapter(data);
        homeItemList.setLayoutManager(new LinearLayoutManager(getContext()));

        homeItemList.setAdapter(adapter);

        /**
         * Start calling API here
         * **/
        // Get the Uri to append user_id
        Uri.Builder mostViewedAPI = Master.getMostViewedAPI();
        mostViewedAPI.appendQueryParameter("user_id", "16");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = mostViewedAPI.toString();

/*        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // JsonObject to get the response
                        JSONArray rootArray = new JSONArray();

                        try {
                            rootArray = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                            if (rootArray != null) {

                                for (int i = 0; i < rootArray.length(); i++) {

                                    JSONObject videoItem = rootArray.getJSONObject(i);

                                    Log.i("API " + i, videoItem.toString());

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);*/

        return rootLayout;
    }
}
