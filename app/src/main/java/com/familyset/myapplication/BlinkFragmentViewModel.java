package com.familyset.myapplication;

import static org.opencv.imgproc.Imgproc.TM_CCOEFF;
import static org.opencv.imgproc.Imgproc.TM_CCOEFF_NORMED;
import static org.opencv.imgproc.Imgproc.TM_CCORR;
import static org.opencv.imgproc.Imgproc.TM_CCORR_NORMED;
import static org.opencv.imgproc.Imgproc.TM_SQDIFF;
import static org.opencv.imgproc.Imgproc.TM_SQDIFF_NORMED;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BlinkFragmentViewModel {

    private View view;
    private Button btnOnOff;
    private Button templeReset;
    private TextView firstSet;
    private TextView secondSet;
    private String fSet;
    private String sSet;
    private Spinner firstSpinner;
    private Spinner secondSpinner;
    private TextView eyeInfo;
    private CameraBridgeViewBase mOpenCvCameraView;
    private FragmentActivity fragmentActivity;
    private Mat mRgba;
    private Mat mGray;
    private static final String TAG = "opencv";
    private CascadeClassifier mJavaDetector;
    private CascadeClassifier mJavaDetectorEye;
    private File mCascadeFile;
    private File mCascadeFileEye;
    private int mAbsoluteeyeSize = 0;
    private float mRelativeeyeSize = 0.001f;
    private int learn_frames = 0;
    private Mat teplateR;
    private Mat teplateL;
    int method = 5;
    private double eye1area = 0;
    private double eye2area = 0;
    private double areaAvgR = 0;
    private double areaAvgL = 0;
    ArrayList<Double> areaR = new ArrayList();
    ArrayList<Double> areaL = new ArrayList();
    Rect eye_right = new Rect();
    Rect eye_left = new Rect();
    Handler handler;
    InfoSendThread infoSendThread;
    PersonalInfo personalInfo;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmmss");

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    BlinkFragmentViewModel(View view, FragmentActivity fragmentActivity){
        this.view = view;
        this.fragmentActivity = fragmentActivity;
        initView();
    }

    private void initView() {
        firstSet = view.findViewById(R.id.firstset);
        secondSet = view.findViewById(R.id.secondset);
        btnOnOff = view.findViewById(R.id.onoff);
        btnOnOff.setBackgroundColor(Color.GRAY);
        templeReset = view.findViewById(R.id.templereset);
        eyeInfo = view.findViewById(R.id.info);
        firstSpinner = view.findViewById(R.id.firstspiner);
        secondSpinner = view.findViewById(R.id.secondspiner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(fragmentActivity, R.array.blinkfragmentfirstspinner, R.layout.support_simple_spinner_dropdown_item);
        firstSpinner.setAdapter(adapter);
        secondSpinner.setAdapter(adapter);
        handler = new Handler();
        mOpenCvCameraView = (CameraBridgeViewBase)view.findViewById(R.id.activity_surface_view);
        btnOnOff.setText("OFF");
        templeReset.setVisibility(View.GONE);

        firstSet.setText("첫번째 설정");
        secondSet.setText("두번째 설정");

        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlinkOnOff();
            }
        });
        templeReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                learn_frames = 0;
            }
        });

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, fragmentActivity, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    // 눈 감지 켜고 끄기
    @SuppressLint("ResourceAsColor")
    public void BlinkOnOff(){
        fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setCameraIndex(1);
        mOpenCvCameraView.enableView();
        fSet = firstSpinner.getSelectedItem().toString();
        sSet = secondSpinner.getSelectedItem().toString();
        if(mOpenCvCameraView.getVisibility() == View.GONE) {
            mNow = System.currentTimeMillis();
            mDate = new Date(mNow);
            personalInfo = new PersonalInfo(mFormat.format(mDate),fSet,sSet);
            infoSendThread = new InfoSendThread();
            infoSendThread.start();
            firstSpinner.setVisibility(View.GONE);
            secondSpinner.setVisibility(View.GONE);
            btnOnOff.setText("ON");
            btnOnOff.setBackgroundColor(Color.RED);
            templeReset.setVisibility(View.VISIBLE);
            firstSet.setVisibility(View.GONE);
            secondSet.setVisibility(View.GONE);
            eyeInfo.setVisibility(View.VISIBLE);
            mOpenCvCameraView.setVisibility(View.VISIBLE);
            mOpenCvCameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
                @Override
                public void onCameraViewStarted(int width, int height) {

                }

                @Override
                public void onCameraViewStopped() {

                }

                @Override
                public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                    //초기 세팅
                    mRgba = inputFrame.rgba();
                    mGray = inputFrame.gray();
                    Mat rotImage = Imgproc.getRotationMatrix2D(new Point(mRgba.cols() / 2,
                            mRgba.rows() / 2), 90, 1.0);
                    Imgproc.warpAffine(mRgba, mRgba, rotImage, mRgba.size());
                    Imgproc.warpAffine(mGray, mGray, rotImage, mRgba.size());

                    //눈 그릴 크기 mRelativeeyeSize = 0.05
                    if (mAbsoluteeyeSize == 0) {
                        int height = mGray.rows();
                        if (Math.round(height * mRelativeeyeSize) > 0) {
                            mAbsoluteeyeSize = Math.round(height * mRelativeeyeSize);
                        }
                    }

                    //눈 감지
                    MatOfRect eyes = new MatOfRect();
                    if (mJavaDetector != null)
                        mJavaDetector.detectMultiScale(mGray, eyes, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                                new Size(mAbsoluteeyeSize, mAbsoluteeyeSize), new Size());

                    else {
                        Log.e(TAG, "Detection method is not selected!");
                    }

                    //감지한 눈 개수만큼 그리기
                    Rect[] eyesArray = eyes.toArray();
                    for (int i = 0; i < eyesArray.length; i++) {
                        Imgproc.rectangle(mRgba, eyesArray[i].tl(), eyesArray[i].br(),
                                new Scalar(0, 255, 0, 255), 3);
                    }

                    Rect eyearea_right;
                    Rect eyearea_left;
                    if (eyesArray.length == 1) {
                        personalInfo.areaListAdd(eyesArray[0].area(), eyesArray[0].area());
                        if (learn_frames > 4) {
                            //return null;
                        }

                    } else if (eyesArray.length > 1) {
                        personalInfo.areaListAdd(eyesArray[0].area(), eyesArray[1].area());
                        eyearea_right = eyesArray[0];
                        eyearea_left = eyesArray[1];

                        //5번 학습
                        if (learn_frames < 5) {
                            teplateR = get_template(mJavaDetectorEye, eyearea_right, 24);
                            teplateL = get_template(mJavaDetectorEye, eyearea_left, 24);
                            learn_frames++;
                        } else if(learn_frames == 5){
                            eye_right = match_eye(eyearea_right, teplateR, method);
                            eye_left = match_eye(eyearea_left, teplateL, method);
                            personalInfo.setEyeAreaAvg_30();
                            learn_frames++;
                        }
                        else {
                            // 템플릿 학습 완료시
                            eye_right = match_eye(eyearea_right, teplateR, method);
                            eye_left = match_eye(eyearea_left, teplateL, method);
                            if (Math.abs(eye1area - eye2area) < 5000)
                                personalInfo.blinkEyeCheck(eyearea_right, eye_right, eyearea_left, eye_left);
                            //return null;
                        }
                    } else {
                        if (learn_frames > 4) {
                            //return null;
                        }
                    }
                    return mRgba;
                }
            });
        } else if(mOpenCvCameraView.getVisibility() == View.VISIBLE){
            mNow = System.currentTimeMillis();
            mDate = new Date(mNow);
            personalInfo.finishObserve(mFormat.format(mDate));
            AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
            builder.setTitle("통계").setMessage(personalInfo.getAll());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            infoSendThread.interrupt();
            btnOnOff.setText("OFF");
            templeReset.setVisibility(View.GONE);
            eyeInfo.setVisibility(View.GONE);
            firstSet.setVisibility(View.VISIBLE);
            secondSet.setVisibility(View.VISIBLE);
            firstSpinner.setVisibility(View.VISIBLE);
            secondSpinner.setVisibility(View.VISIBLE);
            btnOnOff.setBackgroundColor(Color.GRAY);
            mOpenCvCameraView.disableView();
            mOpenCvCameraView.setVisibility(View.GONE);
        }
    }

    //학습된 템플릿을 이용해 눈을 감지한다
    private Rect match_eye(Rect area, Mat mTemplate, int type) {
        Point matchLoc;
        Mat mROI = mGray.submat(area);
        int result_cols;
        int result_rows;
        if(mROI.cols()>=mTemplate.cols()) {
            result_cols = mROI.cols() - mTemplate.cols() + 1;
        }else{
            result_cols=1;
        }
        if(mROI.rows()>=mTemplate.rows()) {
            result_rows = mROI.rows() - mTemplate.rows() + 1;
        }else{
            result_rows=1;
        }
        // Check for bad template size
        if (mTemplate.cols() == 0 || mTemplate.rows() == 0) {
            return new Rect();
        }
        Mat mResult;
        if(result_cols>0&&result_rows>0) {
            mResult = new Mat(result_cols, result_rows, CvType.CV_8U);
        }else{
            mResult = new Mat(0, 0, CvType.CV_8U);
        }

        // 템플릿 매칭 방법
        // 본 프로젝트는 TM_CCOEFF_NORMED 사용
        // 상관계수 방법으로 연상량이 제일 많지만 프로젝트 특성상 적확도가 있어야 함으로 사용한다.
        switch (type) {
            case TM_SQDIFF:
                Imgproc.matchTemplate(mROI, mTemplate, mResult, TM_SQDIFF);
                break;
            case TM_SQDIFF_NORMED:
                Imgproc.matchTemplate(mROI, mTemplate, mResult,
                        TM_SQDIFF_NORMED);
                break;
            case TM_CCOEFF:
                Imgproc.matchTemplate(mROI, mTemplate, mResult, TM_CCOEFF);
                break;
            case TM_CCOEFF_NORMED:
                Imgproc.matchTemplate(mROI, mTemplate, mResult,
                        TM_CCOEFF_NORMED);
                break;
            case TM_CCORR:
                Imgproc.matchTemplate(mROI, mTemplate, mResult, TM_CCORR);
                break;
            case TM_CCORR_NORMED:
                Imgproc.matchTemplate(mROI, mTemplate, mResult,
                        TM_CCORR_NORMED);
                break;
        }

        Core.MinMaxLocResult mmres = Core.minMaxLoc(mResult);
        // there is difference in matching methods - best match is max/min value
        if (type == TM_SQDIFF || type == TM_SQDIFF_NORMED) {
            matchLoc = mmres.minLoc;
        } else {
            matchLoc = mmres.maxLoc;
        }

        Point matchLoc_tx = new Point(matchLoc.x + area.x, matchLoc.y + area.y);
        Point matchLoc_ty = new Point(matchLoc.x + mTemplate.cols() + area.x,
                matchLoc.y + mTemplate.rows() + area.y);

        Imgproc.rectangle(mRgba, matchLoc_tx, matchLoc_ty, new Scalar(255, 255, 0,
                255));
        Rect rec = new Rect(matchLoc_tx,matchLoc_ty);
        return rec;
    }

    // 템플릿 불러오기
    private Mat get_template(CascadeClassifier clasificator, Rect area, int size) {
        Mat template = new Mat();
        Mat mROI = mGray.submat(area);
        MatOfRect eyes = new MatOfRect();
        Point iris = new Point();
        Rect eye_template = new Rect();
        clasificator.detectMultiScale(mROI, eyes, 1.15, 2,
                Objdetect.CASCADE_FIND_BIGGEST_OBJECT
                        | Objdetect.CASCADE_SCALE_IMAGE, new Size(30, 30),
                new Size());

        Rect[] eyesArray = eyes.toArray();
        for (int i = 0; i < eyesArray.length;) {
            Rect e = eyesArray[i];
            e.x = area.x + e.x;
            e.y = area.y + e.y;
            Rect eye_only_rectangle = new Rect((int) e.tl().x,
                    (int) (e.tl().y + e.height * 0.4), (int) e.width,
                    (int) (e.height * 0.6));
            mROI = mGray.submat(eye_only_rectangle);
            Mat vyrez = mRgba.submat(eye_only_rectangle);


            Core.MinMaxLocResult mmG = Core.minMaxLoc(mROI);

            Imgproc.circle(vyrez, mmG.minLoc, 2, new Scalar(255, 255, 255, 255), 2);
            iris.x = mmG.minLoc.x + eye_only_rectangle.x;
            iris.y = mmG.minLoc.y + eye_only_rectangle.y;
            eye_template = new Rect((int) iris.x - size / 2, (int) iris.y
                    - size / 2, size, size);
            Imgproc.rectangle(mRgba, eye_template.tl(), eye_template.br(),
                    new Scalar(255, 0, 0, 255), 2);
            template = (mGray.submat(eye_template)).clone();
            return template;
        }
        return template;
    }

    //미리 학습된 haar cascade 불러오기
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(fragmentActivity) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    CascadeClassifier cascade = new CascadeClassifier();
                    try {
                        // load cascade file from application resources
                        InputStream is = fragmentActivity.getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                        File cascadeDir = fragmentActivity.getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "haarcascade_lefteye_2splits.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        // load cascade file from application resources
                        InputStream ise = fragmentActivity.getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                        File cascadeDirEye = fragmentActivity.getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFileEye = new File(cascadeDirEye, "haarcascade_eye.xml");
                        FileOutputStream ose = new FileOutputStream(mCascadeFileEye);

                        while ((bytesRead = ise.read(buffer)) != -1) {
                            ose.write(buffer, 0, bytesRead);
                        }
                        ise.close();
                        ose.close();



                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                        mJavaDetectorEye = new CascadeClassifier(mCascadeFileEye.getAbsolutePath());
                        if (mJavaDetectorEye.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier for eye");
                            mJavaDetectorEye = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileEye.getAbsolutePath());

                        cascadeDir.delete();
                        cascadeDirEye.delete();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mOpenCvCameraView.enableFpsMeter();
                    mOpenCvCameraView.setCameraIndex(1);
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void onpause(){
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        Log.d("onpause", "onpause");
    }

    public void onresume(){
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, fragmentActivity, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void ondestroy(){
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    //정보 전달 스레드
    class InfoSendThread extends Thread{
        public void run(){
            int before = 0;
            while(true) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("eyeset", fSet);
                bundle.putString("eyeset2", sSet);
                bundle.putString("eyeinfo", Double.toString(personalInfo.getArea()) + " " + personalInfo.eyeAreaAvg_30);
                if((personalInfo.getBlinkNumber() - before)<2) {
                    bundle.putString("eyeblink", Integer.toString(personalInfo.getBlinkNumber()));
                    before = personalInfo.getBlinkNumber();
                } else{
                    bundle.putString("eyeblink", Integer.toString(before));
                    personalInfo.setBlinkNumber(before);
                }
                message.setData(bundle);
                handler.sendMessage(message);
                eye1area = 0;
                eye2area = 0;
                try{
                    Thread.sleep(1000);
                } catch (Exception e){}
            }
        }
    }

    class Handler extends android.os.Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String set = bundle.getString("eyeset");
            String set2 = bundle.getString("eyeset2");
            String eye = bundle.getString("eyeinfo");
            String bli = bundle.getString("eyeblink");
            eyeInfo.setText(set + "\n");
            eyeInfo.append(set2 + "\n");
            eyeInfo.append(eye);
            eyeInfo.append("\n" + bli);
        }
    }

}

class PersonalInfo {

    public double eyeAreaAvg_30;
    public double firstEyeArea;
    public double allEyeArea;
    public String firstSetting;
    public String secondSetting;
    public String startTime;
    public String finishTime;
    ArrayList<Double> rightEyeAreaList = new ArrayList();
    ArrayList<Double> leftEyeAreaList = new ArrayList();
    private int blink = 0;
    ArrayList<Double> listRx = new ArrayList();
    ArrayList<Double> listRy = new ArrayList();
    ArrayList<Double> listLx = new ArrayList();
    ArrayList<Double> listLy = new ArrayList();

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
        if(rightEyeAreaList.size()<5) {
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
        result = sumR/rightEyeAreaList.size() + sumL/leftEyeAreaList.size();
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


}
