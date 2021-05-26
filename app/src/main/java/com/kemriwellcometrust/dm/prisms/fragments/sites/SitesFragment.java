package com.kemriwellcometrust.dm.prisms.fragments.sites;

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
import com.kemriwellcometrust.dm.prisms.MainActivity;
import com.kemriwellcometrust.dm.prisms.R;
import com.kemriwellcometrust.dm.prisms.adapters.SitesAdapter;
import com.kemriwellcometrust.dm.prisms.dependencies.Constants;
import com.kemriwellcometrust.dm.prisms.dependencies.Dialogs;
import com.kemriwellcometrust.dm.prisms.dependencies.PrismsApplication;
import com.kemriwellcometrust.dm.prisms.dependencies.VolleyErrors;
import com.kemriwellcometrust.dm.prisms.models.Site;
import com.kemriwellcometrust.dm.prisms.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class SitesFragment extends Fragment {


    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;
    public ProgressDialog mProgressDialog;


    private SitesAdapter mAdapter;
    private ArrayList<Site> sitesArrayList;


    @BindView(R.id.shimmer_my_container)
    ShimmerFrameLayout shimmer_my_container;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.no_sites)
    LinearLayout no_sites;

    @BindView(R.id.btn_create_site)
    Button btn_create_site;


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
        root = inflater.inflate(R.layout.fragment_sites, container, false);
        unbinder = ButterKnife.bind(this, root);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);

        btn_create_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siteDialog("Create a new site",null);
            }
        });

        sitesArrayList = new ArrayList<>();
        mAdapter = new SitesAdapter(context, sitesArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnDeleteListener(new SitesAdapter.OnDeleteListener() {
            @Override
            public void onItemClick(int position) {
                Site site = sitesArrayList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm site deletion?");
                builder.setMessage("Are you sure you want to delete the site: "+site.getSite_name()+"?");
                builder.setPositiveButton("Yes, delete!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgressDialog();
                        deleteSite(site.getId());
                    }
                });
                builder.setNegativeButton("Cancel", null);

                builder.show();
            }
        });


        mAdapter.setOnEditListener(new SitesAdapter.OnEditListener() {
            @Override
            public void onItemClick(int position) {
                Site site = sitesArrayList.get(position);
                siteDialog("Edit site", site);
            }
        });

        getSites();

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

    private void siteDialog(String titleStr, Site site) {
        final Dialog dialog = new Dialog( context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_create_site);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        EditText siteET = dialog.findViewById(R.id.et_site);
        TextView title = dialog.findViewById(R.id.title);
        Button btn = dialog.findViewById(R.id.btn_create_site);


        title.setText(titleStr);

        if (site!=null){
            siteET.setText(site.getSite_name());
            btn.setText("Update");
        }



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(siteET.getText().toString())) {
                    siteET.setError("Please enter the site name");
                } else {
                    showProgressDialog();
                    if (site == null)
                        createSite(siteET.getText().toString());
                    else
                        editSite(siteET.getText().toString(),site.getId());
                    dialog.dismiss();
                }
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void deleteSite(int siteId) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.DELETE_SITE+siteId, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    boolean success = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "";
                    String errors = response.has("errors") ? response.getString("errors") : "";

                    if (success) {
                        Dialogs.showOkDialog(context,"Success",message);
                        getSites();
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

    private void createSite(String siteName) {

        JSONObject payload = new JSONObject();
        try {
            payload.put("site_name", siteName);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Stash.getString(Constants.END_POINT)+ Constants.SITES, payload, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    boolean success = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "";
                    String errors = response.has("errors") ? response.getString("errors") : "";

                    if (success) {
                        Dialogs.showOkDialog(context,"Success",message);
                        getSites();
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

    private void editSite(String siteName, int siteId) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("site_name", siteName);
            payload.put("id", siteId);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Stash.getString(Constants.END_POINT)+ Constants.UPDATE_SITE, payload, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    boolean success = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "";
                    String errors = response.has("errors") ? response.getString("errors") : "";

                    if (success) {
                        Dialogs.showOkDialog(context,"Success",message);
                        getSites();
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

    private void getSites() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.SITES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

//                    Log.e("resoponse", response.toString());

                    sitesArrayList.clear();

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

                            if (no_sites!=null)
                                no_sites.setVisibility(View.GONE);



                            for (int i = 0; i < myArray.length(); i++) {

                                JSONObject item = (JSONObject) myArray.get(i);


                                int  id = item.has("id") ? item.getInt("id") : 0;
                                String site_name = item.has("site_name") ? item.getString("site_name") : "";

                                Site site = new Site(site_name,id);

                                sitesArrayList.add(site);
                                mAdapter.notifyDataSetChanged();

                            }

                        }else {
                            //not data found
                            if (no_sites!=null)
                                no_sites.setVisibility(View.VISIBLE);

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