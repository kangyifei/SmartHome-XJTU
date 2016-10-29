package com.kyf.futurespace.smarthome.utils;

import java.util.ArrayList;

/**
 * Created by kyf on 2016/8/2 0002.
 */
public class Task {
    private String condition="";
    private ArrayList<String> order=new ArrayList<>();
    private ArrayList<String> answer=new ArrayList<>();

    public Task() {
    }

    public Task(String condition, ArrayList<String> order, ArrayList<String> answer) {
        this.condition = condition;
        this.order = order;
        this.answer = answer;
    }

    public void clear(){
        if(!"".equals(condition)){
        this.condition="";}
        if(null!=order){
        this.order.clear();}
        if(null!=answer){
        this.answer.clear();}
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public ArrayList<String> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<String> order) {
        this.order = order;
    }

    public ArrayList<String> getAnswer() {
        return answer;
    }

    public void setAnswer(ArrayList<String> answer) {
        this.answer = answer;
    }

}
