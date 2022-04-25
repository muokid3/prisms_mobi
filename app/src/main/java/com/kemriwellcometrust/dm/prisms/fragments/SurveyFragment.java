package com.kemriwellcometrust.dm.prisms.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.kemriwellcometrust.dm.prisms.MainActivity;
import com.kemriwellcometrust.dm.prisms.R;
import com.kemriwellcometrust.dm.prisms.dependencies.Constants;
import com.kemriwellcometrust.dm.prisms.dependencies.Dialogs;
import com.kemriwellcometrust.dm.prisms.dependencies.PrismsApplication;
import com.kemriwellcometrust.dm.prisms.dependencies.VolleyErrors;
import com.kemriwellcometrust.dm.prisms.models.Answer;
import com.kemriwellcometrust.dm.prisms.models.Question;
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


public class SurveyFragment extends Fragment {


    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;
    public ProgressDialog mProgressDialog;


    private int selectedSingleAnswerId = 0;
    private String selectedAnswer = "";
    private boolean hasFollowUp = false;

    List<Integer> multipleAnswerIds = new ArrayList<>();

    private Question questionObject;



    @BindView(R.id.questionTV)
    TextView questionTV;

    @BindView(R.id.followUpQuestionTV)
    TextView followUpQuestionTV;

    @BindView(R.id.followup_answerET)
    EditText followup_answerET;

    @BindView(R.id.open_ended_answerET)
    EditText open_ended_answerET;

    @BindView(R.id.other_specifyET)
    EditText other_specifyET;

    @BindView(R.id.options_linear)
    LinearLayout options_linear;

    @BindView(R.id.followup_linear)
    LinearLayout followup_linear;

    @BindView(R.id.open_ended_linear)
    LinearLayout open_ended_linear;

    @BindView(R.id.specify_other_linear)
    LinearLayout specify_other_linear;

    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.no_survey)
    LinearLayout no_survey;


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
        root = inflater.inflate(R.layout.fragment_survey, container, false);
        unbinder = ButterKnife.bind(this, root);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionObject!=null){
                    if (questionObject.getType().equals("CHOICE")){
                        processChoiceQuestion();
                    }else if (questionObject.getType().equals("OPEN")){
                        processOpenEnded();
                    }
                }else {
                    Dialogs.showWarningDialog(context,"Question  not found", "Please select an answer from a valid question before submitting");
                }
            }
        });


        getQuestion();


        return root;
    }

    private void processOpenEnded() {

        if (TextUtils.isEmpty(open_ended_answerET.getText().toString())){
            open_ended_answerET.setError("Please type an answer");
        }else {
            JSONObject payload = new JSONObject();
            try {
                payload.put("question_id", questionObject.getId());
                payload.put("open_ended_answer", open_ended_answerET.getText().toString());

                postAnswer(payload);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void processChoiceQuestion() {
        if (questionObject.getAnswer_count().equals("SINGLE")){
            if (selectedSingleAnswerId == 0){
                Dialogs.showWarningDialog(context,"Missing answer", "Please select an answer");
            }else {
                if (selectedAnswer.equals("Other (specify)") && TextUtils.isEmpty(other_specifyET.getText().toString())){
                    Dialogs.showWarningDialog(context,"Specify other", "Please type on the text box to specify other");
                    other_specifyET.setError("This field is required");
                }else {

                    if (hasFollowUp && TextUtils.isEmpty(followup_answerET.getText().toString())){
                        Dialogs.showWarningDialog(context,"Answer required", "Please type on the text box to answer the additional question");
                        followup_answerET.setError("This field is required");
                    }else {
                        JSONObject payload = new JSONObject();
                        try {
                            payload.put("question_id", questionObject.getId());
                            payload.put("answer_id", selectedSingleAnswerId);
                            payload.put("details", other_specifyET.getText().toString());
                            payload.put("follow_up_answer", followup_answerET.getText().toString());

                            postAnswer(payload);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }else if (questionObject.getAnswer_count().equals("MULTIPLE")){
            if (multipleAnswerIds.size() == 0){
                Dialogs.showWarningDialog(context,"Answer required", "Please select at least one option from the answers");
            }else {
                JSONObject payload = new JSONObject();
                try {

                    StringBuilder str = new StringBuilder("");
                    // Traversing the ArrayList
                    for (int eachstring : multipleAnswerIds) {
                        // Each element in ArrayList is appended
                        // followed by comma
                        str.append(eachstring).append(",");
                    }

                    // StringBuffer to String conversion
                    String commaseparatedlist = str.toString();

                    // Condition check to remove the last comma
                    if (commaseparatedlist.length() > 0)
                        commaseparatedlist = commaseparatedlist.substring(0, commaseparatedlist.length() - 1);

                    payload.put("question_id", questionObject.getId());
                    payload.put("answers_ids", commaseparatedlist);

                    postAnswer(payload);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
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


    private void getQuestion() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Stash.getString(Constants.END_POINT)+ Constants.GET_QUESTION, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    //Log.e("resoponse", response.toString());



                    boolean  status = response.has("success") && response.getBoolean("success");
                    String  message = response.has("message") ? response.getString("message") : "" ;
                    String  errors = response.has("errors") ? response.getString("errors") : "" ;


                    if (status){

                        if (options_linear!=null)
                            options_linear.removeAllViews();

                        if (specify_other_linear!=null)
                            specify_other_linear.setVisibility(View.GONE);

                        if (followup_linear!=null)
                            followup_linear.setVisibility(View.GONE);

                        if (open_ended_linear!=null)
                            open_ended_linear.setVisibility(View.GONE);

                        if(btn_submit!=null)
                            btn_submit.setVisibility(View.VISIBLE);

                        JSONArray myArray = response.getJSONArray("data");

                        if (myArray.length() == 0){
                            no_survey.setVisibility(View.VISIBLE);
                            questionTV.setVisibility(View.GONE);
                            followup_linear.setVisibility(View.GONE);
                            open_ended_linear.setVisibility(View.GONE);
                            specify_other_linear.setVisibility(View.GONE);
                            btn_submit.setVisibility(View.GONE);
                        }else {
                            JSONObject item = (JSONObject) myArray.get(0);

                            int  id = item.has("id") ? item.getInt("id") : 0;
                            String  question = item.has("question") ? item.getString("question") : "";
                            String  answer_count = item.has("answer_count") ? item.getString("answer_count") : "";
                            String  type = item.has("type") ? item.getString("type") : "";

                            JSONArray  answers = item.has("answers") && !item.isNull("answers") ? item.getJSONArray("answers") : null;

                            List<Answer> answerList = new ArrayList<>();

                            if (answers!=null){
                                for (int i = 0; i < answers.length(); i++) {

                                    Answer answerObject;

                                    JSONObject ans = (JSONObject) answers.get(i);

                                    int  answer_id = ans.has("id") ? ans.getInt("id") : 0;
                                    int  question_id = ans.has("question_id") ? ans.getInt("question_id") : 0;
                                    String  answer = ans.has("answer") ? ans.getString("answer") : "";

                                    JSONObject followupObj = ans.has("followup") && !ans.isNull("followup") ? ans.getJSONObject("followup") : null;

                                    if (followupObj !=null){

                                        JSONObject flp = (JSONObject) ans.getJSONObject("followup");

                                        int  followup_id = flp.has("id") ? flp.getInt("id") : 0;
                                        int  followup_answer_id = flp.has("answer_id") ? flp.getInt("answer_id") : 0;
                                        String  followup_question = flp.has("question") ? flp.getString("question") : "";

                                        Answer.Followup followUp = new Answer.Followup(followup_id,followup_answer_id,followup_question);

                                        answerObject = new Answer(answer_id,question_id,answer,followUp);

                                    }else {
                                        answerObject = new Answer(answer_id,question_id,answer);
                                    }

                                    answerList.add(answerObject);


                                }

                                questionObject = new Question(id,question,type,answer_count,answerList);

                            }else {
                                questionObject = new Question(id,question,type,answer_count);
                            }

                            //Log.e("answerslist::",answerList.size()+" answers");

                            questionTV.setText(questionObject.getQuestion());

                            if (questionObject.getType().equals("CHOICE")){

                                //Log.e("question type::",questionObject.getType());

                                open_ended_linear.setVisibility(View.GONE);

                                //checkboxes and radio buttons
                                if (questionObject.getAnswerList() != null){

                                    List<Answer> answerList1 = questionObject.getAnswerList(); //or replace your list here

                                    if (questionObject.getAnswer_count().equals("SINGLE")){
                                        RadioGroup radioGroup = new RadioGroup(context);

                                        for (int j = 0; j < answerList1.size(); j++){

                                            Answer ans = answerList1.get(j);

                                            RadioButton radioButton = new RadioButton(context);
                                            radioButton.setText(ans.getAnswer());
                                            radioGroup.addView(radioButton);

                                            //Log.e("added answer RB for ", ans.getAnswer());

                                            radioButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (radioButton.isChecked()){
                                                        selectedSingleAnswerId = ans.getId();
                                                        selectedAnswer = ans.getAnswer();
                                                        //Toast.makeText(context,selectedSingleAnswerId+" is answer id for "+ ans.getAnswer(), Toast.LENGTH_SHORT).show();

                                                        if (ans.getAnswer().equals("Other (specify)") || ans.getAnswer().equals("Yes (specify)")){
                                                            specify_other_linear.setVisibility(View.VISIBLE);
                                                        }else {
                                                            specify_other_linear.setVisibility(View.GONE);
                                                        }

                                                        //check if needs follow up and add
                                                        if (ans.getFollowup() == null){
                                                            hasFollowUp = false;
                                                            followup_linear.setVisibility(View.GONE);
                                                        }else {
                                                            hasFollowUp = true;
                                                            followup_linear.setVisibility(View.VISIBLE);
                                                            followup_answerET.setText("");
                                                            followUpQuestionTV.setText(ans.getFollowup().getFollowup_question());
                                                        }

                                                    }
                                                }
                                            });
                                        }


                                        if (options_linear!=null)
                                            options_linear.addView(radioGroup);

                                    }else if (questionObject.getAnswer_count().equals("MULTIPLE")){
                                        for (int k = 0; k < answerList1.size(); k++){
                                            // Create Checkbox Dynamically
                                            Answer ans = answerList1.get(k);

                                            CheckBox checkBox = new CheckBox(context);
                                            checkBox.setText(ans.getAnswer());
                                            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    String msg = "You have " + (isChecked ? "checked" : "unchecked") + ans.getAnswer();
                                                    //add or remove to array
                                                    //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                                                    if (isChecked)
                                                        multipleAnswerIds.add(ans.getId());
                                                    else
                                                        multipleAnswerIds.remove(new Integer(ans.getId()));

                                                    //check if needs follow up and add
//                                                    if (ans.getFollowup() == null){
//                                                        followup_linear.setVisibility(View.GONE);
//                                                    }else {
//                                                        followup_linear.setVisibility(View.VISIBLE);
//                                                        followUpQuestionTV.setText(ans.getFollowup().getFollowup_question());
//                                                    }
                                                }
                                            });

                                            if (options_linear != null)
                                                options_linear.addView(checkBox);

                                        }
                                    }
                                }
                                //end of checkbox and radio button

                            }else if (questionObject.getType().equals("OPEN")){
                                //Log.e("question type::",questionObject.getType());

                                open_ended_linear.setVisibility(View.VISIBLE);

                            }

                            //Log.e("question type::",questionObject.getType());

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

    private void postAnswer(JSONObject payload) {

        showProgressDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Stash.getString(Constants.END_POINT)+ Constants.POST_ANSWER, payload, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                try {
                    boolean success = response.has("success") && response.getBoolean("success");
                    String message = response.has("message") ? response.getString("message") : "";
                    String errors = response.has("errors") ? response.getString("errors") : "";

                    if (success) {
                        Dialogs.showOkDialog(context,"Success",message);
                        getQuestion();
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