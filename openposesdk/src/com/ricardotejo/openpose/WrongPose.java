package com.ricardotejo.openpose;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WrongPose {

    private transient int neckx = 0;
    private transient int necky = 0;
    private transient int earRx = 0;
    private transient int earRy = 0;
    private transient int earLx = 0;
    private transient int earLy = 0;
    private transient int hipRx = 0;
    private transient int hipRy = 0;
    private transient int hipLx = 0;
    private transient int hipLy = 0;
    private transient int nosex = 0;
    private transient int nosey = 0;
    transient int i = 6;
    transient int j = 6;
    private Date startTime;
    private Date finishTime;
    transient long mNow;
    transient Date mDate;
    transient SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    transient SimpleDateFormat mFormatTime = new SimpleDateFormat("hhmmss");
    private ArrayList<String> wrongNeckTimes = new ArrayList();
    private ArrayList<String> wrongWaistTimes = new ArrayList();
    private String allWrongNeckTimes = "00:00:00";
    private String allWrongWaistTimes = "00:00:00";
    private Integer wrongStandard;

    WrongPose(){
        setStartTimeNow();

    }
    public void setWrongStandard(Integer i) {
        this.wrongStandard = i;
    }

    public int getBornWrong(int index, int wrongStandard){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        this.wrongStandard = wrongStandard;

        if(index == 12) {
            if(earRx == 0 && earLx == 0){
                if(Math.abs(nosex - neckx) > wrongStandard) {
                    if(i == 6){
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                    }
                    i = 0; //잘못된 자세

                }else{
                    if(i == 0){
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                    }
                    i = 6;
                }
            }
            else if(earRx==0 || earLx==0){
                if(earRx < earLx)
                    earRx = earLx;
                else
                    earLx = earRx;
                if(Math.abs((earRx + earLx)/2 - neckx) > wrongStandard) {
                    if(i == 6){
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                    }
                    i = 0; //잘못된 자세

                } else {
                    if(i == 0){
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                    }
                    i = 6;
                }
            }
            else{
                if(Math.abs((earRx + earLx)/2 - neckx) > wrongStandard) {
                    if(i == 6){
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                    }
                    i = 0; //잘못된 자세

                } else {
                    if(i == 0){
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                    }
                    i = 6;
                }
            }
            return i;
        }
        else if(index == 6 || index==9) {
            if(hipRx==0 || hipLx==0){
                if(hipRx < hipLx)
                    hipRx = hipLx;
                else
                    hipLx = hipRx;
            }
            if(Math.abs((hipRx + hipLx)/2 - neckx) > wrongStandard){
                if(j == 6){
                    wrongWaistTimes.add(mFormatTime.format(mDate));
                }
                j = 0; //잘못된 자세
            } else{
                if(j==0){
                    wrongWaistTimes.add(mFormatTime.format(mDate));
                }
                j = 6;
            }
            return j;
        }
        else return 6;
    }

    public void setBorn(int index, int x, int y) {
        //목 좌표값
        if(index == 1){
            neckx = x;
            necky = y;
        }
        //왼쪽 귀
        if(index == 17){
            earLx = x;
            earLy = y;
        }
        //오른쪽 귀
        if(index == 16){
            earRx = x;
            earRy = y;
        }
        //왼쪽 엉덩이
        if(index == 11){
            hipLx = x;
            hipLy = y;
        }
        //오른쪽 엉덩이
        if(index == 8){
            hipRx = x;
            hipRy = y;
        }
    }

    public void poseReset(){
        neckx = 0;
        necky = 0;
        earRx = 0;
        earRy = 0;
        earLx = 0;
        earLy = 0;
        hipRx = 0;
        hipRy = 0;
        hipLx = 0;
        hipLy = 0;
    }

    public void setStartTimeNow() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        startTime = mDate;
    }

    public void setFinishTimeNow() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        finishTime = mDate;
    }

    public void calAllWrongNeckTimes() throws ParseException {
        allWrongNeckTimes = difference(wrongTime(wrongNeckTimes));
    }

    public void calAllWrongWaistTimes() throws ParseException {
        allWrongWaistTimes = difference(wrongTime(wrongWaistTimes));
    }

    public long wrongTime(ArrayList<String> timeStamp) throws ParseException {
        long startSum = 0;
        long finishSum = 0;
        Date f1;
        Date f2;

        if(wrongNeckTimes.size()%2 == 1){
            mNow = System.currentTimeMillis();
            mDate = new Date(mNow);
            wrongNeckTimes.add(mFormatTime.format(mDate));
        }
        if(wrongWaistTimes.size()%2 == 1){
            mNow = System.currentTimeMillis();
            mDate = new Date(mNow);
            wrongWaistTimes.add(mFormatTime.format(mDate));
        }

        for (int i = 0; i < timeStamp.size(); i++) {
            if (i % 2 == 0) {
                f1 = new SimpleDateFormat("hhmmss").parse(timeStamp.get(i));
                startSum = startSum + f1.getTime();
            } else {
                f2 = new SimpleDateFormat("hhmmss").parse(timeStamp.get(i));
                finishSum = finishSum + f2.getTime();
            }
        }


        return finishSum - startSum;
    }

    private String difference(long time) {
        long _time = time;

        //milliseconds
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = _time / daysInMilli;
        _time = _time % daysInMilli;

        long elapsedHours = _time / hoursInMilli;
        _time = _time % hoursInMilli;

        long elapsedMinutes = _time / minutesInMilli;
        _time = _time % minutesInMilli;

        long elapsedSeconds = _time / secondsInMilli;

        return String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    public String getAll() {
        return "시작시간 : " + mFormat.format(startTime) + "\n끝난시간 : " + mFormat.format(finishTime) + "\n목 : " + getAllWrongNeckTimes() + "\n허리 : " + getAllWrongWaistTimes();
    }
    public String getNeck(){
        return getAllWrongNeckTimes();
    }
    public String getWaist()throws ParseException{
        return difference(wrongTime(wrongWaistTimes));
    }

    public String getAllString() {
        return mFormat.format(startTime) + "\n" + mFormat.format(finishTime) + "\n" + getAllWrongNeckTimes() + "\n" + getAllWrongWaistTimes();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public ArrayList<String> getWrongNeckTimes() {
        return wrongNeckTimes;
    }

    public void setWrongNeckTimes(ArrayList<String> wrongNeckTimes) {
        this.wrongNeckTimes = wrongNeckTimes;
    }

    public ArrayList<String> getWrongWaistTimes() {
        return wrongWaistTimes;
    }

    public void setWrongWaistTimes(ArrayList<String> wrongWaistTimes) {
        this.wrongWaistTimes = wrongWaistTimes;
    }

    public String getAllWrongNeckTimes() {
        return allWrongNeckTimes;
    }

    public void setAllWrongNeckTimes(String allWrongNeckTimes) {
        this.allWrongNeckTimes = allWrongNeckTimes;
    }

    public String getAllWrongWaistTimes() {
        return allWrongWaistTimes;
    }

    public void setAllWrongWaistTimes(String allWrongWaistTimes) {
        this.allWrongWaistTimes = allWrongWaistTimes;
    }

}
