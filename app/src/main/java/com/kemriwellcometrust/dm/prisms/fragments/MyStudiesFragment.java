package com.kemriwellcometrust.dm.prisms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.kemriwellcometrust.dm.prisms.R;
import com.kemriwellcometrust.dm.prisms.adapters.SiteStudyAdapter;
import com.kemriwellcometrust.dm.prisms.dependencies.Constants;
import com.kemriwellcometrust.dm.prisms.dependencies.PrismsApplication;
import com.kemriwellcometrust.dm.prisms.dialogs.StudyDetails;
import com.kemriwellcometrust.dm.prisms.models.SiteStudy;
import com.kemriwellcometrust.dm.prisms.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MyStudiesFragment extends Fragment {


    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;


    private SiteStudyAdapter mAdapter;
    private ArrayList<SiteStudy> siteStudyArrayList;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.no_studies)
    LinearLayout no_studies;


    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        this.context = ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_my_studies, container, false);
        unbinder = ButterKnife.bind(this, root);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);



        getMyStudies();
        loadSiteStudides();







        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void loadSiteStudides(){


        siteStudyArrayList = Stash.getArrayList(Constants.OFFLINE_MY_STUDIES, SiteStudy.class);

        if (siteStudyArrayList.size() > 0) {

            mAdapter = new SiteStudyAdapter(context, siteStudyArrayList);

            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);

            //set data and list adapter
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            mAdapter.setOnClickListener(new SiteStudyAdapter.OnClickListener() {
                @Override
                public void onItemClick(int position) {
                    SiteStudy siteStudy = siteStudyArrayList.get(position);

                    StudyDetails bottomSheetFragment = StudyDetails.newInstance(siteStudy,context);
                    bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());

                }
            });


            no_studies.setVisibility(View.GONE);
        } else {
            no_studies.setVisibility(View.VISIBLE);
        }

    }

    private void getMyStudies() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.MY_STUDIES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    //Log.e("resoponse", response.toString());

                    siteStudyArrayList.clear();


                    boolean  status = response.has("success") && response.getBoolean("success");
                    String  message = response.has("message") ? response.getString("message") : "" ;
                    String  errors = response.has("errors") ? response.getString("errors") : "" ;


                    if (status){
                        Stash.clear(Constants.OFFLINE_MY_STUDIES);

                        JSONArray myArray = response.getJSONArray("data");

                        for (int i = 0; i < myArray.length(); i++) {

                            JSONObject item = (JSONObject) myArray.get(i);


                            int  id = item.has("id") ? item.getInt("id") : 0;
                            int  site_id = item.has("site_id") ? item.getInt("site_id") : 0;
                            int  study_id = item.has("study_id") ? item.getInt("study_id") : 0;
                            int  study_coordinator = item.has("study_coordinator") ? item.getInt("study_coordinator") : 0;
                            String date_initiated = item.has("date_initiated") ? item.getString("date_initiated") : "";
                            String studyStatus = item.has("status") ? item.getString("status") : "";
                            String study_name = item.has("study_name") ? item.getString("study_name") : "";
                            String study_detail = item.has("study_detail") ? item.getString("study_detail") : "";
                            String site_name = item.has("site_name") ? item.getString("site_name") : "";
                            String site_prefix = item.has("site_prefix") ? item.getString("site_prefix") : "";
                            JSONArray strataArray = item.has("strata") ? item.getJSONArray("strata") : new JSONArray();

                            ArrayList<String> list = new ArrayList<String>();
                            for(int j = 0; j < strataArray.length(); j++){
                                list.add(strataArray.get(j).toString());
                            }

                            SiteStudy site = new SiteStudy(id,site_id,study_id,study_coordinator,date_initiated,studyStatus,study_name,study_detail,site_name, site_prefix, list);

                            siteStudyArrayList.add(site);

                        }

                        Stash.put(Constants.OFFLINE_MY_STUDIES,siteStudyArrayList);
                        loadSiteStudides();

                    }else {
                        //Dialogs.showWarningDialog(context,message,errors);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d("VOLLEY ERROE", "Error: " + error.getMessage());
                //MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(error, context));

            }
        }){
            /*
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", loggedInUser.getToken_type()+" "+loggedInUser.getAccess_token());
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        PrismsApplication.getInstance().addToRequestQueue(jsonObjReq);
    }



//    private void getSiteStudies() {
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
//                Stash.getString(Constants.END_POINT)+ Constants.MY_STUDIES, null, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//
//                    //Log.e("resoponse", response.toString());
//
//                    siteStudyArrayList.clear();
//
//                    if (recyclerView!=null)
//                        recyclerView.setVisibility(View.VISIBLE);
//
//                    if (shimmer_my_container!=null){
//                        shimmer_my_container.stopShimmerAnimation();
//                        shimmer_my_container.setVisibility(View.GONE);
//                    }
//
//
//                    boolean  status = response.has("success") && response.getBoolean("success");
//                    String  message = response.has("message") ? response.getString("message") : "" ;
//                    String  errors = response.has("errors") ? response.getString("errors") : "" ;
//
//
//                    if (status){
//                        JSONArray myArray = response.getJSONArray("data");
//
//                        if (myArray.length() > 0){
//
//                            if (no_studies!=null)
//                                no_studies.setVisibility(View.GONE);
//
//
//
//                            for (int i = 0; i < myArray.length(); i++) {
//
//                                JSONObject item = (JSONObject) myArray.get(i);
//
//
//                                int  id = item.has("id") ? item.getInt("id") : 0;
//                                int  site_id = item.has("site_id") ? item.getInt("site_id") : 0;
//                                int  study_id = item.has("study_id") ? item.getInt("study_id") : 0;
//                                int  study_coordinator = item.has("study_coordinator") ? item.getInt("study_coordinator") : 0;
//                                String date_initiated = item.has("date_initiated") ? item.getString("date_initiated") : "";
//                                String studyStatus = item.has("status") ? item.getString("status") : "";
//                                String study_name = item.has("study_name") ? item.getString("study_name") : "";
//                                String study_detail = item.has("study_detail") ? item.getString("study_detail") : "";
//                                String site_name = item.has("site_name") ? item.getString("site_name") : "";
//
//                                SiteStudy site = new SiteStudy(id,site_id,study_id,study_coordinator,date_initiated,studyStatus,study_name,study_detail,site_name);
//
//                                siteStudyArrayList.add(site);
//                                mAdapter.notifyDataSetChanged();
//
//                            }
//
//                        }else {
//                            //not data found
//                            if (no_studies!=null)
//                                no_studies.setVisibility(View.VISIBLE);
//
//                        }
//                    }else {
//                        Dialogs.showWarningDialog(context,message,errors);
//
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                VolleyLog.d("VOLLEY ERROE", "Error: " + error.getMessage());
//                MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(error, context));
//
//            }
//        }){
//            /*
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Authorization", loggedInUser.getToken_type()+" "+loggedInUser.getAccess_token());
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");
//                return headers;
//            }
//        };
//
//        PrismsApplication.getInstance().addToRequestQueue(jsonObjReq);
//    }



}