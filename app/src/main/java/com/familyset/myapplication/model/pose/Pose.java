package com.familyset.myapplication.model.pose;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Pose {
    private Date startTime;
    private Date finishTime;
    private transient ArrayList<String> wrongNeckTimes = new ArrayList();
    private transient ArrayList<String> wrongWaistTimes = new ArrayList();
    private String allWrongNeckTimes = "00:00:00";
    private String allWrongWaistTimes = "00:00:00";
    private transient SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public Pose() {}

    public Pose(String poseString) {
        String[] s = poseString.split("\n");
        try {
            startTime = mFormat.parse(s[0]);
            finishTime = mFormat.parse(s[1]);
        } catch (ParseException e) {
            e.printStackTrace();

            startTime = new Date();
            finishTime = new Date();
        }
        allWrongNeckTimes = s[2];
        allWrongWaistTimes = s[3];
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

    public String getAll() {
        return "시작시간 : " + startTime + "\n끝난시간 : " + finishTime + "\n목 : " + getAllWrongNeckTimes() + "\n허리 : " + getAllWrongWaistTimes();
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

    public String getDifference() {
        return difference(startTime, finishTime);
    }
}
