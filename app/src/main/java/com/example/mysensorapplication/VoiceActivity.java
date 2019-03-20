package com.example.mysensorapplication;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mysensorapplication.services.MediaPlayerService;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;


// https://www.sitepoint.com/a-step-by-step-guide-to-building-an-android-audio-player-app/
public class VoiceActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    boolean isTextToSpeechUp;
    private MediaPlayerService player;
    boolean serviceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        textToSpeech = new TextToSpeech(this, status -> {
            Button button = findViewById(R.id.buttonSpeech);

            if (status == 0) {
                this.isTextToSpeechUp = true;
                button.setEnabled(true);
                Log.d("VOICE_PLACE", "TTS DOES NOT WORKS: OK:");
            } else {
                this.isTextToSpeechUp = false;
                button.setEnabled(false);
                Log.d("VOICE_PLACE", "TTS DOES NOT WORKS: STATUS: " + Integer.toString(status));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void promptSpeechInput(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Speech input is not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    this.switchResult(results.get(0));
                }
                break;
            }
        }
    }

    private void switchResult(String s) {
        String path = Environment.getExternalStorageDirectory().toString() + "/Music/";

        if (s.contains("music") || s.contains("musique")) {
            try {
                File directory = new File(path);
                File[] files = directory.listFiles();

                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i].getName();
                    String[] words = s.split(" ");
                    boolean isFound = false;

                    for (int j = 0; j < words.length; j++) {
                        if (fileName.contains(words[j])) {
                            playAudio(path + fileName);
                            Log.d("VOICE_PLACE FILE", "FileName: " + fileName);
                            isFound = true;
                            break;
                        }
                    }

                    if (isFound) {
                        break;
                    }
                }
            } catch (Exception ex) {
                Log.d("VOICE_PLACE EXCEPTION", ex.getMessage());
            }
        } else {
            textToSpeech.speak("Je ne comprends pas votre demande", TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(VoiceActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }
}
