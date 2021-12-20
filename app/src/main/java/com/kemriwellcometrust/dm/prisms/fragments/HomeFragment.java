package com.kemriwellcometrust.dm.prisms.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.kemriwellcometrust.dm.prisms.MainActivity;
import com.kemriwellcometrust.dm.prisms.R;
import com.kemriwellcometrust.dm.prisms.dependencies.Constants;
import com.kemriwellcometrust.dm.prisms.dependencies.Dialogs;
import com.kemriwellcometrust.dm.prisms.dependencies.PrismsApplication;
import com.kemriwellcometrust.dm.prisms.dependencies.VolleyErrors;
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


public class HomeFragment extends Fragment {


    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;

    private static HomeFragment instance = null;

    private ArrayList<SiteStudy> siteStudyArrayList;





    public ProgressDialog mProgressDialog;



    @BindView(R.id.home_title)
    TextView home_title;

    @BindView(R.id.studies_layout)
    CardView studies_layout;

    @BindView(R.id.sms_layout)
    CardView sms_layout;

    @BindView(R.id.sites_layout)
    CardView sites_layout;

    @BindView(R.id.chartCard)
    CardView chartCard;

    @BindView(R.id.survey_layout)
    CardView survey_layout;

    @BindView(R.id.linearAdmin)
    LinearLayout linearAdmin;

    @BindView(R.id.allocation_layout)
    CardView allocation_layout;

    @BindView(R.id.chart)
    LineChart chart;




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
        root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);
        instance = this;

        siteStudyArrayList = new ArrayList<>();



        home_title.setText("Hello "+loggedInUser.getFirst_name()+",");



        studies_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_studies);
            }
        });

        sms_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_sms);
            }
        });

        sites_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_site_main);
            }
        });

        allocation_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_allocation);
            }
        });


        survey_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.nav_survey);
            }
        });


        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);

        chart.getAxisLeft().setEnabled(true);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(true);
        chart.getXAxis().setDrawGridLines(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);


        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        if (loggedInUser!=null && (loggedInUser.getUser_group()==1 || loggedInUser.getUser_group()==2)){
            chartCard.setVisibility(View.VISIBLE);
            linearAdmin.setVisibility(View.VISIBLE);
            getChartData();
        }else {

            chartCard.setVisibility(View.GONE);
            linearAdmin.setVisibility(View.GONE);

        }

        getMyStudies();




        return root;
    }


    private void getChartData() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.STUDY_RANDOMIZATION_RATE, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.e("resoponse", response.toString());


                    boolean  status = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "" ;
                    String errors = response.has("errors") ? response.getString("errors") : "" ;


                    if (status){
                        JSONArray myArray = response.getJSONArray("data");

                        Log.e("myarray", myArray.toString());


                        if (myArray.length() > 0){


                            // the labels that should be drawn on the XAxis
                            List<String> datesArray = new ArrayList<>();

                            List<Entry> randomization = new ArrayList<Entry>();

                            for (int i = 0; i < myArray.length(); i++) {

                                JSONObject item = (JSONObject) myArray.get(i);


                                int  total = item.has("total") ? item.getInt("total") : 0;
                                String date_randomised = item.has("date_randomised") ? item.getString("date_randomised") : "";

                                datesArray.add(date_randomised);

                                randomization.add(new Entry(i, total));
                            }

                            LineDataSet randomizationDataset = new LineDataSet(randomization, "Randomization per day");
                            randomizationDataset.setAxisDependency(YAxis.AxisDependency.LEFT);
                            randomizationDataset.setColor(Color.RED);


                            // use the interface ILineDataSet
                            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                            dataSets.add(randomizationDataset);

//                            ValueFormatter formatter = new ValueFormatter() {
//
//                                @Override
//                                public String getFormattedValue(float value) {
//                                    int index = Math.round(value);
//                                    Log.e("value", String.valueOf(index));
//
//                                    if (datesArray == null){
//                                        return  null;
//                                    }else {
//                                        if (index == -1){
//                                            return datesArray[0];
//                                        }else {
//
//                                            if(index < datesArray.length) {
//                                                return datesArray[index];
//                                            }
//                                            return null;
//                                        }
//                                    }
//                                }
//                            };

                            if (chart!=null){
                                XAxis xAxis = chart.getXAxis();
                                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                                //xAxis.setValueFormatter(formatter);
                                chart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(datesArray));


                                LineData data = new LineData(dataSets);
                                chart.setData(data);
                                chart.invalidate(); // refresh
                            }




                        }
                    }else {
                        Dialogs.showWarningDialog(context,message,errors);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("VOLLEY ERROE", "Error: " + error.getMessage());
                MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(error, context));

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
                            JSONArray strataArray = item.has("strata") ? item.getJSONArray("strata") : new JSONArray();

                            ArrayList<String> list = new ArrayList<String>();
                            for(int j = 0; j < strataArray.length(); j++){
                                list.add(strataArray.get(j).toString());
                            }

                            SiteStudy site = new SiteStudy(id,site_id,study_id,study_coordinator,date_initiated,studyStatus,study_name,study_detail,site_name, list);

                            siteStudyArrayList.add(site);

                        }

                        Stash.put(Constants.OFFLINE_MY_STUDIES,siteStudyArrayList);

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

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(getString(R.string.processing));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public static HomeFragment getInstance() {
        return instance;
    }


}