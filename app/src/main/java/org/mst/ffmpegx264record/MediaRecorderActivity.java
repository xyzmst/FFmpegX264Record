package org.mst.ffmpegx264record;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author mac
 * @title MediaRecorderActivity$
 * @description
 * @modifier
 * @date
 * @since 16/1/17$ 下午5:40$
 **/
public class MediaRecorderActivity extends Activity {

    @InjectView(R.id.record_camera_led)
    CheckBox mRecordLed; /** 闪光灯 */

    @InjectView(R.id.record_camera_switcher)
    CheckBox mCameraSwitch;/** 前后摄像头切换 */
    @InjectView(R.id.record_controller)
    Button mRecordController; /** 拍摄按钮 */
    @InjectView(R.id.record_preview)
    SurfaceView mSurfaceView;/** 摄像头数据显示画布 */
    @InjectView(R.id.record_focusing)
    ImageView   mFocusImage;

    /** 录制最长时间 */
    public final static int RECORD_TIME_MAX = 10 * 1000;
    /** 录制最小时间 */
    public final static int RECORD_TIME_MIN = 3 * 1000;
    /** 刷新进度条 */
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    /** 延迟拍摄停止 */
    private static final int HANDLE_STOP_RECORD = 1;
    /** 对焦 */
    private static final int HANDLE_HIDE_RECORD_FOCUS = 2;


    /** 需要重新编译（拍摄新的或者回删） */
    private boolean mRebuild;
    /** on */
    private boolean mCreated;
    /** 是否是点击状态 */
    private volatile boolean mPressedStatus;
    /** 是否已经释放 */
    private volatile boolean mReleased;
    /** 对焦图片宽度 */
    private int mFocusWidth;
    /** 底部背景色 */
    private int mBackgroundColorNormal, mBackgroundColorPress;
    /** 屏幕宽度 */
    private int mWindowWidth;
    /** SDK视频录制对象 */
    private MediaRecorderBase mMediaRecorder;
    /** 对焦动画 */
    private Animation mFocusAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        mWindowWidth = DeviceUtils.getScreenWidth(this);
        // ~~~ 绑定事件
        if (DeviceUtils.hasICS()){
            //点击屏幕录制
            mSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);
        }
        // ~~~ 设置数据

        // 是否支持前置摄像头
        if (MediaRecorderBase.isSupportFrontCamera()) {
//            mCameraSwitch.setOnClickListener(new );
        } else {
            mCameraSwitch.setVisibility(View.GONE);
        }
        // 是否支持闪光灯
        if (DeviceUtils.isSupportCameraLedFlash(getPackageManager())) {
//            mRecordLed.setOnClickListener(this);
        } else {
            mRecordLed.setVisibility(View.GONE);
        }
        initSurfaceView();
    }

    /** 初始化画布 */
    private void initSurfaceView() {
        final int w = DeviceUtils.getScreenWidth(this);
//        ((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin = w;
        int width = w;
        int height = w * 4 / 3;
        //
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView
                .getLayoutParams();
        lp.width = width;
        lp.height = height;
        mSurfaceView.setLayoutParams(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        UtilityAdapter.freeFilterParser();
//        UtilityAdapter.initFilterParser();

        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            mRecordLed.setChecked(false);
            mMediaRecorder.prepare();
        }
    }

    /** 初始化拍摄SDK */
    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderNative();
        mRebuild = true;

//        mMediaRecorder.setOnErrorListener(this);
//        mMediaRecorder.setOnEncodeListener(this);
//        File f = new File(VCamera.getVideoCachePath());
//        if (!FileUtils.checkFile(f)) {
//            f.mkdirs();
//        }
//        String key = String.valueOf(System.currentTimeMillis());
//        mMediaObject = mMediaRecorder.setOutputDirectory(key,
//                VCamera.getVideoCachePath() + key);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();

    }

    @OnClick(R.id.record_camera_switcher)
    public void changeCamera(View view){
        if (mRecordLed.isChecked()) {
            if (mMediaRecorder != null) {
                mMediaRecorder.toggleFlashMode();
            }
            mRecordLed.setChecked(false);
        }

        if (mMediaRecorder != null) {
            mMediaRecorder.switchCamera();
        }

        if (mMediaRecorder.isFrontCamera()) {
            mRecordLed.setEnabled(false);
        } else {
            mRecordLed.setEnabled(true);
        }
    }

    @OnClick(R.id.record_camera_led)
    public void openLed(View view){
    // 开启前置摄像头以后不支持开启闪光灯
        if (mMediaRecorder != null) {
            if (mMediaRecorder.isFrontCamera()) {
                return;
            }
        }

        if (mMediaRecorder != null) {
            mMediaRecorder.toggleFlashMode();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecord();
//        UtilityAdapter.freeFilterParser();
        if (!mReleased) {
            if (mMediaRecorder != null)
                mMediaRecorder.release();
        }
        mReleased = false;
    }

    /** 停止录制 */
    private void stopRecord() {
        mPressedStatus = false;
//        mRecordController.setImageResource(R.drawable.record_controller_normal);
//        mBottomLayout.setBackgroundColor(mBackgroundColorNormal);

        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
        }

//        mRecordDelete.setVisibility(View.VISIBLE);
        mCameraSwitch.setEnabled(true);
        mRecordLed.setEnabled(true);

//        mHandler.removeMessages(HANDLE_STOP_RECORD);
//        checkStatus();
    }


    /** 点击屏幕录制 */
    private View.OnTouchListener mOnSurfaveViewTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null || !mCreated) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 检测是否手动对焦
                    if (checkCameraFocus(event))
                        return true;
                    break;
            }
            return true;
        }

    };

    /** 手动对焦 */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private boolean checkCameraFocus(MotionEvent event) {
        mFocusImage.setVisibility(View.GONE);
        float x = event.getX();
        float y = event.getY();
        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();

        Rect touchRect = new Rect((int) (x - touchMajor / 2),
                (int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
                (int) (y + touchMinor / 2));
        // The direction is relative to the sensor orientation, that is, what
        // the sensor sees. The direction is not affected by the rotation or
        // mirroring of setDisplayOrientation(int). Coordinates of the rectangle
        // range from -1000 to 1000. (-1000, -1000) is the upper left point.
        // (1000, 1000) is the lower right point. The width and height of focus
        // areas cannot be 0 or negative.
        // No matter what the zoom level is, (-1000,-1000) represents the top of
        // the currently visible camera frame
        if (touchRect.right > 1000)
            touchRect.right = 1000;
        if (touchRect.bottom > 1000)
            touchRect.bottom = 1000;
        if (touchRect.left < 0)
            touchRect.left = 0;
        if (touchRect.right < 0)
            touchRect.right = 0;

        if (touchRect.left >= touchRect.right
                || touchRect.top >= touchRect.bottom)
            return false;

        ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        focusAreas.add(new Camera.Area(touchRect, 1000));
        if (!mMediaRecorder.manualFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                // if (success) {
                mFocusImage.setVisibility(View.GONE);
                // }
            }
        }, focusAreas)) {
            mFocusImage.setVisibility(View.GONE);
        }

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFocusImage
                .getLayoutParams();
        int left = touchRect.left - (mFocusWidth / 2);// (int) x -
        // (focusingImage.getWidth()
        // / 2);
        int top = touchRect.top - (mFocusWidth / 2);// (int) y -
        // (focusingImage.getHeight()
        // / 2);
        if (left < 0)
            left = 0;
        else if (left + mFocusWidth > mWindowWidth)
            left = mWindowWidth - mFocusWidth;
        if (top + mFocusWidth > mWindowWidth)
            top = mWindowWidth - mFocusWidth;

        lp.leftMargin = left;
        lp.topMargin = top;
        mFocusImage.setLayoutParams(lp);
        mFocusImage.setVisibility(View.VISIBLE);

        if (mFocusAnimation == null)
            mFocusAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.record_focus);

        mFocusImage.startAnimation(mFocusAnimation);

        mHandler.sendEmptyMessageDelayed(HANDLE_HIDE_RECORD_FOCUS, 3500);// 最多3.5秒也要消失
        return true;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_INVALIDATE_PROGRESS:
                    if (mMediaRecorder != null && !isFinishing()) {
//                        if (mProgressView != null)
//                            mProgressView.invalidate();
                        // if (mPressedStatus)
                        // titleText.setText(String.format("%.1f",
                        // mMediaRecorder.getDuration() / 1000F));
                        if (mPressedStatus)
                            sendEmptyMessageDelayed(0, 30);
                    }
                    break;
            }
        }
    };
}
