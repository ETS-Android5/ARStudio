package com.ProLabs.arstudyboard;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import com.google.ar.sceneform.SceneView;

import java.io.File;
import java.io.IOException;

/**
 * Video Recorder class handles recording the contents of a SceneView. It uses MediaRecorder to
 * encode the video. The quality settings can be set explicitly or simply use the CamcorderProfile
 * class to select a predefined set of parameters.
 */
public class VideoRecorder {
    private static final String TAG = "VideoRecorder";
    private static final int DEFAULT_BITRATE = 15000000;
    private static final int DEFAULT_FRAMERATE = 60;

    // recordingVideoFlag is true when the media recorder is capturing video.
    private boolean recordingVideoFlag;

    private MediaRecorder mediaRecorder;

    private Size videoSize;

    private SceneView sceneView;
    private int videoCodec;
    private File videoDirectory;
    private String videoBaseName;
    private File videoPath;
    private int bitRate = DEFAULT_BITRATE;
    private int frameRate = DEFAULT_FRAMERATE;
    private Surface encoderSurface;
    private Context context;
    private ParcelFileDescriptor fileDescriptor;
    private Handler handler = new Handler(Looper.getMainLooper());

    public VideoRecorder(Context context) {
        this.context = context;
        recordingVideoFlag = false;
    }

    public int getFrameRate() {
        return frameRate;
    }

    private static final int[] FALLBACK_QUALITY_LEVELS = {
            CamcorderProfile.QUALITY_HIGH_SPEED_2160P,
            CamcorderProfile.QUALITY_HIGH_SPEED_1080P,
            CamcorderProfile.QUALITY_HIGH_SPEED_720P,
            CamcorderProfile.QUALITY_HIGH_SPEED_480P,
            CamcorderProfile.QUALITY_HIGH_SPEED_HIGH,
            CamcorderProfile.QUALITY_HIGH_SPEED_LOW,
            CamcorderProfile.QUALITY_2160P,
            CamcorderProfile.QUALITY_1080P,
            CamcorderProfile.QUALITY_720P,
            CamcorderProfile.QUALITY_480P,
            CamcorderProfile.QUALITY_QVGA,
            CamcorderProfile.QUALITY_HIGH,
            CamcorderProfile.QUALITY_LOW
    };

    public File getVideoPath() {
        return videoPath;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public void setSceneView(SceneView sceneView) {
        this.sceneView = sceneView;
    }

    /**
     * Toggles the state of video recording.
     *
     * @return true if recording is now active.
     */
    public boolean onToggleRecord(Context context) {
        if (recordingVideoFlag) {
            stopRecordingVideo();
        } else {
            startRecordingVideo();
        }
        return recordingVideoFlag;
    }

    private void startRecordingVideo() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }

        try {
            buildFilename();
            setUpMediaRecorder();
        } catch (IOException e) {
            Log.e(TAG, "Exception setting up recorder", e);
            return;
        }

        // Set up Surface for the MediaRecorder
        encoderSurface = mediaRecorder.getSurface();

        sceneView.startMirroringToSurface(
                encoderSurface, 0, 0, videoSize.getWidth(), videoSize.getHeight());

        recordingVideoFlag = true;
    }

    private void buildFilename() {
        videoBaseName = "ARS_"+Long.toHexString(System.currentTimeMillis()) + ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                videoPath=null;
                ContentValues values = new ContentValues(4);
                values.put(MediaStore.Video.Media.DISPLAY_NAME, videoBaseName);
                values.put(MediaStore.Video.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/ARStudio/");

                Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                fileDescriptor = context.getContentResolver().openFileDescriptor(uri, "w");
            }
            catch (Exception e)
            {
                handler.post(()->{
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
        else
        {

            if (videoDirectory == null) {
                videoDirectory =
                        new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                                        + "/ARStudio");
            }

            videoPath =
                    new File(
                            videoDirectory, videoBaseName);
            File dir = videoPath.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }


    }

    private void stopRecordingVideo() {
        // UI
        recordingVideoFlag = false;

        if (encoderSurface != null) {
            sceneView.stopMirroringToSurface(encoderSurface);
            encoderSurface = null;
        }
        // Stop recording
        mediaRecorder.stop();
        mediaRecorder.reset();
    }

    private void setUpMediaRecorder() throws IOException {

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncodingBitRate(bitRate);
        mediaRecorder.setVideoFrameRate(frameRate);
        mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
        mediaRecorder.setVideoEncoder(videoCodec);

        if(videoPath==null)
        {
            mediaRecorder.setOutputFile(fileDescriptor.getFileDescriptor());
        }
        else
        {
            mediaRecorder.setOutputFile(videoPath.getAbsolutePath());
        }

        mediaRecorder.prepare();

        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Exception starting capture: " + e.getMessage(), e);
        }
    }

    public void setVideoSize(int width, int height) {
        videoSize = new Size(width, height);
    }

    public void setVideoQuality(int quality , int orientation) {
        CamcorderProfile profile = null;
        if (CamcorderProfile.hasProfile(quality)) {
            profile = CamcorderProfile.get(quality);
        }
        if (profile == null) {
            // Select a quality  that is available on this device.
            for (int level : FALLBACK_QUALITY_LEVELS) {
                if (CamcorderProfile.hasProfile(level)) {
                    profile = CamcorderProfile.get(level);
                    break;
                }
            }
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        } else {
            setVideoSize(profile.videoFrameHeight, profile.videoFrameWidth);
        }
        setVideoCodec(profile.videoCodec);
        setBitRate(profile.videoBitRate);
        setFrameRate(DEFAULT_FRAMERATE);
    }

    public void setVideoCodec(int videoCodec) {
        this.videoCodec = videoCodec;
    }

    public boolean isRecording() {
        return recordingVideoFlag;
    }
}
