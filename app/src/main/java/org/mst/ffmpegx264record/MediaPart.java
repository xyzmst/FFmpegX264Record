package org.mst.ffmpegx264record;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class MediaPart implements Serializable {
    public int index;
    public String mediaPath;
    public String audioPath;
    public String tempMediaPath;
    public String tempAudioPath;
    public String thumbPath;
    public String tempPath;
    public int type = 0;
    public int cutStartTime;
    public int cutEndTime;
    public int duration;
    public int position;
    public int speed = 10;
    public int cameraId;
    public int yuvWidth;
    public int yuvHeight;
    public transient boolean remove;
    public transient long startTime;
    public transient long endTime;
    public transient FileOutputStream mCurrentOutputVideo;
    public transient FileOutputStream mCurrentOutputAudio;
    public transient volatile boolean recording;

    public MediaPart() {
    }

    public void delete() {
        FileUtils.deleteFile(this.mediaPath);
        FileUtils.deleteFile(this.audioPath);
        FileUtils.deleteFile(this.thumbPath);
        FileUtils.deleteFile(this.tempMediaPath);
        FileUtils.deleteFile(this.tempAudioPath);
    }

    public void writeAudioData(byte[] buffer) throws IOException {
        if (this.mCurrentOutputAudio != null) {
            this.mCurrentOutputAudio.write(buffer);
        }

    }

    public void writeVideoData(byte[] buffer) throws IOException {
        if (this.mCurrentOutputVideo != null) {
            this.mCurrentOutputVideo.write(buffer);
        }

    }

    public void prepare() {
        try {
            this.mCurrentOutputVideo = new FileOutputStream(this.mediaPath);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

        this.prepareAudio();
    }

    public void prepareAudio() {
        try {
            this.mCurrentOutputAudio = new FileOutputStream(this.audioPath);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }

    public int getDuration() {
        return this.duration > 0 ? this.duration : (int) (System.currentTimeMillis() - this.startTime);
    }

    public void stop() {
        if (this.mCurrentOutputVideo != null) {
            try {
                this.mCurrentOutputVideo.flush();
                this.mCurrentOutputVideo.close();
            } catch (IOException var3) {
                var3.printStackTrace();
            }

            this.mCurrentOutputVideo = null;
        }

        if (this.mCurrentOutputAudio != null) {
            try {
                this.mCurrentOutputAudio.flush();
                this.mCurrentOutputAudio.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }

            this.mCurrentOutputAudio = null;
        }

    }
}