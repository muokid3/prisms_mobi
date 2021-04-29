package com.kemriwellcome.dm.prisms.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import com.kemriwellcome.dm.prisms.R;
import com.kemriwellcome.dm.prisms.dependencies.Constants;
import com.kemriwellcome.dm.prisms.dependencies.Dialogs;
import com.kemriwellcome.dm.prisms.models.SiteStudy;
import com.kemriwellcome.dm.prisms.models.User;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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

    @BindView(R.id.randomise_btn)
    Button randomise_btn;

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

}
