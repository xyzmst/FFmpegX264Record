//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mst.ffmpegx264record;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public abstract class MediaRecorderBase implements Callback, PreviewCallback, IMediaRecorder {
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    public static final int MEDIA_ERROR_CAMERA_SET_PREVIEW_DISPLAY = 101;
    public static final int MEDIA_ERROR_CAMERA_PREVIEW = 102;
    public static final int MEDIA_ERROR_CAMERA_AUTO_FOCUS = 103;
    public static final int AUDIO_RECORD_ERROR_UNKNOWN = 0;
    public static final int AUDIO_RECORD_ERROR_SAMPLERATE_NOT_SUPPORT = 1;
    public static final int AUDIO_RECORD_ERROR_GET_MIN_BUFFER_SIZE_NOT_SUPPORT = 2;
    public static final int AUDIO_RECORD_ERROR_CREATE_FAILED = 3;
    public static final int VIDEO_BITRATE_NORMAL = 1024;
    public static final int VIDEO_BITRATE_MEDIUM = 1536;
    public static final int VIDEO_BITRATE_HIGH = 2048;
    protected static final int MESSAGE_ENCODE_START = 0;
    protected static final int MESSAGE_ENCODE_PROGRESS = 1;
    protected static final int MESSAGE_ENCODE_COMPLETE = 2;
    protected static final int MESSAGE_ENCODE_ERROR = 3;
    public static final int MAX_FRAME_RATE = 25;
    public static final int MIN_FRAME_RATE = 15;
    protected Camera camera;
    protected Parameters mParameters = null;
    protected List<Size> mSupportedPreviewSizes;
    protected SurfaceHolder mSurfaceHolder;
    protected AudioRecorder mAudioRecorder;
    protected MediaRecorderBase.EncodeHandler mEncodeHanlder;
//    protected MediaObject mMediaObject;
    protected MediaRecorderBase.OnEncodeListener mOnEncodeListener;
    protected MediaRecorderBase.OnErrorListener mOnErrorListener;
    protected MediaRecorderBase.OnPreparedListener mOnPreparedListener;
    protected int mFrameRate = 15;
    protected int mCameraId = 0;
    protected int mVideoBitrate = 2048;
    protected boolean mPrepared;
    protected boolean mStartPreview;
    protected boolean mSurfaceCreated;
    protected volatile boolean mRecording;
    protected volatile long mPreviewFrameCallCount = 0L;

    public MediaRecorderBase() {
    }

    public void setSurfaceHolder(SurfaceHolder sh) {
        if(sh != null) {
            sh.addCallback(this);
            if(!DeviceUtils.hasHoneycomb()) {
                sh.setType(3);
            }
        }

    }

    public void setOnEncodeListener(MediaRecorderBase.OnEncodeListener l) {
        this.mOnEncodeListener = l;
        this.mEncodeHanlder = new MediaRecorderBase.EncodeHandler(this);
    }

    public void setOnPreparedListener(MediaRecorderBase.OnPreparedListener l) {
        this.mOnPreparedListener = l;
    }

    public void setOnErrorListener(MediaRecorderBase.OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    public boolean isFrontCamera() {
        return this.mCameraId == 1;
    }

    @SuppressLint({"NewApi"})
    @TargetApi(9)
    public static boolean isSupportFrontCamera() {
        if(!DeviceUtils.hasGingerbread()) {
            return false;
        } else {
            int numberOfCameras = Camera.getNumberOfCameras();
            return 2 == numberOfCameras;
        }
    }

    public void switchCamera(int cameraFacingFront) {
        switch(cameraFacingFront) {
        case 0:
        case 1:
            this.mCameraId = cameraFacingFront;
            this.stopPreview();
            this.startPreview();
        default:
        }
    }

    public void switchCamera() {
        if(this.mCameraId == 0) {
            this.switchCamera(1);
        } else {
            this.switchCamera(0);
        }

    }

    public boolean autoFocus(AutoFocusCallback cb) {
        if(this.camera != null) {
            try {
                this.camera.cancelAutoFocus();
                if(this.mParameters != null) {
                    String e = this.getAutoFocusMode();
                    if(StringUtils.isNotEmpty(e)) {
                        this.mParameters.setFocusMode(e);
                        this.camera.setParameters(this.mParameters);
                    }
                }

                this.camera.autoFocus(cb);
                return true;
            } catch (Exception var3) {
                if(this.mOnErrorListener != null) {
                    this.mOnErrorListener.onVideoError(103, 0);
                }

                if(var3 != null) {
                    Log.e("Yixia", "autoFocus", var3);
                }
            }
        }

        return false;
    }

    private String getAutoFocusMode() {
        if(this.mParameters != null) {
            List focusModes = this.mParameters.getSupportedFocusModes();
            if((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL.endsWith("MEIZU MX3")) && this.isSupported(focusModes, "continuous-picture")) {
                return "continuous-picture";
            }

            if(this.isSupported(focusModes, "continuous-video")) {
                return "continuous-video";
            }

            if(this.isSupported(focusModes, "auto")) {
                return "auto";
            }
        }

        return null;
    }

    @SuppressLint({"NewApi"})
    @TargetApi(14)
    public boolean manualFocus(AutoFocusCallback cb, List<Area> focusAreas) {
        if(this.camera != null && focusAreas != null && this.mParameters != null && DeviceUtils.hasICS()) {
            try {
                this.camera.cancelAutoFocus();
                if(this.mParameters.getMaxNumFocusAreas() > 0) {
                    this.mParameters.setFocusAreas(focusAreas);
                }

                if(this.mParameters.getMaxNumMeteringAreas() > 0) {
                    this.mParameters.setMeteringAreas(focusAreas);
                }

                this.mParameters.setFocusMode("macro");
                this.camera.setParameters(this.mParameters);
                this.camera.autoFocus(cb);
                return true;
            } catch (Exception var4) {
                if(this.mOnErrorListener != null) {
                    this.mOnErrorListener.onVideoError(103, 0);
                }

                if(var4 != null) {
                    Log.e("Yixia", "autoFocus", var4);
                }
            }
        }

        return false;
    }

    public boolean toggleFlashMode() {
        if(this.mParameters != null) {
            try {
                String e = this.mParameters.getFlashMode();
                if(!TextUtils.isEmpty(e) && !"off".equals(e)) {
                    this.setFlashMode("off");
                } else {
                    this.setFlashMode("torch");
                }

                return true;
            } catch (Exception var2) {
                Log.e("Yixia", "toggleFlashMode", var2);
            }
        }

        return false;
    }

    private boolean setFlashMode(String value) {
        if(this.mParameters != null && this.camera != null) {
            try {
                if("torch".equals(value) || "off".equals(value)) {
                    this.mParameters.setFlashMode(value);
                    this.camera.setParameters(this.mParameters);
                }

                return true;
            } catch (Exception var3) {
                Log.e("Yixia", "setFlashMode", var3);
            }
        }

        return false;
    }

    public void setVideoBitRate(int bitRate) {
        if(bitRate > 0) {
            this.mVideoBitrate = bitRate;
        }

    }

    public void prepare() {
        this.mPrepared = true;
        if(this.mSurfaceCreated) {
            this.startPreview();
        }

    }

//    public MediaObject setOutputDirectory(String key, String path) {
//        if(StringUtils.isNotEmpty(path)) {
//            File f = new File(path);
//            if(f != null) {
//                if(f.exists()) {
//                    if(f.isDirectory()) {
//                        FileUtils.deleteDir(f);
//                    } else {
//                        FileUtils.deleteFile(f);
//                    }
//                }
//
//                if(f.mkdirs()) {
//                    this.mMediaObject = new MediaObject(key, path, this.mVideoBitrate);
//                }
//            }
//        }
//
//        return this.mMediaObject;
//    }

//    public void setMediaObject(MediaObject mediaObject) {
//        this.mMediaObject = mediaObject;
//    }

    public void stopRecord() {
//        this.mRecording = false;
//        if(this.mMediaObject != null) {
//            MediaPart part = this.mMediaObject.getCurrentPart();
//            if(part != null && part.recording) {
//                part.recording = false;
//                part.endTime = System.currentTimeMillis();
//                part.duration = (int)(part.endTime - part.startTime);
//                part.cutStartTime = 0;
//                part.cutEndTime = part.duration;
//            }
//        }

    }

    private void stopAllRecord() {
//        this.mRecording = false;
//        if(this.mMediaObject != null && this.mMediaObject.getMedaParts() != null) {
//            Iterator var2 = this.mMediaObject.getMedaParts().iterator();
//
//            while(var2.hasNext()) {
//                MediaPart part = (MediaPart)var2.next();
//                if(part != null && part.recording) {
//                    part.recording = false;
//                    part.endTime = System.currentTimeMillis();
//                    part.duration = (int)(part.endTime - part.startTime);
//                    part.cutStartTime = 0;
//                    part.cutEndTime = part.duration;
//                    File videoFile = new File(part.mediaPath);
//                    if(videoFile != null && videoFile.length() < 1L) {
//                        this.mMediaObject.removePart(part, true);
//                    }
//                }
//            }
//        }

    }

    private boolean isSupported(List<String> list, String key) {
        return list != null && list.contains(key);
    }

    protected void prepareCameraParaments() {
        if(this.mParameters != null) {
            List rates = this.mParameters.getSupportedPreviewFrameRates();
            if(rates != null) {
                if(rates.contains(Integer.valueOf(25))) {
                    this.mFrameRate = 25;
                } else {
                    Collections.sort(rates);

                    for(int mode = rates.size() - 1; mode >= 0; --mode) {
                        if(((Integer)rates.get(mode)).intValue() <= 25) {
                            this.mFrameRate = ((Integer)rates.get(mode)).intValue();
                            break;
                        }
                    }
                }
            }

            this.mParameters.setPreviewFrameRate(this.mFrameRate);
            this.mParameters.setPreviewSize(640, 480);
            this.mParameters.setPreviewFormat(17);
            String var3 = this.getAutoFocusMode();
            if(StringUtils.isNotEmpty(var3)) {
                this.mParameters.setFocusMode(var3);
            }

            if(this.isSupported(this.mParameters.getSupportedWhiteBalance(), "auto")) {
                this.mParameters.setWhiteBalance("auto");
            }

            if("true".equals(this.mParameters.get("video-stabilization-supported"))) {
                this.mParameters.set("video-stabilization", "true");
            }

            if(!DeviceUtils.isDevice(new String[]{"GT-N7100", "GT-I9308", "GT-I9300"})) {
                this.mParameters.set("cam_mode", 1);
                this.mParameters.set("cam-mode", 1);
            }

        }
    }

    public void startPreview() {
        if(!this.mStartPreview && this.mSurfaceHolder != null && this.mPrepared) {
            this.mStartPreview = true;

            try {
                if(this.mCameraId == 0) {
                    this.camera = Camera.open();
                } else {
                    this.camera = Camera.open(this.mCameraId);
                }

                this.camera.setDisplayOrientation(90);

                try {
                    this.camera.setPreviewDisplay(this.mSurfaceHolder);
                } catch (IOException var2) {
                    if(this.mOnErrorListener != null) {
                        this.mOnErrorListener.onVideoError(101, 0);
                    }

                    Log.e("Yixia", "setPreviewDisplay fail " + var2.getMessage());
                }

                this.mParameters = this.camera.getParameters();
                this.mSupportedPreviewSizes = this.mParameters.getSupportedPreviewSizes();
                this.prepareCameraParaments();
                this.camera.setParameters(this.mParameters);
                this.setPreviewCallback();
                this.camera.startPreview();
                this.onStartPreviewSuccess();
                if(this.mOnPreparedListener != null) {
                    this.mOnPreparedListener.onPrepared();
                }
            } catch (Exception var3) {
                var3.printStackTrace();
                if(this.mOnErrorListener != null) {
                    this.mOnErrorListener.onVideoError(102, 0);
                }

                Log.e("Yixia", "startPreview fail :" + var3.getMessage());
            }

        }
    }

    protected void onStartPreviewSuccess() {
    }

    protected void setPreviewCallback() {
        Size size = this.mParameters.getPreviewSize();
        if(size != null) {
            PixelFormat pf = new PixelFormat();
            PixelFormat.getPixelFormatInfo(this.mParameters.getPreviewFormat(), pf);
            int buffSize = size.width * size.height * pf.bitsPerPixel / 8;

            try {
                this.camera.addCallbackBuffer(new byte[buffSize]);
                this.camera.addCallbackBuffer(new byte[buffSize]);
                this.camera.addCallbackBuffer(new byte[buffSize]);
                this.camera.setPreviewCallbackWithBuffer(this);
            } catch (OutOfMemoryError var5) {
                Log.e("Yixia", "startPreview...setPreviewCallback...", var5);
            }

            Log.e("Yixia", "startPreview...setPreviewCallbackWithBuffer...width:" + size.width + " height:" + size.height);
        } else {
            this.camera.setPreviewCallback(this);
        }

    }

    public void stopPreview() {
        if(this.camera != null) {
            try {
                this.camera.stopPreview();
                this.camera.setPreviewCallback((PreviewCallback)null);
                this.camera.release();
            } catch (Exception var2) {
                Log.e("Yixia", "stopPreview...");
            }

            this.camera = null;
        }

        this.mStartPreview = false;
    }

    public void release() {
        this.stopAllRecord();
        this.stopPreview();
        if(this.mAudioRecorder != null) {
            this.mAudioRecorder.interrupt();
            this.mAudioRecorder = null;
        }

        this.mSurfaceHolder = null;
        this.mPrepared = false;
        this.mSurfaceCreated = false;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mSurfaceHolder = holder;
        this.mSurfaceCreated = true;
        if(this.mPrepared && !this.mStartPreview) {
            this.startPreview();
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mSurfaceHolder = holder;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mSurfaceHolder = null;
        this.mSurfaceCreated = false;
    }

    public void onAudioError(int what, String message) {
        if(this.mOnErrorListener != null) {
            this.mOnErrorListener.onAudioError(what, message);
        }

    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        ++this.mPreviewFrameCallCount;
        camera.addCallbackBuffer(data);
    }

    public void testPreviewFrameCallCount() {
//        (new CountDownTimer(60000L, $anonymous1) {
//            public void onTick(long millisUntilFinished) {
//                Log.e("[Vitamio Recorder]", "testFrameRate..." + MediaRecorderBase.this.mPreviewFrameCallCount);
//                MediaRecorderBase.this.mPreviewFrameCallCount = 0L;
//            }
//
//            public void onFinish() {
//            }
//        }).start();
    }

    public void receiveAudioData(byte[] sampleBuffer, int len) {
    }

    public void startEncoding() {
//        if(this.mMediaObject != null && this.mEncodeHanlder != null) {
//            this.mEncodeHanlder.removeMessages(1);
//            this.mEncodeHanlder.removeMessages(2);
//            this.mEncodeHanlder.removeMessages(0);
//            this.mEncodeHanlder.removeMessages(3);
//            this.mEncodeHanlder.sendEmptyMessage(0);
//        }
    }

    protected void concatVideoParts() {
//        (new AsyncTask() {
//            protected Boolean doInBackground(Void... params) {
//                String cmd = String.format("ffmpeg %s -i \"%s\" -vcodec copy -acodec copy -absf aac_adtstoasc -f mp4 -movflags faststart \"%s\"", new Object[]{FFMpegUtils.getLogCommand(), MediaRecorderBase.this.mMediaObject.getConcatYUV(), MediaRecorderBase.this.mMediaObject.getOutputTempVideoPath()});
//                return UtilityAdapter.FFmpegRun("", cmd) == 0?Boolean.valueOf(true):Boolean.valueOf(false);
//            }
//
//            protected void onPostExecute(Boolean result) {
//                if(result.booleanValue()) {
//                    MediaRecorderBase.this.mEncodeHanlder.sendEmptyMessage(2);
//                } else {
//                    MediaRecorderBase.this.mEncodeHanlder.sendEmptyMessage(3);
//                }
//
//            }
//        }).execute(new Void[0]);
    }

    public static class EncodeHandler extends Handler {
        private WeakReference<MediaRecorderBase> mMediaRecorderBase;

        public EncodeHandler(MediaRecorderBase l) {
            this.mMediaRecorderBase = new WeakReference(l);
        }

        public void handleMessage(Message msg) {
//            MediaRecorderBase mrb = (MediaRecorderBase)this.mMediaRecorderBase.get();
//            if(mrb != null && mrb.mOnEncodeListener != null) {
//                MediaRecorderBase.OnEncodeListener listener = mrb.mOnEncodeListener;
//                switch(msg.what) {
//                case 0:
//                    listener.onEncodeStart();
//                    this.sendEmptyMessage(1);
//                    break;
//                case 1:
//                    int progress = UtilityAdapter.FilterParserAction("", 5);
//                    if(progress == 100) {
//                        listener.onEncodeProgress(progress);
//                        mrb.concatVideoParts();
//                    } else if(progress == -1) {
//                        this.sendEmptyMessage(3);
//                    } else {
//                        listener.onEncodeProgress(progress);
//                        this.sendEmptyMessageDelayed(1, 200L);
//                    }
//                    break;
//                case 2:
//                    listener.onEncodeComplete();
//                    break;
//                case 3:
//                    listener.onEncodeError();
//                }
//
//            }
        }
    }

    public interface OnEncodeListener {
        void onEncodeStart();

        void onEncodeProgress(int var1);

        void onEncodeComplete();

        void onEncodeError();
    }

    public interface OnErrorListener {
        void onVideoError(int var1, int var2);

        void onAudioError(int var1, String var2);
    }

    public interface OnPreparedListener {
        void onPrepared();
    }
}
