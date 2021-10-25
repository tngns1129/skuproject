package com.familyset.myapplication.model.blink;

import org.opencv.core.Rect;

import java.util.ArrayList;

public class PersonalInfo {
    private double eyeAreaAvg_30 = 0;
    private double firstEyeArea;
    private double allEyeArea;
    private String firstSetting;
    private String secondSetting;
    private String startTime;
    private String finishTime;
    private ArrayList<Double> rightEyeAreaList = new ArrayList();
    private ArrayList<Double> leftEyeAreaList = new ArrayList();
    private int blink = 0;
    private ArrayList<Double> listRx = new ArrayList();
    private ArrayList<Double> listRy = new ArrayList();
    private ArrayList<Double> listLx = new ArrayList();
    private ArrayList<Double> listLy = new ArrayList();

    public PersonalInfo(String time, String firstSet, String secondSet){
        startTime = time;
        firstSetting = firstSet;
        secondSetting = secondSet;
    }

    public void areaListAdd(double R, double L){
        rightEyeAreaList.add(R);
        leftEyeAreaList.add(L);
    }

    public String getFiveEyeAreaAvg(){
        double sumR = 0;
        double sumL = 0;
        double result = 0;
        if(rightEyeAreaList.size() < 5 && rightEyeAreaList.size() > 0) {
            for (int i = 0; i < rightEyeAreaList.size(); i++) {
                sumR = sumR + rightEyeAreaList.get(i);
            }
            for (int i = 0; i < leftEyeAreaList.size(); i++) {
                sumL = sumL + leftEyeAreaList.get(i);
            }
            result = sumR/rightEyeAreaList.size() + sumL/leftEyeAreaList.size();
        }else {
            for (int i = rightEyeAreaList.size() - 6; i < rightEyeAreaList.size()-1; i++) {
                sumR = sumR + rightEyeAreaList.get(i);
            }
            for (int i = leftEyeAreaList.size() - 6; i < leftEyeAreaList.size()-1; i++) {
                sumL = sumL + leftEyeAreaList.get(i);
            }
            result = sumR/5 + sumL/5;
        }
        return Double.toString(result);
    }

    public void setEyeAreaAvg_30(){
        eyeAreaAvg_30 = Double.parseDouble(getFiveEyeAreaAvg());
    }

    public String getAllEyeAreaAvg(){
        double sumR = 0;
        double sumL = 0;
        double result = 0;
        for (int i = 0; i < rightEyeAreaList.size(); i++) {
            sumR = sumR + rightEyeAreaList.get(i);
        }
        for (int i = 0; i < leftEyeAreaList.size(); i++) {
            sumL = sumL + leftEyeAreaList.get(i);
        }
        if (rightEyeAreaList.size() != 0 && leftEyeAreaList.size() != 0) {
            result = sumR/rightEyeAreaList.size() + sumL/leftEyeAreaList.size();
        }
        return Double.toString(result);
    }

    //눈범위의 넓이를 이용해 거리를 측정
    //왼쪽눈과 오른쪽눈의 넓이 차이가 5000이상 나면
    //감지 이상을 판단하여 큰쪽 눈으로만 측정한다.
    public double getArea(){
        double result;
        double R;
        double L;
        if(!rightEyeAreaList.isEmpty()) {
            R = rightEyeAreaList.get(rightEyeAreaList.size() - 1);
            L = leftEyeAreaList.get(leftEyeAreaList.size() - 1);
        }else{
            R = 0;
            L = 0;
        }
        if(Math.abs(R-L) > 5000) {
            if(R > L){
                result = R;
            }else{
                result = L;
            }
        }
        else{
            result = (R + L);
        }
        return result;
    }

    public int getBlinkNumber(){
        return blink;
    }
    public void setBlinkNumber(int a){
        blink = a;
    }

    public void areaToCm(double a, double cm_30){
        a = 200000 - a;
    }

    //눈 깜빡임 감지 눈 전체 범위와 눈동자 범위가
    //전의 5번 평균보다 크게 어긋날 시
    //눈이 깜빡였다고 판단
    public void blinkEyeCheck(Rect eyearea_right, Rect eye_right, Rect eyearea_left, Rect eye_left){

        double rx = Math.abs(eyearea_right.tl().x - eye_right.tl().x);
        double ry = Math.abs(eyearea_right.tl().y - eye_right.tl().y);
        double lx = Math.abs(eyearea_left.tl().x - eye_left.tl().x);
        double ly = Math.abs(eyearea_left.tl().y - eye_left.tl().y);
        if(listRx.size()<6) {
            listRx.add(rx);
        }else {
            listRx.remove(0);
            listRx.add(rx);
        }
        if(listRy.size()<6) {
            listRy.add(ry);
        }else{
            listRy.remove(0);
            listRy.add(ry);
        }

        if(listLx.size()<6) {
            listLx.add(lx);
        }else{
            listLx.remove(0);
            listLx.add(lx);
        }

        if(listLy.size()<6) {
            listLy.add(ly);
        }else{
            listLy.remove(0);
            listLy.add(ly);
        }

        if(listLy.size()>5){
            if(Math.abs(rx - getAverage(listRx)) > 20 || Math.abs(ry - getAverage(listRy)) > 20){
                if(Math.abs(lx - getAverage(listLx)) > 20 || Math.abs(ly - getAverage(listLy)) > 20){
                    blink++;
                }
            }
        }
    }

    //array list 의 평균을 마지막 수 빼고 구함
    private double getAverage(ArrayList<Double> list){
        double sum = 0;
        double result = 0;
        for(int i = 0; i<list.size()-1; i++){
            sum = sum + list.get(i);
        }
        result = sum/list.size();
        return result;
    }

    public void finishObserve(String time){
        finishTime = time;
    }
    public String getAll(){
        return "시작시간 : " + startTime + "\n끝난시간 : " + finishTime + "\n거리 평균 : " + getAllEyeAreaAvg() + "\n깜박임 횟수 : " + getBlinkNumber();
    }

    public double getEyeAreaAvg_30() {
        return eyeAreaAvg_30;
    }

}
