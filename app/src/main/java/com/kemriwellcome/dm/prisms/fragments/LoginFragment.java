package com.kemriwellcome.dm.prisms.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.kemriwellcome.dm.prisms.AuthActivity;
import com.kemriwellcome.dm.prisms.MainActivity;
import com.kemriwellcome.dm.prisms.R;
import com.kemriwellcome.dm.prisms.dependencies.Constants;
import com.kemriwellcome.dm.prisms.dependencies.Dialogs;
import com.kemriwellcome.dm.prisms.dependencies.PrismsApplication;
import com.kemriwellcome.dm.prisms.dependencies.VolleyErrors;
import com.kemriwellcome.dm.prisms.interfaces.NavigationHost;
import com.kemriwellcome.dm.prisms.models.User;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment representing the login screen for Shrine.
 */
public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Unbinder unbinder;


    @BindView(R.id.input_email)
    TextInputEditText input_email;

    @BindView(R.id.input_password)
    TextInputEditText input_password;

    @BindView(R.id.text_feedback)
    TextView text_feedback;


    @BindView(R.id.progressBar)
    AVLoadingIndicatorView progressBar;

    @BindView(R.id.btn_login)
    Button btn_login;

    @BindView(R.id.forgot_pin)
    Button forgot_pin;


    @BindView(R.id.image)
    CircularImageView image;


//    private BiometricPrompt.PromptInfo promptInfo;




    Context context;
    NavigationHost navigationHost;
    public static Fragment getInstance(AuthActivity context){

        LoginFragment frag = new LoginFragment();
        frag.context = context;
        frag.navigationHost = (NavigationHost) context;
        return frag;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);



        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    signInAnonymously();
                }

            }
        };




        forgot_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPassResetDialog();
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(input_email.getText().toString())){
                    input_email.setError("Please enter your email");
                }else if (TextUtils.isEmpty(input_password.getText().toString())){
                    input_password.setError("Please enter your password");
                }
                else{
                    progressBar.show();
                    doLogin();
                }
            }
        });


//        Executor newExecutor = Executors.newSingleThreadExecutor();

//        final BiometricPrompt myBiometricPrompt = new BiometricPrompt(LoginFragment.this, newExecutor, new BiometricPrompt.AuthenticationCallback(){
//
//            //onAuthenticationError is called when a fatal error occurrs//
//            @Override
//            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
//                super.onAuthenticationError(errorCode, errString);
//                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
//                } else {
//                        //Print a message to Logcat//
////                    Log.d(TAG, "An unrecoverable error occurred");
//                }
//            }
//
//            @Override
//            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
//                super.onAuthenticationSucceeded(result);
//
//                startActivity(new Intent(context, HomeActivity.class));
//                getActivity().finish();
////                Log.d(TAG, "Fingerprint recognised successfully");
//            }
//
//
//            //onAuthenticationFailed is called when the fingerprint doesn’t match//
//            @Override
//            public void onAuthenticationFailed() {
//                super.onAuthenticationFailed();
//                //Print a message to Logcat//
////                Log.d(TAG, "Fingerprint not recognised");
//            }
//
//        });



//        promptInfo = new BiometricPrompt.PromptInfo.Builder()
//                .setTitle("Sawazisha")
//                .setSubtitle("Biometrics login")
//                .setDescription("Use your fingerprint/biometrics to log in")
//                .setNegativeButtonText("Cancel")
//                .build();

//        view.findViewById(R.id.fingerPrintAuth).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myBiometricPrompt.authenticate(promptInfo);
//            }
//        });

        return view;
    }

//    private PinLockListener mPinLockListener = new PinLockListener() {
//        @Override
//        public void onComplete(String pin) {
//            if (pin.length() == 4) {
//                progressBar.show();
//                doLogin(pin);
//
//            }
////            Log.d(TAG, "Pin complete: " + pin);
//        }
//
//        @Override
//        public void onEmpty() {
//            Log.d(TAG, "Pin empty");
//        }
//
//        @Override
//        public void onPinChange(int pinLength, String intermediatePin) { }
//    };

    private void doLogin() {
//        JSONObject payload = new JSONObject();
//        try {
//            payload.put("email",input_email.getText().toString());
//            payload.put("password", input_password.getText().toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                Stash.getString(Constants.END_POINT)+Constants.LOGIN, payload, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                //Log.e("RESPONSE::", response.toString());
//
//                if (progressBar.isShown())
//                    progressBar.hide();
//
//                try {
//                    boolean success = response.has("success") && response.getBoolean("success");
//                    String message = response.has("message") ? response.getString("message") : "";
//                    String errors = response.has("errors") ? response.getString("errors") : "";
//                    if (success) {
//
//
//                        //store user to shared prefs using stash
//
//                        String access_token = response.has("access_token") ? response.getString("access_token") : "" ;
//                        String token_type = response.has("token_type") ? response.getString("token_type") : "" ;
//
//                        JSONObject user = response.has("user") ? response.getJSONObject("user") : null;
//
//                        if (user != null){
//                            int  user_id = user.has("id") ? user.getInt("id") : 0 ;
//                            String email = user.has("email") ? user.getString("email") : "" ;
//                            String wallet_balance = user.has("wallet_balance") ? user.getString("wallet_balance") : "0.0" ;
//
//                            JSONObject profile = user.has("profile") ? user.getJSONObject("profile") : null;
//
//                            if (profile != null){
//                                int  profile_id = profile.has("profile_id") ? profile.getInt("profile_id") : 0 ;
//                                String msisdn = profile.has("msisdn") ? profile.getString("msisdn") : "" ;
//                                String id_number = profile.has("id_number") ? profile.getString("id_number") : "" ;
//                                String phone_imei = profile.has("phone_imei") ? profile.getString("phone_imei") : "" ;
//                                String first_name = profile.has("first_name") ? profile.getString("first_name") : "" ;
//                                String last_name = profile.has("last_name") ? profile.getString("last_name") : "" ;
//                                String is_defaulter = profile.has("is_defaulter") ? profile.getString("is_defaulter") : "" ;
//                                int  default_count = profile.has("default_count") ? profile.getInt("default_count") : 0 ;
//                                int  is_validated_on_crb = profile.has("is_validated_on_crb") ? profile.getInt("is_validated_on_crb") : 0 ;
//                                String crb_pass_status = profile.has("crb_pass_status") ? profile.getString("crb_pass_status") : "" ;
//                                String mpesa_number_verified = profile.has("mpesa_number_verified") ? profile.getString("mpesa_number_verified") : "" ;
//                                String status = profile.has("status") ? profile.getString("status") : "" ;
//
//                                User newUser = new User(access_token,token_type,user_id,profile_id,email,msisdn,id_number,phone_imei,first_name,last_name,is_defaulter,
//                                        default_count,is_validated_on_crb,crb_pass_status,mpesa_number_verified,status,wallet_balance);
//
//                                Stash.put(Constants.USER, newUser);
//
//                                Intent intent = new Intent(context, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//
//
//                            }else {
//                                Dialogs.showWarningDialog(context,"Profile Error","A fatal error occurred. Please contact system administrator for assistance");
//                            }
//                        }else {
//                            Dialogs.showWarningDialog(context,"User Error","A fatal error occurred. Please contact system administrator for assistance");
//                        }
//
//                    } else {
//                        Dialogs.showWarningDialog(context,message,errors);
//                    }
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
//                if (progressBar!=null)
//                    progressBar.hide();
//
//                NetworkResponse response = error.networkResponse;
//                if(response != null && response.data != null){
//                    String body;
//                    //get status code here
//                    String statusCode = String.valueOf(error.networkResponse.statusCode);
//                    //get response body and parse with appropriate encoding
//                    if(error.networkResponse.data!=null) {                    try {
//                        body = new String(error.networkResponse.data,"UTF-8");
//
//                        JSONObject json = new JSONObject(body);
////                            Log.e("error response : ", json.toString());
//
//
//                        String message = json.has("message") ? json.getString("message") : "";
//                        String errors = json.has("errors") ? json.getString("errors") : "";
//
//                        Dialogs.showWarningDialog(context,message,errors);
//
//
//
//                    } catch (UnsupportedEncodingException | JSONException e) {
//                        e.printStackTrace();
//                        }
//                    }
//
//                }else {
//                    Toast.makeText(context, VolleyErrors.getVolleyErrorMessages(error, context), Toast.LENGTH_LONG).show();
//                }
//
////             Log.e(TAG, "Error: " + error.getMessage());
//            }
//        }){
//            /**
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                //headers.put("Authorization", loggei);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");
//                return headers;
//            }
//        };
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//
//        PrismsApplication.getInstance().addToRequestQueue(jsonObjReq);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FIREBASE::", "signInAnonymously:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FIREBASE::", "signInAnonymously:failure", task.getException());
//                            Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });

    }


//    private void sendResetOTP() {
//
//        JSONObject payload = new JSONObject();
//        try {
//            payload.put("api_user", Stash.getString(Constants.API_USER));
//            payload.put("api_key", Stash.getString(Constants.API_KEY));
//            payload.put("msisdn", Stash.getString(Constants.MSISDN));
//            payload.put("request", Constants.SEND_RESET_OTP);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                Stash.getString(Constants.END_POINT), payload, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.e(TAG, response.toString());
//
////                if (progressBar.isShown())
////                    progressBar.hide();
//                try {
//                    int status = response.has("status") ? response.getInt("status") : 0;
//                    String message = response.has("message") ? response.getString("message") : "";
//                    String reason = response.has("reason") ? response.getString("reason") : "";
//
//                    if (status == 200) {
//                        otpLayout.setVisibility(View.GONE);
//                        btReset.setVisibility(View.GONE);
//                    } else {
//                        reset_error_message.setText(reason +". Please try again.");
//                        reset_error_message.setTextColor(Color.RED);
//                        reset_error_message.setVisibility(View.VISIBLE);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                otpLayout.setVisibility(View.VISIBLE);
//
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (progressBar!=null)
//                    progressBar.hide();
//
//                NetworkResponse response = error.networkResponse;
//                if(response != null && response.data != null){
//                    String body;
//                    //get status code here
//                    String statusCode = String.valueOf(error.networkResponse.statusCode);
//                    //get response body and parse with appropriate encoding
//                    if(error.networkResponse.data!=null) {                    try {
//                        body = new String(error.networkResponse.data,"UTF-8");
//
//                        JSONObject json = new JSONObject(body);
////                            Log.e("error response : ", json.toString());
//
//
//                        String message = json.has("message") ? json.getString("message") : "";
//                        String reason = json.has("reason") ? json.getString("reason") : "";
//
//                        ErrorMessage bottomSheetFragment = ErrorMessage.newInstance(message,reason,context);
//                        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
//
////                        final SweetAlertDialog sweetAlert = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
////                        sweetAlert.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
////                        sweetAlert.setTitleText(message);
////                        sweetAlert.setContentText(reason);
////                        sweetAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                            @Override
////                            public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                sweetAlert.dismiss();
////                            }
////                        });
////                        sweetAlert.show();
//
//                    } catch (UnsupportedEncodingException | JSONException e) {
//                        e.printStackTrace();
//                    }
//                    }
//
//                }else {
//                    reset_error_message.setText(VolleyErrors.getVolleyErrorMessages(error, context));
//                    reset_error_message.setVisibility(View.VISIBLE);
//                }
//
////             Log.e(TAG, "Error: " + error.getMessage());
//            }
//        }){
//            /**
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                //headers.put("Authorization", loggei);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");
//                return headers;
//            }
//        };
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        FettiApplication.getInstance().addToRequestQueue(jsonObjReq);
//    }

//    private void verifyOTP(String otp) {
//
//        JSONObject payload = new JSONObject();
//        try {
//            payload.put("api_user", Stash.getString(Constants.API_USER));
//            payload.put("api_key", Stash.getString(Constants.API_KEY));
//            payload.put("msisdn", Stash.getString(Constants.MSISDN));
//            payload.put("otp", otp);
//            payload.put("request", Constants.VERIFY_RESET_OTP);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                Stash.getString(Constants.END_POINT), payload, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
////                Log.d(TAG, response.toString());
//
////                if (progressBar.isShown())
////                    progressBar.hide();
//
//                try {
//                    int status = response.has("status") ? response.getInt("status") : 0;
//                    String message = response.has("message") ? response.getString("message") : "";
//                    String reason = response.has("reason") ? response.getString("reason") : "";
//
//                    if (status == 200) {
//                        otpLayout.setVisibility(View.GONE);
//                        btReset.setVisibility(View.GONE);
//                        reset_error_message.setVisibility(View.GONE);
//                        changePinLayout.setVisibility(View.VISIBLE);
//                    } else {
//                        reset_error_message.setText(reason +". Please try again.");
//                        reset_error_message.setTextColor(Color.RED);
//                        reset_error_message.setVisibility(View.VISIBLE);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (progressBar!=null)
//                    progressBar.hide();
//
//                NetworkResponse response = error.networkResponse;
//                if(response != null && response.data != null){
//                    String body;
//                    //get status code here
//                    String statusCode = String.valueOf(error.networkResponse.statusCode);
//                    //get response body and parse with appropriate encoding
//                    if(error.networkResponse.data!=null) {                    try {
//                        body = new String(error.networkResponse.data,"UTF-8");
//
//                        JSONObject json = new JSONObject(body);
////                            Log.e("error response : ", json.toString());
//
//
//                        String message = json.has("message") ? json.getString("message") : "";
//                        String reason = json.has("reason") ? json.getString("reason") : "";
//
//                        ErrorMessage bottomSheetFragment = ErrorMessage.newInstance(message,reason,context);
//                        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
//
////                        final SweetAlertDialog sweetAlert = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
////                        sweetAlert.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
////                        sweetAlert.setTitleText(message);
////                        sweetAlert.setContentText(reason);
////                        sweetAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                            @Override
////                            public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                sweetAlert.dismiss();
////                            }
////                        });
////                        sweetAlert.show();
//
//                    } catch (UnsupportedEncodingException | JSONException e) {
//                        e.printStackTrace();
//                    }
//                    }
//
//                }else {
//                    reset_error_message.setText(VolleyErrors.getVolleyErrorMessages(error, context));
//                    reset_error_message.setVisibility(View.VISIBLE);
//                }
//
////             Log.e(TAG, "Error: " + error.getMessage());
//            }
//        }){
//            /**
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                //headers.put("Authorization", loggei);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");
//                return headers;
//            }
//        };
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        FettiApplication.getInstance().addToRequestQueue(jsonObjReq);
//    }
//
//    private void doSetPin() {
//        JSONObject payload  = new JSONObject();
//        try {
//            payload.put("api_user", Stash.getString(Constants.API_USER));
//            payload.put("api_key", Stash.getString(Constants.API_KEY));
//            payload.put("id_number", Stash.getString(Constants.ID_NUMBER));
//            payload.put("msisdn", Stash.getString(Constants.MSISDN));
//            payload.put("pin", pinEntryEditText.getText().toString());
//            payload.put("request", Constants.SET_PIN);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
//                Stash.getString(Constants.END_POINT), payload, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
////                Log.d(TAG, response.toString());
////                if (progressBar!=null)
////                    progressBar.hide();
//
//                try {
//                    int status = response.has("status") ? response.getInt("status") : 0;
//                    String message = response.has("message") ? response.getString("message") : "";
//                    String reason = response.has("reason") ? response.getString("reason") : "";
//
//                    if (status != 200){
//
//                        ErrorMessage bottomSheetFragment = ErrorMessage.newInstance(message,reason,context);
//                        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
//
////                        final SweetAlertDialog sweetAlert = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
////                        sweetAlert.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
////                        sweetAlert.setTitleText("OOPS! :(");
////                        sweetAlert.setContentText(reason);
////                        sweetAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                            @Override
////                            public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                sweetAlert.dismiss();
////                                dialog.dismiss();
////                            }
////                        });
////                        sweetAlert.show();
//                    }else {
//
//                        ErrorMessage bottomSheetFragment = ErrorMessage.newInstance("SUCCESS! :)","Your PIN has been reset successfully!",context);
//                        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
//
//
////                        final SweetAlertDialog sweetAlert = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
////                        sweetAlert.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
////                        sweetAlert.setTitleText("SUCCESS! :)");
////                        sweetAlert.setContentText("Your PIN has been reset successfully!");
////                        sweetAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                            @Override
////                            public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                sweetAlert.dismiss();
////                                dialog.dismiss();
////                            }
////                        });
////                        sweetAlert.show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getContext(),
//                            "Error: " + e.getMessage(),
//                            Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (progressBar!=null)
//                    progressBar.hide();
//                //                VolleyLog.d(TAG, "Error: " + error.getMessage());
//
//                NetworkResponse response = error.networkResponse;
//                if(response != null && response.data != null){
//                    String body;
//                    //get status code here
//                    String statusCode = String.valueOf(error.networkResponse.statusCode);
//                    //get response body and parse with appropriate encoding
//                    if(error.networkResponse.data!=null) {                    try {
//                        body = new String(error.networkResponse.data,"UTF-8");
//
//                        JSONObject json = new JSONObject(body);
////                            Log.e("error response : ", json.toString());
//
//
//                        String message = json.has("message") ? json.getString("message") : "";
//                        String reason = json.has("reason") ? json.getString("reason") : "";
//
//                        ErrorMessage bottomSheetFragment = ErrorMessage.newInstance(message,reason,context);
//                        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
//
////                        final SweetAlertDialog sweetAlert = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
////                        sweetAlert.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
////                        sweetAlert.setTitleText(message);
////                        sweetAlert.setContentText(reason);
////                        sweetAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
////                            @Override
////                            public void onClick(SweetAlertDialog sweetAlertDialog) {
////                                sweetAlert.dismiss();
////                            }
////                        });
////                        sweetAlert.show();
//
//                    } catch (UnsupportedEncodingException | JSONException e) {
//                        e.printStackTrace();
//                    }
//                    }
//
//                }else {
//                    Toast.makeText(context,VolleyErrors.getVolleyErrorMessages(error, context), Toast.LENGTH_LONG).show();
//                }
//            }
//        }){
//            /**
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                //headers.put("Authorization", loggei);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");
//                return headers;
//            }
//        };
//
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        FettiApplication.getInstance().addToRequestQueue(jsonObjReq);
//
//    }



}
