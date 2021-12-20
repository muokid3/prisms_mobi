package com.kemriwellcometrust.dm.prisms.models;

import java.util.List;

public class Answer {
    private int id;
    private int question_id;
    private String answer;
    private Followup followup;

    public Answer(int id, int question_id, String answer, Followup followup) {
        this.id = id;
        this.question_id = question_id;
        this.answer = answer;
        this.followup = followup;
    }

    public Answer(int id, int question_id, String answer) {
        this.id = id;
        this.question_id = question_id;
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Followup getFollowup() {
        return followup;
    }

    public void setFollowup(Followup followup) {
        this.followup = followup;
    }

    public static  class Followup {
        private int followup_id;
        private int answer_id;
        private  String followup_question;

        public Followup(int followup_id, int answer_id, String followup_question) {
            this.followup_id = followup_id;
            this.answer_id = answer_id;
            this.followup_question = followup_question;
        }

        public int getFollowup_id() {
            return followup_id;
        }

        public void setFollowup_id(int followup_id) {
            this.followup_id = followup_id;
        }

        public int getAnswer_id() {
            return answer_id;
        }

        public void setAnswer_id(int answer_id) {
            this.answer_id = answer_id;
        }

        public String getFollowup_question() {
            return followup_question;
        }

        public void setFollowup_question(String followup_question) {
            this.followup_question = followup_question;
        }
    }
}