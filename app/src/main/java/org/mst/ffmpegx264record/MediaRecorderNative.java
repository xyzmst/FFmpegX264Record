//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mst.ffmpegx264record;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.util.Log;

public class MediaRecorderNative extends MediaRecorderBase implements OnErrorListener {
    private static final String VIDEO_SUFFIX = ".ts";

    public MediaRecorderNative() {
    }

    public MediaPart startRecord() {
//        if(!UtilityAdapter.isInitialized()) {
//            UtilityAdapter.initFilterParser();
//        }

        MediaPart result = null;
//        if(this.mMediaObject != null) {
//            this.mRecording = true;
//            result = this.mMediaObject.buildMediaPart(this.mCameraId, ".ts");
//            String cmd = String.format("filename = \"%s\"; ", new Object[]{result.mediaPath});
////            UtilityAdapter.FilterParserAction(cmd, 2);
//            if(this.mAudioRecorder == null && result != null) {
//                this.mAudioRecorder = new AudioRecorder(this);
//                this.mAudioRecorder.start();
//            }
//        }

        return result;
    }

    public void stopRecord() {
//        UtilityAdapter.FilterParserAction("", 3);
        super.stopRecord();
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
//        if(this.mRecording) {
//            UtilityAdapter.RenderDataYuv(data);
//        }

        super.onPreviewFrame(data, camera);
    }

    protected void onStartPreviewSuccess() {
//        if(this.mCameraId == 0) {
//            UtilityAdapter.RenderInputSettings(640, 480, 0, 0);
//        } else {
//            UtilityAdapter.RenderInputSettings(640, 480, 180, 1);
//        }
//
//        UtilityAdapter.RenderOutputSettings(480, 480, this.mFrameRate, 33);
    }

    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if(mr != null) {
                mr.reset();
            }
        } catch (IllegalStateException var5) {
            Log.w("Yixia", "stopRecord", var5);
        } catch (Exception var6) {
            Log.w("Yixia", "stopRecord", var6);
        }

        if(this.mOnErrorListener != null) {
            this.mOnErrorListener.onVideoError(what, extra);
        }

    }

    public void receiveAudioData(byte[] sampleBuffer, int len) {
//        if(this.mRecording && len > 0) {
//            UtilityAdapter.RenderDataPcm(sampleBuffer);
//        }

    }
}
