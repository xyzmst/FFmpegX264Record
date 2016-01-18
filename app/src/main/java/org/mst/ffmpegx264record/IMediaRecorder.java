//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mst.ffmpegx264record;

public interface IMediaRecorder {
    MediaPart startRecord();

    void stopRecord();

    void onAudioError(int var1, String var2);

    void receiveAudioData(byte[] var1, int var2);
}
