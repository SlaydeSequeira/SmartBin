package com.example.smartbin;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.concurrent.atomic.AtomicBoolean;

public class prototype2 extends AppCompatActivity {
    private final int sampleRate = 44100; // Sample rate in Hz
    private final double targetFrequency = 1000.0; // Target frequency in Hz
    private final double tolerance = 50.0; // Tolerance for frequency detection
    private AudioRecord audioRecord;
    private AtomicBoolean isListening = new AtomicBoolean(false);
    private byte[] buffer;
    private FastFourierTransformer transformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prototype2);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            createAudioRecord();
            startListening();
        }
    }

    private void createAudioRecord() {
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        buffer = new byte[bufferSize];
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        transformer = new FastFourierTransformer(DftNormalization.STANDARD);
    }

    private void startListening() {
        if (audioRecord == null) {

            return;
        }

        audioRecord.startRecording();
        isListening.set(true);

        new Thread(() -> {
            while (isListening.get()) {
                int bytesRead = audioRecord.read(buffer, 0, buffer.length);

                // Implement your frequency detection logic here (using FFT)
                double detectedFrequency = detectFrequency(buffer, bytesRead);

                if (Math.abs(detectedFrequency - targetFrequency) < tolerance) {
                    // Frequency detected within the specified tolerance
                    showToast("Frequency Detected!");
                }
            }
        }).start();
    }

    // Implement your frequency detection logic here (using FFT)
    private double detectFrequency(byte[] audioData, int bytesRead) {
        // Convert the audio data to a complex array
        Complex[] complexAudioData = new Complex[bytesRead / 2];
        for (int i = 0; i < bytesRead / 2; i++) {
            complexAudioData[i] = new Complex(audioData[2 * i], audioData[2 * i + 1]);
        }

        // Apply FFT to the complex data
        Complex[] fftResult = transformer.transform(complexAudioData, TransformType.FORWARD);

        // Find the index with the maximum magnitude
        int maxIndex = 0;
        double maxMagnitude = 0.0;
        for (int i = 0; i < fftResult.length / 2; i++) {
            double magnitude = fftResult[i].abs();
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
                maxIndex = i;
            }
        }

        // Calculate the frequency corresponding to the maxIndex
        double detectedFrequency = maxIndex * sampleRate / (double) bytesRead;

        return detectedFrequency;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isListening.set(false);
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
        }
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createAudioRecord();
                startListening();
            } else {
                showToast("Microphone permission denied. Cannot continue.");
            }
        }
    }
}
