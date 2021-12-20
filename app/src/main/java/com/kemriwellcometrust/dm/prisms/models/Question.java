package com.kemriwellcometrust.dm.prisms.models;

import java.util.List;

public class Question {
    private int id;
    private String question;
    private String type;
    private String answer_count;
    private List<Answer> answerList;

    public Question(int id, String question, String type, String answer_count, List<Answer> answerList) {
        this.id = id;
        this.question = question;
        this.type = type;
        this.answer_count = answer_count;
        this.answerList = answerList;
    }

    public Question(int id, String question, String type, String answer_count) {
        this.id = id;
        this.question = question;
        this.type = type;
        this.answer_count = answer_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnswer_count() {
        return answer_count;
    }

    public void setAnswer_count(String answer_count) {
        this.answer_count = answer_count;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }
}
