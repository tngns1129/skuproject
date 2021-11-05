package com.familyset.myapplication.model.blink;

import android.util.Log;

import org.opencv.core.Rect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PersonalInfo {
    private transient double eyeAreaAvg_30 = 0;
    private transient double firstEyeArea;
    private double eyeDistanceAvg = 0;
    private transient String firstSetting;
    private transient String secondSetting;
    private Date startTime;
    private Date finishTime;
    private transient ArrayList<Double> rightEyeAreaList = new ArrayList();
    private transient ArrayList<Double> leftEyeAreaList = new ArrayList();
    private transient ArrayList<Double> eyeDistance = new ArrayList();
    private ArrayList<Double> eyeDistanceCm = new ArrayList();
    private int blink = 0;
    private transient ArrayList<Double> listRx = new ArrayList();
    private transient ArrayList<Double> listRy = new ArrayList();
    private transient ArrayList<Double> listLx = new ArrayList();
    private transient ArrayList<Double> listLy = new ArrayList();
    private transient SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public PersonalInfo(Date time, String firstSet, String secondSet){
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

    public String getAllEyeDistanceAvg() {
        double sum = 0;
        double result = 0;
        int zerocount = 0;
        for(int i = 0; i < eyeDistance.size(); i++){
            if(eyeDistance.get(i) != 0) {
                sum = sum + eyeDistance.get(i);
            }else{zerocount++;}
        }
        if (eyeDistance.size() != 0) {
            result = sum/(eyeDistance.size()-zerocount);
        }
        return Double.toString(Math.round(result));
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
    //왼쪽눈과 오른쪽눈의 넓이 차이가 3000이상 나면
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
        if(Math.abs(R-L) > 3000) {
            if(R > L){
                result = R;
            }else{
                result = L;
            }
        }
        else{
            result = (R + L);
        }
        if(result != 0) {
            result = areaToCm(result, eyeAreaAvg_30);
            eyeDistance.add(result);
            result = getDistance(eyeDistance);
            eyeDistanceCm.add(result);
        }
        return Math.round(result);
    }

    public int getBlinkNumber(){
        return blink;
    }
    public void setBlinkNumber(int a){
        blink = a;
    }

    public double areaToCm(double now, double cm_30){
        double result;
        double i;
        i = now - cm_30;
        i = i/2500;
        result = 30 - i;
        return result;
    }

    public double getDistance(ArrayList<Double> eyeDistance) {
        double result;
        double max;
        double min;
        double sum = 0;
        max = eyeDistance.get(eyeDistance.size() - 1);
        min = eyeDistance.get(eyeDistance.size() - 1);
        if(eyeDistance.size() > 5){
            for(int i = eyeDistance.size() - 5; i < eyeDistance.size(); i++){
                if(max < eyeDistance.get(i)){
                    max = eyeDistance.get(i);
                }
                if(min > eyeDistance.get(i)){
                    min = eyeDistance.get(i);
                }
            }
            for(int i = eyeDistance.size() - 5; i < eyeDistance.size(); i++){
                sum = sum + eyeDistance.get(i);
            }
            sum = sum - max - min;
            result = sum/3;
        } else {
            result = 0;
        }
        return result;
    }

    //눈 깜빡임 감지 눈 전체 범위와 눈동자 범위가
    //전의 5번 평균보다 크게 어긋날 시
    //눈이 깜빡였다고 판단
    public boolean blinkEyeCheck(Rect eyearea_right, Rect eye_right, Rect eyearea_left, Rect eye_left){
        boolean plusBlink = false;
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
                    plusBlink = true;
                }
            }
        }
        return plusBlink;
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

    public void finishObserve(Date time){
        finishTime = time;
    }
    public String getAll(){
        return "시작시간 : " + mFormat.format(startTime) + "\n끝난시간 : " + mFormat.format(finishTime) + "\n거리 평균 : " + getAllEyeDistanceAvg() + "\n깜박임 횟수 : " + getBlinkNumber();
    }

    public double getEyeAreaAvg_30() {
        return eyeAreaAvg_30;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setEyeDistanceAvg(Double eyeDistanceAvg) {
        this.eyeDistanceAvg = eyeDistanceAvg;
    }

    public Double getEyeDistanceAvg() {
        return eyeDistanceAvg;
    }

    public String executeTime() {
        return difference(startTime, finishTime);
    }

    private String difference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
    }
}
