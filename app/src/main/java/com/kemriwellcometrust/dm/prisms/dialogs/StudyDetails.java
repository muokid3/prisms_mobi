package com.kemriwellcometrust.dm.prisms.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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


public class StudyDetails extends BottomSheetDialogFragment {


    private SiteStudy siteStudy;
    private Context context;
    private Unbinder unbinder;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    private String message = "";



    @BindView(R.id.study_name)
    TextView study_name;

    @BindView(R.id.study_detail)
    TextView study_detail;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.date_initiated)
    TextView date_initiated;

    @BindView(R.id.site_name)
    TextView site_name;

    @BindView(R.id.total_randomizations)
    TextView total_randomizations;

    @BindView(R.id.randomise_btn)
    Button randomise_btn;

    @BindView(R.id.piechart)
    PieChart piechart;

    private User loggedInUser;

    public StudyDetails() {
        // Required empty public constructor
    }


    public static StudyDetails newInstance(SiteStudy siteStudy, Context context) {
        StudyDetails fragment = new StudyDetails();
        fragment.siteStudy = siteStudy;
        fragment.context = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.study_details_bottom_sheet, container, false);
        unbinder = ButterKnife.bind(this, view);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);


        study_name.setText(siteStudy.getStudy_name());
        study_detail.setText(siteStudy.getStudy_detail());
        status.setText("Status: "+siteStudy.getStatus());
        date_initiated.setText("Initiated on: "+siteStudy.getDate_initiated());
        site_name.setText("Site: "+siteStudy.getSite_name());


        randomise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRandomiseDialog(siteStudy);
                dismiss();
            }
        });

//        piechart.setUsePercentValues(true);
//        piechart.getDescription().setEnabled(false);
//        piechart.setExtraOffsets(5, 10, 5, 5);
//
//        piechart.setDragDecelerationFrictionCoef(0.95f);
//
//
//        piechart.setDrawHoleEnabled(true);
//        piechart.setHoleColor(Color.WHITE);
//
//
//        piechart.setDrawCenterText(true);
//
//        piechart.setRotationAngle(0);
//        // enable rotation of the chart by touch
//        piechart.setRotationEnabled(true);
//        piechart.setHighlightPerTapEnabled(true);

        //getChartData();
        getTotalRandz();

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showRandomiseDialog(SiteStudy siteStudy) {
        final Dialog dialog = new Dialog( context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_randomise);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView study_name = dialog.findViewById(R.id.study_name);
        TextView site_name = dialog.findViewById(R.id.site_name);
        EditText et_ip_no = dialog.findViewById(R.id.et_ip_no);

        study_name.setText("Study: "+siteStudy.getStudy_name());
        site_name.setText("Site: "+siteStudy.getSite_name());



        ((Button) dialog.findViewById(R.id.randomise_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_ip_no.getText().toString())) {
                    et_ip_no.setError("Please enter IP Number");
                } else {
                    randomise(siteStudy, et_ip_no.getText().toString());
                    dialog.dismiss();
                }
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void randomise(SiteStudy siteStudy, String ipNo) {
        message = "randomise "+ipNo+" to "+siteStudy.getStudy_name()+" "+siteStudy.getSite_name()+" "+loggedInUser.getPhone_no();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Constants.SHORTCODE, null, message, null, null);

            Dialogs.showOkDialog(context,"Request has been sent", "The randomisation request below has been sent via SMS\n\n"+message);

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(Constants.SHORTCODE, null, message, null, null);

                    Dialogs.showOkDialog(context,"Request has been sent", "The randomisation request below has been sent via SMS\n\n"+message);

                } else {
                    Toast.makeText(context,
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    private void getChartData() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.STUDY_ALLOCATION_GRAPH+siteStudy.getStudy_id(), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                   Log.e("resoponse", response.toString());


                    boolean  status = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "" ;
                    String errors = response.has("errors") ? response.getString("errors") : "" ;


                    if (status){
                        JSONArray myArray = response.getJSONArray("data");

                        if (myArray.length() > 0){



                            List<PieEntry> entries = new ArrayList<>();

                            for (int i = 0; i < myArray.length(); i++) {

                                JSONObject item = (JSONObject) myArray.get(i);


                                double  total = item.has("total") ? item.getDouble("total") : 0.0;
                                String allocation = item.has("allocation") ? item.getString("allocation") : "";

                                entries.add(new PieEntry((float) total, allocation));
                            }

                            PieDataSet set = new PieDataSet(entries, "");
                            set.setColors(ColorTemplate.COLORFUL_COLORS);

                            PieData data = new PieData(set);
                            piechart.setData(data);
                            piechart.setCenterText("Allocation Rate");

                            piechart.invalidate(); // refresh

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

    private void getTotalRandz () {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.TOTAL_RANDZ, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

//                    Log.e("resoponse", response.toString());


                    boolean  status = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "" ;
                    String errors = response.has("errors") ? response.getString("errors") : "" ;


                    if (status){
                        total_randomizations.setText(message);
                        total_randomizations.setVisibility(View.VISIBLE);
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


}
