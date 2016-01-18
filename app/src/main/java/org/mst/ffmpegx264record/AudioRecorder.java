//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mst.ffmpegx264record;

import android.media.AudioRecord;

public class AudioRecorder extends Thread {
    private AudioRecord mAudioRecord = null;
    private int mSampleRate = '걄';
    private IMediaRecorder mMediaRecorder;

    public AudioRecorder(IMediaRecorder mediaRecorder) {
        this.mMediaRecorder = mediaRecorder;
    }

    public void setSampleRate(int sampleRate) {
        this.mSampleRate = sampleRate;
    }

    public void run() {
        if(this.mSampleRate != 8000 && this.mSampleRate != 16000 && this.mSampleRate != 22050 && this.mSampleRate != '걄') {
            this.mMediaRecorder.onAudioError(1, "sampleRate not support.");
        } else {
            int mMinBufferSize = AudioRecord.getMinBufferSize(this.mSampleRate, 16, 2);
            if(-2 == mMinBufferSize) {
                this.mMediaRecorder.onAudioError(2, "parameters are not supported by the hardware.");
            } else {
                this.mAudioRecord = new AudioRecord(1, this.mSampleRate, 16, 2, mMinBufferSize);
                if(this.mAudioRecord == null) {
                    this.mMediaRecorder.onAudioError(3, "new AudioRecord failed.");
                } else {
                    try {
                        this.mAudioRecord.startRecording();
                    } catch (IllegalStateException var5) {
                        this.mMediaRecorder.onAudioError(0, "startRecording failed.");
                        return;
                    }

                    byte[] sampleBuffer = new byte[mMinBufferSize];

                    try {
                        while(!Thread.currentThread().isInterrupted()) {
                            int e = this.mAudioRecord.read(sampleBuffer, 0, mMinBufferSize);
                            if(e > 0) {
                                this.mMediaRecorder.receiveAudioData(sampleBuffer, e);
                            }
                        }
                    } catch (Exception var6) {
                        String message = "";
                        if(var6 != null) {
                            message = var6.getMessage();
                        }

                        this.mMediaRecorder.onAudioError(0, message);
                    }

                    this.mAudioRecord.release();
                    this.mAudioRecord = null;
                }
            }
        }
    }
}
