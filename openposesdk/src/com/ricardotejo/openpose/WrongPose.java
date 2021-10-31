package com.ricardotejo.openpose;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WrongPose {

    private int neckx = 0;
    private int necky = 0;
    private int earRx = 0;
    private int earRy = 0;
    private int earLx = 0;
    private int earLy = 0;
    private int hipRx = 0;
    private int hipRy = 0;
    private int hipLx = 0;
    private int hipLy = 0;
    private int nosex = 0;
    private int nosey = 0;
    int i = 6;
    int j = 6;
    private String startTime;
    private String finishTime;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmmss");
    SimpleDateFormat mFormatTime = new SimpleDateFormat("hhmmss");
    private ArrayList<String> wrongNeckTimes = new ArrayList();
    private ArrayList<String> wrongWaistTimes = new ArrayList();

    WrongPose(){
        setStartTime();
    }

    public int getBornWrong(int index){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        if(index == 12) {
            if(earRx == 0 && earLx == 0){
                if(Math.abs(nosex - neckx) > 20) {
                    if(i == 6){
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                        Log.d("worgpose_neck", "start");
                    }
                    i = 0; //잘못된 자세

                }else{
                    if(i == 0){
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                        Log.d("worgpose_neck", "finish");
                    }
                    i = 6;
                }
            }
            else if(earRx==0 || earLx==0){
                if(earRx < earLx)
                    earRx = earLx;
                else
                    earLx = earRx;
                if(Math.abs((earRx + earLx)/2 - neckx) > 20) {
                    if(i == 6){
                        Log.d("worgpose_neck", "start");
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                    }
                    i = 0; //잘못된 자세

                } else {
                    if(i == 0){
                        Log.d("worgpose_neck", "finish");
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                    }
                    i = 6;
                }
            }
            else{
                if(Math.abs((earRx + earLx)/2 - neckx) > 20) {
                    if(i == 6){
                        Log.d("worgpose_neck", "start");
                        wrongNeckTimes.add(mFormatTime.format(mDate));
                    }
                    i = 0; //잘못된 자세

                } else {
                    if(i == 0){
                        Log.d("worgpose_neck", "finish");
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
            if(Math.abs((hipRx + hipLx)/2 - neckx) > 20){
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

    public void setStartTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        startTime = mFormat.format(mDate);
    }
    public void setFinishTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        finishTime = mFormat.format(mDate);
    }
    public void wrongTime(ArrayList<String> timeStamp){
        //Date d1 =
        for(int i = 0; i < timeStamp.size(); i++){
            if(i % 2 == 0){

            }
        }
    }
    public String getAll(){
        return "시작시간 : " + startTime + "\n끝난시간 : " + finishTime + "\ndd : " + wrongNeckTimes.size();
    }


}
