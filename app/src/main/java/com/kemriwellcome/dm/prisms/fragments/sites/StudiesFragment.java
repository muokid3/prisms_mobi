package com.kemriwellcome.dm.prisms.fragments.sites;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.kemriwellcome.dm.prisms.MainActivity;
import com.kemriwellcome.dm.prisms.R;
import com.kemriwellcome.dm.prisms.adapters.StudyAdapter;
import com.kemriwellcome.dm.prisms.dependencies.Constants;
import com.kemriwellcome.dm.prisms.dependencies.Dialogs;
import com.kemriwellcome.dm.prisms.dependencies.PrismsApplication;
import com.kemriwellcome.dm.prisms.dependencies.VolleyErrors;
import com.kemriwellcome.dm.prisms.models.Site;
import com.kemriwellcome.dm.prisms.models.Study;
import com.kemriwellcome.dm.prisms.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class StudiesFragment extends Fragment {


    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;
    public ProgressDialog mProgressDialog;


    private StudyAdapter mAdapter;
    private ArrayList<Study> studyArrayList;

    @BindView(R.id.shimmer_my_container)
    ShimmerFrameLayout shimmer_my_container;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.no_studies)
    LinearLayout no_studies;

    @BindView(R.id.btn_create_study)
    Button btn_create_study;


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
        root = inflater.inflate(R.layout.fragment_studies, container, false);
        unbinder = ButterKnife.bind(this, root);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);

        btn_create_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyDialog("Create a new study", null);
            }
        });

        studyArrayList = new ArrayList<>();
        mAdapter = new StudyAdapter(context, studyArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnDeleteListener(new StudyAdapter.OnDeleteListener() {
            @Override
            public void onItemClick(int position) {
                Study study = studyArrayList.get(position);


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm study deletion?");
                builder.setMessage("Are you sure you want to delete the study: "+study.getStudy_name()+"?");
                builder.setPositiveButton("Yes, delete!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgressDialog();
                        deleteStudy(study.getId());
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });


        mAdapter.setOnEditListener(new StudyAdapter.OnEditListener() {
            @Override
            public void onItemClick(int position) {
                Study study = studyArrayList.get(position);
                studyDialog("Edit study", study);
            }
        });

        getStudies();




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
        shimmer_my_container.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        shimmer_my_container.stopShimmerAnimation();
        super.onPause();
    }

    private void studyDialog(String titleStr, Study study) {
        final Dialog dialog = new Dialog( context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_create_study);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView title = dialog.findViewById(R.id.title);
        EditText studyET = dialog.findViewById(R.id.et_study);
        EditText studyDetailET = dialog.findViewById(R.id.et_study_detail);
        Button btn = dialog.findViewById(R.id.btn_create_study);


        title.setText(titleStr);

        if (study!=null){
            studyET.setText(study.getStudy_name());
            studyDetailET.setText(study.getStudy_detail());
            btn.setText("Update");
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(studyET.getText().toString())) {
                    studyET.setError("Please enter the study name");
                } else if (TextUtils.isEmpty(studyDetailET.getText().toString())) {
                    studyDetailET.setError("Please enter the study details");
                } else {
                    showProgressDialog();
                    if (study == null)
                        createStudy(studyET.getText().toString(),studyDetailET.getText().toString());
                    else
                        editStudy(studyET.getText().toString(),studyDetailET.getText().toString(), study.getId());
                    dialog.dismiss();
                }
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void createStudy(String studyName, String studyDetail) {

        JSONObject payload = new JSONObject();
        try {
            payload.put("name", studyName);
            payload.put("detail", studyDetail);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Stash.getString(Constants.END_POINT)+ Constants.STUDIES, payload, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    boolean success = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "";
                    String errors = response.has("errors") ? response.getString("errors") : "";

                    if (success) {
                        Dialogs.showOkDialog(context,"Success",message);
                        getStudies();
                    } else {
                        Dialogs.showWarningDialog(context,message,errors);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON Exception: ", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
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

    private void editStudy(String studyName, String studyDetail, int studyId) {

        JSONObject payload = new JSONObject();
        try {
            payload.put("name", studyName);
            payload.put("detail", studyDetail);
            payload.put("id", studyId);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Stash.getString(Constants.END_POINT)+ Constants.UPDATE_STUDY, payload, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    boolean success = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "";
                    String errors = response.has("errors") ? response.getString("errors") : "";

                    if (success) {
                        Dialogs.showOkDialog(context,"Success",message);
                        getStudies();
                    } else {
                        Dialogs.showWarningDialog(context,message,errors);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON Exception: ", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
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

    private void deleteStudy(int studyId) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.DELETE_STUDY+studyId, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //Log.e("response", response.toString());
                hideProgressDialog();
                try {
                    boolean success = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "";
                    String errors = response.has("errors") ? response.getString("errors") : "";

                    if (success) {
                        Dialogs.showOkDialog(context,"Success",message);
                        getStudies();
                    } else {
                        Dialogs.showWarningDialog(context,message,errors);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON Exception: ", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
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

    private void getStudies() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.STUDIES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

//                    Log.e("resoponse", response.toString());

                    studyArrayList.clear();

                    if (recyclerView!=null)
                        recyclerView.setVisibility(View.VISIBLE);

                    if (shimmer_my_container!=null){
                        shimmer_my_container.stopShimmerAnimation();
                        shimmer_my_container.setVisibility(View.GONE);
                    }


                    boolean  status = response.has("success") && response.getBoolean("success");
                    String  message = response.has("message") ? response.getString("message") : "" ;
                    String  errors = response.has("errors") ? response.getString("errors") : "" ;


                    if (status){
                        JSONArray myArray = response.getJSONArray("data");

                        if (myArray.length() > 0){

                            if (no_studies!=null)
                                no_studies.setVisibility(View.GONE);

                            for (int i = 0; i < myArray.length(); i++) {

                                JSONObject item = (JSONObject) myArray.get(i);


                                int  id = item.has("id") ? item.getInt("id") : 0;
                                String study_name = item.has("study") ? item.getString("study") : "";
                                String study_detail = item.has("study_detail") ? item.getString("study_detail") : "";

                                Study study = new Study(id,study_name,study_detail);

                                studyArrayList.add(study);
                                mAdapter.notifyDataSetChanged();

                            }

                        }else {
                            //not data found
                            if (no_studies!=null)
                                no_studies.setVisibility(View.VISIBLE);

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


}