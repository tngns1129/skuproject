package com.familyset.myapplication.ui.main.blink;

import static org.opencv.imgproc.Imgproc.TM_CCOEFF;
import static org.opencv.imgproc.Imgproc.TM_CCOEFF_NORMED;
import static org.opencv.imgproc.Imgproc.TM_CCORR;
import static org.opencv.imgproc.Imgproc.TM_CCORR_NORMED;
import static org.opencv.imgproc.Imgproc.TM_SQDIFF;
import static org.opencv.imgproc.Imgproc.TM_SQDIFF_NORMED;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.familyset.myapplication.R;
import com.familyset.myapplication.model.blink.PersonalInfo;

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

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BlinkFragmentViewModel extends ViewModel {

    private Mat mRgba;
    private Mat mGray;
    private static final String TAG = "opencv";
    private CascadeClassifier mJavaDetector;
    private CascadeClassifier mJavaDetectorEye;
    private File mCascadeFile = null;
    private File mCascadeFileEye = null;
    private int mAbsoluteeyeSize = 0;
    private float mRelativeeyeSize = 0.001f;
    private int learn_frames = 0;
    private Mat teplateR;
    private Mat teplateL;
    private int method = 5;
    private double eye1area = 0;
    private double eye2area = 0;
    private double areaAvgR = 0;
    private double areaAvgL = 0;
    private ArrayList<Double> areaR = new ArrayList();
    private ArrayList<Double> areaL = new ArrayList();
    private Rect eye_right = new Rect();
    private Rect eye_left = new Rect();
    private PersonalInfo personalInfo = null;
    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmmss");
    private BaseLoaderCallback mLoaderCallback;
    private Boolean beforeBlink = false;

    private int count = 0;

    private boolean running = false;

    private MutableLiveData<String> _distance = new MutableLiveData<>();
    public LiveData<String> distance = _distance;

    private MutableLiveData<String> _distanceAvg = new MutableLiveData<>();
    public LiveData<String> distanceAvg = _distanceAvg;

    private MutableLiveData<String> _blink = new MutableLiveData<>();
    public LiveData<String> blink = _blink;

    private MutableLiveData<String> _templeset = new MutableLiveData<>();
    public LiveData<String> templeset = _templeset;

    //static {
    //    System.loadLibrary("opencv_java4");
    //    System.loadLibrary("native-lib");
    //}

    @Inject
    public BlinkFragmentViewModel() {}

    //public BlinkFragmentViewModel(Context context){
        //this.view = view;
        //this.fragmentActivity = fragmentActivity;
        //initView();
    //}

    public String getAllPersonalInfo() {
        return personalInfo.getAll();
    }

    public void start(String fSet, String sSet) {
        if (personalInfo == null) {
            personalInfo = initPersonalInfo(fSet, sSet);
        }
        personalInfo.setBlinkNumber(0);
        running = true;
    }

    public void initOpenCV(Context context) {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, context, getLoaderCallback(context));
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");

            getLoaderCallback(context).onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void stop() {
        finishPersonalInfo();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    private void finishPersonalInfo() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        personalInfo.finishObserve(mFormat.format(mDate));
    }

    private PersonalInfo initPersonalInfo(String fSet, String sSet) {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        PersonalInfo personalInfo = new PersonalInfo(mFormat.format(mDate),fSet,sSet);
        return personalInfo;
    }

    //미리 학습된 haar cascade 불러오기
    private BaseLoaderCallback getLoaderCallback(Context context) {
        if (mLoaderCallback == null) {
            mLoaderCallback = new BaseLoaderCallback(context) {
                @Override
                public void onManagerConnected(int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:
                            try {
                                if (mCascadeFile == null) {
                                    // load cascade file from application resources
                                    InputStream is = context.getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                                    File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
                                    mCascadeFile = new File(cascadeDir, "haarcascade_lefteye_2splits.xml");
                                    FileOutputStream os = new FileOutputStream(mCascadeFile);

                                    byte[] buffer = new byte[4096];
                                    int bytesRead;
                                    while ((bytesRead = is.read(buffer)) != -1) {
                                        os.write(buffer, 0, bytesRead);
                                    }
                                    is.close();
                                    os.close();
                                    //cascadeDir.delete();

                                    mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                                    if (mJavaDetector.empty()) {
                                        Log.e(TAG, "Failed to load cascade classifier");
                                        mJavaDetector = null;
                                    } else {
                                        Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                                    }
                                }

                                if (mCascadeFileEye == null) {
                                    // load cascade file from application resources
                                    InputStream ise = context.getResources().openRawResource(R.raw.haarcascade_lefteye_2splits);
                                    File cascadeDirEye = context.getDir("cascade", Context.MODE_PRIVATE);
                                    mCascadeFileEye = new File(cascadeDirEye, "haarcascade_lefteye_2splits.xml");
                                    FileOutputStream ose = new FileOutputStream(mCascadeFileEye);

                                    byte[] buffer = new byte[4096];
                                    int bytesRead;
                                    while ((bytesRead = ise.read(buffer)) != -1) {
                                        ose.write(buffer, 0, bytesRead);
                                    }
                                    ise.close();
                                    ose.close();
                                    //cascadeDirEye.delete();

                                    mJavaDetectorEye = new CascadeClassifier(mCascadeFileEye.getAbsolutePath());
                                    if (mJavaDetectorEye.empty()) {
                                        Log.e(TAG, "Failed to load cascade classifier for eye");
                                        mJavaDetectorEye = null;
                                    } else {
                                        Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileEye.getAbsolutePath());
                                    }
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        default:
                            super.onManagerConnected(status);

                    }
                }
            };
        }

        return mLoaderCallback;
    }

    public void resetLearnFrames() {
        learn_frames = 0;
    }

    public Mat drawEyes(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        Mat rotImage = Imgproc.getRotationMatrix2D(
                new Point(mRgba.cols() / 2, mRgba.rows() / 2), 90, 1.0);
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
        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mGray, eyes, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteeyeSize, mAbsoluteeyeSize), new Size());
        } else {
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
            Log.e(TAG, eyesArray[0].toString());
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
                _templeset.postValue("학습중");
            } else if(learn_frames == 5){
                eye_right = match_eye(eyearea_right, teplateR, method);
                eye_left = match_eye(eyearea_left, teplateL, method);
                personalInfo.setEyeAreaAvg_30();
                _distanceAvg.postValue(Double.toString(personalInfo.getEyeAreaAvg_30()));
                learn_frames++;
                _templeset.postValue("");
            }
            else {
                // 템플릿 학습 완료시
                eye_right = match_eye(eyearea_right, teplateR, method);
                eye_left = match_eye(eyearea_left, teplateL, method);
                if (eyesArray.length > 1) {
                    if(Math.abs(eyesArray[0].area()-eyesArray[1].area()) < 3000) {
                        if (!beforeBlink)
                            beforeBlink = personalInfo.blinkEyeCheck(eyearea_right, eye_right, eyearea_left, eye_left);
                        else beforeBlink = false;
                    }
                }
                //return null;
            }
        } else {
            personalInfo.areaListAdd(0,0);
            if (learn_frames > 4) {
                //return null;
            }
        }

        // info 변경
        //count += 1;
        //if (count == 60) {
        //    updatePersonalInfoLiveData();
        //    count = 0;
        //}
        updatePersonalInfoLiveData();

        return mRgba;
    }

    private void updatePersonalInfoLiveData() {
        _distance.postValue(Double.toString(personalInfo.getArea()));
        _blink.postValue(Integer.toString(personalInfo.getBlinkNumber()));
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
}