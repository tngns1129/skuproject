// Partial code migrated from https://github.com/ildoonet/tf-pose-estimation

package com.ricardotejo.openpose;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;

import com.ricardotejo.openpose.OverlayView.DrawCallback;
import com.ricardotejo.openpose.env.BorderedText;
import com.ricardotejo.openpose.env.ImageUtils;
import com.ricardotejo.openpose.env.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class MocapActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    private static final int MP_INPUT_SIZE = 368;
    private static final String MP_INPUT_NAME = "image";
    private static final String MP_OUTPUT_L1 = "Openpose/MConv_Stage6_L1_5_pointwise/BatchNorm/FusedBatchNorm";
    private static final String MP_OUTPUT_L2 = "Openpose/MConv_Stage6_L2_5_pointwise/BatchNorm/FusedBatchNorm";
    private static final String MP_MODEL_FILE = "file:///android_asset/frozen_person_model.pb";

    private static final boolean MAINTAIN_ASPECT = true;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;

    private Integer sensorOrientation;

    private Classifier detector;

    private long lastProcessingTimeMs;
    private int lastHumansFound;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private byte[] luminanceCopy;

    private BorderedText borderedText;
    private OverlayView trackingOverlay;

    Bitmap SAMPLE_IMAGE;
    private WrongPose wrongPose = new WrongPose();

    Bitmap image = null;
    List<String> imageDir;

    long mNow;
    Date mDate;
    SimpleDateFormat mSimpleDateFormat;
    String mStringDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent = new Intent();
        try {
            // 종료 전 총 시간 계산
            wrongPose.calAllWrongNeckTimes();
            wrongPose.calAllWrongWaistTimes();
            wrongPose.setFinishTimeNow();

            intent.putExtra("pose", wrongPose.getAll());
            intent.putExtra("poseObject", wrongPose.getAllString());
            intent.putExtra("imageDir", (Parcelable) imageDir);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        int cropSize = MP_INPUT_SIZE;

        // Configure the detector
        detector = TensorFlowPoseDetector.create(
                getAssets(),
                MP_MODEL_FILE,
                MP_INPUT_SIZE,
                MP_INPUT_NAME,
                new String[]{MP_OUTPUT_L1, MP_OUTPUT_L2}
        );

        SAMPLE_IMAGE = BitmapFactory.decodeResource(getResources(), R.drawable.jump2);

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        bitmapToVideoEncoder = new BitmapToVideoEncoder(new BitmapToVideoEncoder.IBitmapToVideoEncoderCallback() {
            @Override
            public void onEncodingComplete(File outputFile) {}
        });
        mDate = new Date(System.currentTimeMillis());
        mSimpleDateFormat = new SimpleDateFormat("hh_mm_ss");
        bitmapToVideoEncoder.startEncoding(cropSize, cropSize,
                new File(getExternalFilesDir(Environment.DIRECTORY_DCIM),
                        "position_" + mSimpleDateFormat.format(mDate) + ".mp4"));
        mSimpleDateFormat.applyPattern("yyyy/MM/dd_hh:mm:ss");

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);
        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        //trackingOverlay = (OverlayView) findViewById(R.id.results);
        // TODO: Draw human bones
        //trackingOverlay.addCallback(
//                new DrawCallback() {
//                    @Override
//                    public void drawCallback(final Canvas canvas) {
//                        // tracker.draw(canvas);
//                    }
//                });

        // TODO: Debug information ( to remove)
        addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        if (!isDebug()) {
                            return;
                        }
                        final Bitmap copy = cropCopyBitmap;
                        if (copy == null) {
                            return;
                        }

                        final int backgroundColor = Color.rgb(0, 255, 0);
                        //canvas.drawColor(backgroundColor);
                        Paint pp = new Paint();
                        pp.setColor(backgroundColor);
                        final float scaleFactor = 2;
                        canvas.drawRect(new Rect(5, 5,
                                15 + copy.getWidth() * (int)scaleFactor,
                                15 + copy.getHeight() * (int)scaleFactor), pp);

                        final Matrix matrix = new Matrix();

                        matrix.postScale(scaleFactor, scaleFactor);

                        // RT: Position of the preview canvas
                        matrix.postTranslate(10, 10);
                        //matrix.postTranslate(
                        //        canvas.getWidth() - copy.getWidth() * scaleFactor,
                        //        canvas.getHeight() - copy.getHeight() * scaleFactor);
                        canvas.drawBitmap(copy, matrix, new Paint());

                        final Vector<String> lines = new Vector<String>();
                        if (detector != null) {
                            final String statString = detector.getStatString();
                            final String[] statLines = statString.split("\n");
                            for (final String line : statLines) {
                                lines.add(line);
                            }
                        }
                        lines.add("");


                        lines.add("Frame: " + previewWidth + "x" + previewHeight);

                        lines.add("Crop: " + copy.getWidth() + "x" + copy.getHeight());
                        lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());
                        lines.add("Rotation: " + sensorOrientation);
                        lines.add("Inference time: " + lastProcessingTimeMs + "ms");

                        lines.add("Start : " + wrongPose.getStartTime());
                        lines.add("Humans found: " + lastHumansFound);
                        lines.add("Sensitivity : " + getFirstSet());
                        lines.add("Sensitivity id : " + getFirstSetId());

                        //borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines); // bottom
                        borderedText.drawLinesTop(canvas, copy.getWidth() * scaleFactor + 30, 30, lines); // top-right
                    }
                });
    }


    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
//        byte[] originalLuminance = getLuminance();
        //trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);


        //rgbFrameBitmap = SAMPLE_IMAGE;
        // TODO: Use real camera image

//        if (luminanceCopy == null) {
//            luminanceCopy = new byte[originalLuminance.length];
//        }
//        System.arraycopy(originalLuminance, 0, luminanceCopy, 0, originalLuminance.length);
        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);

        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null); // paint the cropped image

        //canvas.drawBitmap(rgbFrameBitmap,
        //        new Rect(0, 0, rgbFrameBitmap.getWidth(), rgbFrameBitmap.getWidth()),
        //        new Rect(0, 0, MP_INPUT_SIZE, MP_INPUT_SIZE), null); // RT: Paint the background

//        // For examining the actual TF input.
//        if (SAVE_PREVIEW_BITMAP) {
//            ImageUtils.saveBitmap(croppedBitmap);
//        }

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();

                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);

                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                        lastHumansFound = results.get(0).humans.size();
                        LOGGER.i("Running detection on image (DONE) in " + lastProcessingTimeMs);

                        //cropCopyBitmap = Bitmap.createBitmap(results.get(0).heat);
                        //cropCopyBitmap = Bitmap.createBitmap(results.get(0).pose);

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        draw_humans(canvas, results.get(0).humans);


                        //mNow = System.currentTimeMillis();
                        //mDate = new Date(mNow);
                        //SimpleDateFormat sdf = new SimpleDateFormat("hh_mm_ss");
                        //String filename = sdf.format(mDate);
                        //saveBitmapToJpeg(cropCopyBitmap, filename);
                        if (bitmapToVideoEncoder != null) {
                            Bitmap copy = Bitmap.createBitmap(cropCopyBitmap);
                            draw_date(new Canvas(copy));
                            bitmapToVideoEncoder.queueFrame(copy);
                        }

//                        final Paint paint = new Paint();
//                        paint.setColor(Color.RED);
//                        paint.setStyle(Style.STROKE);
//                        paint.setStrokeWidth(2.0f);
//
//                        final List<Classifier.Recognition> mappedRecognitions =
//                                new LinkedList<Classifier.Recognition>();
//
//                        for (final Classifier.Recognition result : results) {
//                            final RectF location = result.getLocation();
//                            if (location != null) {
//                                canvas.drawRect(location, paint);
//
//                                cropToFrameTransform.mapRect(location);
//                                result.setLocation(location);
//                                mappedRecognitions.add(result);
//                            }
//                        }

                        //trackingOverlay.postInvalidate();

                        requestRender();
                        computingDetection = false;
                    }
                });
    }

    private Integer HUMAN_RADIUS = 3;

    private void draw_date(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        mDate.setTime(System.currentTimeMillis());
        mStringDate = mSimpleDateFormat.format(mDate);
        canvas.drawText(mStringDate, 0, 20, paint);
    }

    private void draw_humans(Canvas canvas, List<TensorFlowPoseDetector.Human> human_list) {
        //def draw_humans(img, human_list):
        // image_h, image_w = img_copied.shape[:2]
        int cp = Common.CocoPart.values().length;
        int image_w = canvas.getWidth();
        int image_h = canvas.getHeight();

        //    for human in human_list:
        for (TensorFlowPoseDetector.Human human : human_list) {
            Point[] centers = new Point[cp];
            //part_idxs = human.keys()
            Set<Integer> part_idxs = human.parts.keySet();

            LOGGER.i("COORD =====================================");
            //사람 뼈 마다 점 그리는 부분
            //for i in range(CocoPart.Background.value):
            for (Common.CocoPart i : Common.CocoPart.values()) {
                //if i not in part_idxs:
                if (!part_idxs.contains(i.index)) {
                    LOGGER.w("COORD %s, NULL, NULL", i.toString());
                    continue;
                }
                //part_coord = human[i][1]
                TensorFlowPoseDetector.Coord part_coord = human.parts.get(i.index);
                //center = (int(part_coord[0] * image_w + 0.5), int(part_coord[1] * image_h + 0.5))
                Point center = new Point((int) (part_coord.x * image_w + 0.5f), (int) (part_coord.y * image_h + 0.5f));
                //centers[i] = center
                centers[i.index] = center;

                //cv2.circle(img_copied, center, 3, CocoColors[i], thickness=3, lineType=8, shift=0)
                Paint paint = new Paint();
                paint.setColor(Color.rgb(Common.CocoColors[i.index][0], Common.CocoColors[i.index][1], Common.CocoColors[i.index][2]));
                paint.setStyle(Style.FILL);
                canvas.drawCircle(center.x, center.y, HUMAN_RADIUS, paint);

                wrongPose.setBorn(i.index, center.x, center.y);

                LOGGER.i("COORD %s, %f, %f", i.toString(), part_coord.x, part_coord.y);
            }

            //# 나온 뼈를 잇는 선을 그리는 부분
            //for pair_order, pair in enumerate(CocoPairsRender):
            for (int pair_order = 0; pair_order < Common.CocoPairsRender.length; pair_order++) {
                int[] pair = Common.CocoPairsRender[pair_order];
                //if pair[0] not in part_idxs or pair[1] not in part_idxs:
                if (!part_idxs.contains(pair[0]) || !part_idxs.contains(pair[1])) {
                    continue;
                }

                //img_copied = cv2.line(img_copied, centers[pair[0]], centers[pair[1]], CocoColors[pair_order], 3)
                Paint paint = new Paint();
                Log.d("borns " , Integer.toString(pair_order));
                int i = wrongPose.getBornWrong(pair_order, getFirstsetStandard());
                paint.setColor(Color.rgb(Common.CocoColors[i][0], Common.CocoColors[i][1], Common.CocoColors[i][2]));
                paint.setStrokeWidth(HUMAN_RADIUS);
                paint.setStyle(Style.STROKE);
                canvas.drawLine(centers[pair[0]].x, centers[pair[0]].y, centers[pair[1]].x, centers[pair[1]].y, paint);
            }
            wrongPose.poseReset();
        }
        //    return img_copied
    }



    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onSetDebug(final boolean debug) {
        detector.enableStatLogging(debug);
    }

    private void saveBitmapToJpeg(Bitmap bitmap, File file) {

        image = bitmap;

        try {
            // 파일을 쓸 수 있는 스트림을 준비합니다.
            FileOutputStream out = new FileOutputStream(file);

            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // 스트림 사용후 닫아줍니다.
            out.close();

        } catch (FileNotFoundException e) {
            Log.e("MyTag","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("MyTag","IOException : " + e.getMessage());
        }
    }

}
