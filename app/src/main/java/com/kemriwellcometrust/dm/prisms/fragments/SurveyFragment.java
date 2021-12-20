package com.kemriwellcometrust.dm.prisms.fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.kemriwellcometrust.dm.prisms.R;
import com.kemriwellcometrust.dm.prisms.dependencies.Constants;
import com.kemriwellcometrust.dm.prisms.dependencies.Dialogs;
import com.kemriwellcometrust.dm.prisms.dependencies.PrismsApplication;
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

    private int selectedSingleAnswerId = 0;


    @BindView(R.id.questionTV)
    TextView questionTV;

    @BindView(R.id.followUpQuestionTV)
    TextView followUpQuestionTV;

    @BindView(R.id.followup_answerET)
    EditText followup_answerET;

    @BindView(R.id.open_ended_answerET)
    EditText open_ended_answerET;

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



        getQuestion();


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

                        btn_submit.setVisibility(View.VISIBLE);

                        JSONArray myArray = response.getJSONArray("data");

                        if (myArray.length() == 0){
                            no_survey.setVisibility(View.VISIBLE);
                        }else {
                            JSONObject item = (JSONObject) myArray.get(0);

                            int  id = item.has("id") ? item.getInt("id") : 0;
                            String  question = item.has("question") ? item.getString("question") : "";
                            String  answer_count = item.has("answer_count") ? item.getString("answer_count") : "";
                            String  type = item.has("type") ? item.getString("type") : "";

                            JSONArray  answers = item.has("answers") && !item.isNull("answers") ? item.getJSONArray("answers") : null;

                            Question questionObject;
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
                                                        //Toast.makeText(context,selectedSingleAnswerId+" is answer id for "+ ans.getAnswer(), Toast.LENGTH_SHORT).show();

                                                        if (ans.getAnswer().equals("Other (specify)")){
                                                            specify_other_linear.setVisibility(View.VISIBLE);
                                                        }else {
                                                            specify_other_linear.setVisibility(View.GONE);
                                                        }

                                                        //check if needs follow up and add
                                                        if (ans.getFollowup() == null){
                                                            followup_linear.setVisibility(View.GONE);
                                                        }else {
                                                            followup_linear.setVisibility(View.VISIBLE);
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

                                                    //check if needs follow up and add
                                                    if (ans.getFollowup() == null){
                                                        followup_linear.setVisibility(View.GONE);
                                                    }else {
                                                        followup_linear.setVisibility(View.VISIBLE);
                                                        followUpQuestionTV.setText(ans.getFollowup().getFollowup_question());
                                                    }
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


}