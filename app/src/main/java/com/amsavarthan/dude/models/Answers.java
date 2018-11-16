package com.amsavarthan.dude.models;

public class Answers extends Answers_doc_id{

    private String name,timestamp,answer,user_id,is_answer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getIs_answer() {
        return is_answer;
    }

    public void setIs_answer(String is_answer) {
        this.is_answer = is_answer;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
