package com.example.voicerec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView textViewStatus;
    private EditText editTextIp;


    private AudioRecord audioRecord;


    private int intBufferSize;
    private byte[] shortAudioData;

    private boolean isActive = false;

    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        textViewStatus = findViewById(R.id.textViewStatus);
        editTextIp = findViewById(R.id.editTextIp);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    threadLoop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @SuppressLint("SetTextI18n")
    public void buttonStart(View view) {
        isActive = true;
        textViewStatus.setText("Active");
        thread.start();
    }

    @SuppressLint("SetTextI18n")
    public void buttonStop(View view) {
        isActive = false;
        audioRecord.stop();
        textViewStatus.setText("Stopped");

    }

    private void threadLoop() throws IOException {

        int intRecordSampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);

        intBufferSize = AudioRecord.getMinBufferSize(intRecordSampleRate, AudioFormat.CHANNEL_IN_MONO
                , AudioFormat.ENCODING_PCM_16BIT);

        shortAudioData = new byte[intBufferSize];
        Socket socket = new Socket();
        String ip = editTextIp.getText().toString();
        socket.connect(new InetSocketAddress( ip, 5000));





        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC
                , intRecordSampleRate
                , AudioFormat.CHANNEL_IN_MONO
                , AudioFormat.ENCODING_PCM_16BIT
                , intBufferSize);






        audioRecord.startRecording();

        while (isActive){

            audioRecord.read(shortAudioData, 0, intBufferSize);
            OutputStream out = socket.getOutputStream();
            out.write(shortAudioData);





        }
    }
}