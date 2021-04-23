package com.kemriwellcome.dm.prisms.fragments.allocation;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fxn.stash.Stash;
import com.google.android.material.snackbar.Snackbar;
import com.kemriwellcome.dm.prisms.MainActivity;
import com.kemriwellcome.dm.prisms.R;
import com.kemriwellcome.dm.prisms.dependencies.Constants;
import com.kemriwellcome.dm.prisms.dependencies.PrismsApplication;
import com.kemriwellcome.dm.prisms.dependencies.VolleyErrors;
import com.kemriwellcome.dm.prisms.models.Site;
import com.kemriwellcome.dm.prisms.models.Stratum;
import com.kemriwellcome.dm.prisms.models.Study;
import com.kemriwellcome.dm.prisms.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kemriwellcome.dm.prisms.dependencies.PrismsApplication.TAG;


public class UploadListFragment extends Fragment {


    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;

    private int siteID = 0;
    private int studyID = 0;
    private int stratumID = 0;


    private ArrayList<Site> sitesArrayList;
    private ArrayList<String> sitesList;

    private ArrayList<Study> studyArrayList;
    private ArrayList<String> studyList;

    private ArrayList<Stratum> stratumArrayList;
    private ArrayList<String> stratumList;

    public static final int PICKFILE_RESULT_CODE = 1;
    public static final int REQUEST_READ_STORAGE = 2;

    private Uri fileUri;
    private String filePath;


    @BindView(R.id.btn_upload_list)
    Button btn_upload_list;

    @BindView(R.id.select_file)
    Button select_file;

    @BindView(R.id.stratumSpinner)
    Spinner stratumSpinner;

    @BindView(R.id.studySpinner)
    Spinner studySpinner;

    @BindView(R.id.siteSpinner)
    Spinner siteSpinner;


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
        root = inflater.inflate(R.layout.fragment_upload_list, container, false);
        unbinder = ButterKnife.bind(this, root);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);

        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
                } else {
                    Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseFile.setType("*/*");
                    chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                    startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
                }

            }
        });

        getSites();
        getStudies();
        getStrata();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == -1) {
                    fileUri = data.getData();
                    filePath = fileUri.getPath();

                    Toast.makeText(context, "File path:::"+filePath, Toast.LENGTH_SHORT).show();

                    //needs storage permissions
                    File file = new File(fileUri.getPath());

                    Log.e("file", file.getName());

                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        }
    }

    private void getSites() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.SITES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
//                Log.d(TAG, response.toString());

                try {
                    if (response.getBoolean("success")) {

                        sitesArrayList = new ArrayList<Site>();
                        sitesList = new ArrayList<String>();

                        JSONArray jsonArray = response.getJSONArray("data");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = (JSONObject) jsonArray.get(i);

                            int  id = item.has("id") ? item.getInt("id") : 0;
                            String site_name = item.has("site_name") ? item.getString("site_name") : "";

                            Site site = new Site(site_name,id);


                            sitesArrayList.add(site);
                            sitesList.add(site.getSite_name());
                        }

                        sitesArrayList.add(new Site("--select site--",0));
                        sitesList.add("--select site--");

                        ArrayAdapter<String> aa=new ArrayAdapter<String>(context,
                                android.R.layout.simple_spinner_dropdown_item,
                                sitesList){
                            @Override
                            public int getCount() {
                                return super.getCount()-1; // you dont display last item. It is used as hint.
                            }
                        };

                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        siteSpinner.setAdapter(aa);
                        siteSpinner.setSelection(aa.getCount());

                        siteID = sitesArrayList.get(0).getId();

                        siteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                siteID = sitesArrayList.get(i).getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                    } else {
                        Toast.makeText(context, "An fatal error occurred. Please try again later", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(e.getMessage(), context));
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(VolleyErrors.getVolleyErrorMessages(error, getContext()), context));
            }
        })
        {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", loggedInUser.getToken_type()+" "+loggedInUser.getAccess_token());
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PrismsApplication.getInstance().addToRequestQueue(jsonObjReq);


    }

    private void getStudies() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.STUDIES, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
//                Log.d(TAG, response.toString());

                try {
                    if (response.getBoolean("success")) {

                        studyArrayList = new ArrayList<Study>();
                        studyList = new ArrayList<String>();

                        JSONArray jsonArray = response.getJSONArray("data");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = (JSONObject) jsonArray.get(i);

                            int  id = item.has("id") ? item.getInt("id") : 0;
                            String studyTxt = item.has("study") ? item.getString("study") : "";
                            String study_detail = item.has("study_detail") ? item.getString("study_detail") : "";

                            Study study = new Study(id,studyTxt,study_detail);


                            studyArrayList.add(study);
                            studyList.add(study.getStudy_name());
                        }

                        studyArrayList.add(new Study(0,"--select study--","select study"));
                        studyList.add("--select study--");

                        ArrayAdapter<String> aa=new ArrayAdapter<String>(context,
                                android.R.layout.simple_spinner_dropdown_item,
                                studyList){
                            @Override
                            public int getCount() {
                                return super.getCount()-1; // you dont display last item. It is used as hint.
                            }
                        };

                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        studySpinner.setAdapter(aa);
                        studySpinner.setSelection(aa.getCount());

                        studyID = studyArrayList.get(0).getId();

                        studySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                studyID = studyArrayList.get(i).getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                    } else {
                        Toast.makeText(context, "An fatal error occurred. Please try again later", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(e.getMessage(), context));
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(VolleyErrors.getVolleyErrorMessages(error, getContext()), context));
            }
        })
        {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", loggedInUser.getToken_type()+" "+loggedInUser.getAccess_token());
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PrismsApplication.getInstance().addToRequestQueue(jsonObjReq);


    }

    private void getStrata() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.STRATA, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
//                Log.d(TAG, response.toString());

                try {
                    if (response.getBoolean("success")) {

                        stratumArrayList = new ArrayList<Stratum>();
                        stratumList = new ArrayList<String>();

                        JSONArray jsonArray = response.getJSONArray("data");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = (JSONObject) jsonArray.get(i);

                            int  id = item.has("id") ? item.getInt("id") : 0;
                            String stratumTxt = item.has("stratum") ? item.getString("stratum") : "";

                            Stratum stratum = new Stratum(id,stratumTxt);


                            stratumArrayList.add(stratum);
                            stratumList.add(stratum.getStratum());
                        }

                        stratumArrayList.add(new Stratum(0,"--select stratum--"));
                        stratumList.add("--select stratum--");

                        ArrayAdapter<String> aa=new ArrayAdapter<String>(context,
                                android.R.layout.simple_spinner_dropdown_item,
                                stratumList){
                            @Override
                            public int getCount() {
                                return super.getCount()-1; // you dont display last item. It is used as hint.
                            }
                        };

                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        stratumSpinner.setAdapter(aa);
                        stratumSpinner.setSelection(aa.getCount());

                        stratumID = stratumArrayList.get(0).getId();

                        stratumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                stratumID = stratumArrayList.get(i).getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                    } else {
                        Toast.makeText(context, "An fatal error occurred. Please try again later", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(e.getMessage(), context));
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
                MainActivity.getInstance().snack(VolleyErrors.getVolleyErrorMessages(VolleyErrors.getVolleyErrorMessages(error, getContext()), context));
            }
        })
        {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", loggedInUser.getToken_type()+" "+loggedInUser.getAccess_token());
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PrismsApplication.getInstance().addToRequestQueue(jsonObjReq);


    }

}