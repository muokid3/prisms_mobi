package com.kemriwellcometrust.dm.prisms.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kemriwellcometrust.dm.prisms.MainActivity;
import com.kemriwellcometrust.dm.prisms.R;
import com.kemriwellcometrust.dm.prisms.adapters.SmsAdapter;
import com.kemriwellcometrust.dm.prisms.dependencies.Constants;
import com.kemriwellcometrust.dm.prisms.dependencies.Dialogs;
import com.kemriwellcometrust.dm.prisms.dependencies.PrismsApplication;
import com.kemriwellcometrust.dm.prisms.dependencies.VolleyErrors;
import com.kemriwellcometrust.dm.prisms.models.Sms;
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


public class SmsFragment extends Fragment {


    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;


    @BindView(R.id.shimmer_my_container)
    ShimmerFrameLayout shimmer_my_container;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.no_sms)
    LinearLayout no_sms;

    @BindView(R.id.chart1)
    LineChart chart;

    private boolean myShouldLoadMore = true;
    private String MY_NEXT_LINK = null;

    private SmsAdapter mAdapter;
    private ArrayList<Sms> smsArrayList;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;




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
        root = inflater.inflate(R.layout.fragment_sms, container, false);
        unbinder = ButterKnife.bind(this, root);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);

        smsArrayList = new ArrayList<>();
        mAdapter = new SmsAdapter(context, smsArrayList);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);

        firstLoad();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollHorizontally(1)) {
                    if (myShouldLoadMore && !MY_NEXT_LINK.equals("null")) {
                        loadMore();
                    }
                }
            }
        });

        mAdapter.setOnViewListener(new SmsAdapter.OnViewListener() {
            @Override
            public void onItemClick(int position) {

                Sms clickedSms = smsArrayList.get(position);

                showDetailsBottomSheetDialog(clickedSms);

            }
        });


        bottom_sheet = root.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);


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

        getChartData();

        //loadChartData();




        return root;
    }

    private void getChartData() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.SMS_GRAPH, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

//                    Log.e("resoponse", response.toString());


                    boolean  status = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "" ;
                    String errors = response.has("errors") ? response.getString("errors") : "" ;


                    if (status){
                        JSONArray myArray = response.getJSONArray("data");

                        if (myArray.length() > 0){


                            // the labels that should be drawn on the XAxis
                            String[] datesArray = new String[myArray.length()];

                            List<Entry> inbox = new ArrayList<Entry>();
                            List<Entry> outbox = new ArrayList<Entry>();

                            for (int i = 0; i < myArray.length(); i++) {

                                JSONObject item = (JSONObject) myArray.get(i);


                                int  inboxNo = item.has("inbox") ? item.getInt("inbox") : 0;
                                int  outboxNo = item.has("outbox") ? item.getInt("outbox") : 0;
                                String date = item.has("date") ? item.getString("date") : "";

                                datesArray[i] = date;

                                inbox.add(new Entry(i, inboxNo));
                                outbox.add(new Entry(i, outboxNo));
                            }

                            LineDataSet inboxDataset = new LineDataSet(inbox, "Inbox");
                            inboxDataset.setAxisDependency(YAxis.AxisDependency.LEFT);
                            inboxDataset.setColor(Color.RED);

                            LineDataSet outboxDataset = new LineDataSet(outbox, "Outbox");
                            outboxDataset.setAxisDependency(YAxis.AxisDependency.LEFT);

                            // use the interface ILineDataSet
                            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                            dataSets.add(inboxDataset);
                            dataSets.add(outboxDataset);

                            ValueFormatter formatter = new ValueFormatter() {

                                @Override
                                public String getFormattedValue(float value) {
                                    return datesArray[(int) value];
                                }
                            };

                            XAxis xAxis = chart.getXAxis();
                            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                            xAxis.setValueFormatter(formatter);

                            LineData data = new LineData(dataSets);
                            chart.setData(data);
                            chart.invalidate(); // refresh


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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmer_my_container.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        shimmer_my_container.stopShimmerAnimation();
        super.onPause();
    }


    private void firstLoad() {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.SMS, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

//                    Log.e("resoponse", response.toString());

                    smsArrayList.clear();

                    myShouldLoadMore = true;
                    recyclerView.setVisibility(View.VISIBLE);

                    if (shimmer_my_container!=null){
                        shimmer_my_container.stopShimmerAnimation();
                        shimmer_my_container.setVisibility(View.GONE);
                    }


                    boolean  status = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "" ;
                    String errors = response.has("errors") ? response.getString("errors") : "" ;


                    if (status){
                        JSONArray myArray = response.getJSONArray("data");
                        JSONObject links = response.getJSONObject("links");
                        MY_NEXT_LINK = links.getString("next");

                        if (myArray.length() > 0){

                            no_sms.setVisibility(View.GONE);


                            for (int i = 0; i < myArray.length(); i++) {

                                JSONObject item = (JSONObject) myArray.get(i);


                                int  id = item.has("id") ? item.getInt("id") : 0;
                                String timestamp = item.has("timestamp") ? item.getString("timestamp") : "";
                                String source = item.has("source") ? item.getString("source") : "";
                                String text = item.has("text") ? item.getString("text") : "";
                                int  short_code = item.has("short_code") ? item.getInt("short_code") : 0;
                                int  inboxStatus = item.has("status") ? item.getInt("status") : 0;
                                String latency = item.has("latency") ? item.getString("latency") : "";


                                JSONObject outboxObj = item.has("outbox") ? item.getJSONObject("outbox") : null;

                                Sms sms;

                                if (outboxObj != null){

                                    int outboxId = outboxObj.has("id") ? outboxObj.getInt("id") : 0;
                                    String outboxMessage_id = outboxObj.has("message_id") ? outboxObj.getString("message_id") : "";
                                    String outboxTimestamp = outboxObj.has("timestamp") ? outboxObj.getString("timestamp") : "";
                                    String outboxDestination = outboxObj.has("destination") ? outboxObj.getString("destination") : "";
                                    String outboxText = outboxObj.has("text") ? outboxObj.getString("text") : "";
                                    String outboxStatus = outboxObj.has("status") ? outboxObj.getString("status") : "";
                                    String outboxDelivery_time = outboxObj.has("delivery_time") ? outboxObj.getString("delivery_time") : "";
                                    String outboxCreated_at = outboxObj.has("created_at") ? outboxObj.getString("created_at") : "";

                                    Sms.Outbox outbox = new Sms.Outbox(outboxId,outboxMessage_id,outboxTimestamp,outboxDestination,outboxText,outboxStatus,outboxDelivery_time,outboxCreated_at);

                                    sms = new Sms(id, timestamp,source,text,short_code,inboxStatus,latency,outbox);

                                }else {
                                    sms = new Sms(id, timestamp,source,text,short_code,inboxStatus,latency);
                                }

                                smsArrayList.add(sms);
                                mAdapter.notifyDataSetChanged();

                            }

                        }else {
                            //not data found
                            no_sms.setVisibility(View.VISIBLE);
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
                myShouldLoadMore =true;

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


    private void loadMore() {

        myShouldLoadMore =false;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                MY_NEXT_LINK, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    myShouldLoadMore = true;

                    boolean  status = response.has("success") && response.getBoolean("success");
                    String  message = response.has("message") ? response.getString("message") : "" ;
                    String  errors = response.has("errors") ? response.getString("errors") : "" ;


                    if (status){
                        JSONArray myArray = response.getJSONArray("data");
                        JSONObject links = response.getJSONObject("links");
                        MY_NEXT_LINK = links.getString("next");

                        if (myArray.length() > 0){



                            for (int i = 0; i < myArray.length(); i++) {

                                JSONObject item = (JSONObject) myArray.get(i);


                                int  id = item.has("id") ? item.getInt("id") : 0;
                                String timestamp = item.has("timestamp") ? item.getString("timestamp") : "";
                                String source = item.has("source") ? item.getString("source") : "";
                                String text = item.has("text") ? item.getString("text") : "";
                                int  short_code = item.has("short_code") ? item.getInt("short_code") : 0;
                                int  inboxStatus = item.has("status") ? item.getInt("status") : 0;
                                String latency = item.has("latency") ? item.getString("latency") : "";


                                JSONObject outboxObj = item.has("outbox") ? item.getJSONObject("outbox") : null;

                                Sms sms;

                                if (outboxObj != null){

                                    int outboxId = outboxObj.has("id") ? outboxObj.getInt("id") : 0;
                                    String outboxMessage_id = outboxObj.has("message_id") ? outboxObj.getString("message_id") : "";
                                    String outboxTimestamp = outboxObj.has("timestamp") ? outboxObj.getString("timestamp") : "";
                                    String outboxDestination = outboxObj.has("destination") ? outboxObj.getString("destination") : "";
                                    String outboxText = outboxObj.has("text") ? outboxObj.getString("text") : "";
                                    String outboxStatus = outboxObj.has("status") ? outboxObj.getString("status") : "";
                                    String outboxDelivery_time = outboxObj.has("delivery_time") ? outboxObj.getString("delivery_time") : "";
                                    String outboxCreated_at = outboxObj.has("created_at") ? outboxObj.getString("created_at") : "";

                                    Sms.Outbox outbox = new Sms.Outbox(outboxId,outboxMessage_id,outboxTimestamp,outboxDestination,outboxText,outboxStatus,outboxDelivery_time,outboxCreated_at);

                                    sms = new Sms(id, timestamp,source,text,short_code,inboxStatus,latency,outbox);

                                }else {
                                    sms = new Sms(id, timestamp,source,text,short_code,inboxStatus,latency);
                                }

                                smsArrayList.add(sms);
                                mAdapter.notifyDataSetChanged();
                            }

                        }else {
                            //not data found
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
                myShouldLoadMore =true;

                VolleyLog.d("VOLLEY ERROR", "Error: " + error.getMessage());
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

    private void showDetailsBottomSheetDialog(final Sms sms) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.dialog_sms_details, null);
        ((TextView) view.findViewById(R.id.phone_no)).setText(sms.getSource());
        ((TextView) view.findViewById(R.id.timestamp)).setText(sms.getTimestamp());
        ((TextView) view.findViewById(R.id.latency)).setText("Latency: "+sms.getLatency());
        ((TextView) view.findViewById(R.id.message)).setText(sms.getText());
        ((TextView) view.findViewById(R.id.response)).setText(sms.getOutbox()==null?"No response":sms.getOutbox().getText());
        (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
            }
        });



        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });

    }


}