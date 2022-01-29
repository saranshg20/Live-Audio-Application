package com.example.android.livestream;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = MainActivity.class.getName();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

//    private ActivityMainBinding binding;
    private TextView statusText;
    private TextView latencyText;
    private TextView frameRate;
    private TextView burstRate;
    private Button button ;
    private static final int AUDIO_EFFECT_REQUEST = 0;
    private boolean isPlaying = false;
    private boolean isOutputAudioDeviceConnected = false;
    private RadioButton aAudio, openSL_ES;
//    private static final int OBOE_AAUDIO = 0;
//    private static final int OBOE_OPENSL_ES = 1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aAudio = (RadioButton) findViewById(R.id.aaudioButton);
        openSL_ES = (RadioButton) findViewById(R.id.slesButton);
        aAudio.setEnabled(false);
        openSL_ES.setEnabled(false);
        statusText = (TextView) findViewById(R.id.status_text_view);
        statusText.setText("Please connect to any external bluetooth/wired headset to live stream audio");
        latencyText = (TextView)findViewById(R.id.latency_value);
        frameRate = (TextView) findViewById(R.id.default_frame_rate_value);
        burstRate = (TextView) findViewById(R.id.default_frame_burst_value);
        button = (Button) findViewById(R.id.play_button);
        button.setText("Play");

        /** By Default using OpenSL_ES Audio Api as of now  **/

        /** Checking if Audio Devices for playback are connected to the source  **/
        if(isBLEHeadsetConnected()){
            isOutputAudioDeviceConnected = true;
            native_setRecordingDeviceId(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP);
            Toast.makeText(this, "Connected to Bluetooth Headset!", Toast.LENGTH_SHORT).show();
        }
        else if(isWiredHeadsetConnected()){
            isOutputAudioDeviceConnected = true;
            native_setRecordingDeviceId(AudioDeviceInfo.TYPE_WIRED_HEADSET);
            Toast.makeText(this, "Connected to Wired Headset!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Please Connect to any Bluetooth Headset", Toast.LENGTH_SHORT).show();
        }

        if(isOutputAudioDeviceConnected){
            native_create();
        }
        native_setPlaybackDeviceId(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEffect();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            AudioManager myAudioMgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            String sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            int defaultSampleRate = Integer.parseInt(sampleRateStr);
            String framesPerBurstStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            int defaultFramesPerBurst = Integer.parseInt(framesPerBurstStr);

            frameRate.setText(sampleRateStr);
            burstRate.setText(framesPerBurstStr);

            native_setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst);
        }


    }



    private void toggleEffect() {
        if(button.getText()=="Play"){
            startEffect();
            button.setText("Stop");
            statusText.setText("Streaming...");
        }
        else{
            stopEffect();
            button.setText("Play");
            statusText.setText("Press Play Button to live stream audio");
        }
        if(isPlaying){
            isPlaying = false;
        }
        else{
            isPlaying = true;
        }
    }


    private boolean isWiredHeadsetConnected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager packageManager = getPackageManager();
            // The results from AudioManager.getDevices can't be trusted unless the device
            // advertises FEATURE_AUDIO_OUTPUT.
            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
                return false;
            }
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : devices) {
                if (device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBLEHeadsetConnected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager packageManager = getPackageManager();
            // The results from AudioManager.getDevices can't be trusted unless the device
            // advertises FEATURE_AUDIO_OUTPUT.
            if (!packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
                return false;
            }
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : devices) {
                if (device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        native_create();
    }
    @Override
    protected void onPause() {
        stopEffect();
        native_delete();
        super.onPause();
    }


    private void startEffect() {
        Log.d(TAG, "Attempting to start");

        if (!isRecordPermissionGranted()){
            requestRecordPermission();
            return;
        }

        boolean success = native_setEffectOn(true);
        if (success) {
//            statusText.setText(R.string.status_playing);
//            button.setText(R.string.stop_effect);
            isPlaying = true;
        } else {
//            statusText.setText(R.string.status_open_failed);
            isPlaying = false;
        }
    }

    private boolean isRecordPermissionGranted() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED);
    }


    private void stopEffect() {
        Log.d(TAG, "Playing, attempting to stop");
        native_setEffectOn(false);
        resetStatusView();
        button.setText(R.string.start_effect);
        isPlaying = false;
    }

    private void requestRecordPermission(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                AUDIO_EFFECT_REQUEST);
    }

    private void resetStatusView() {
        statusText.setText(R.string.status_reset);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (AUDIO_EFFECT_REQUEST != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 1 ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            // User denied the permission, without this we cannot record audio
            // Show a toast and update the status accordingly
            statusText.setText(R.string.status_record_audio_denied);
            Toast.makeText(getApplicationContext(),
                    getString(R.string.need_record_audio_permission),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            // Permission was granted, start live effect
            toggleEffect();
        }
    }
    private native void native_setDefaultStreamValues(int defaultSampleRate, int defaultFramesPerBurst);

    private native void native_setRecordingDeviceId(int typeBleHeadset);

    private native void native_setPlaybackDeviceId(int typeBuiltinSpeaker);

    private native void native_create();

    private native void native_delete();

    private native boolean native_setEffectOn(boolean b);

}





//    private native void native_setAudioApi();

    /**
     * A native method that is implemented by the 'livestream' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
