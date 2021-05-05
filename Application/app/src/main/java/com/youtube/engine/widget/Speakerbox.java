package com.youtube.engine.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Speakerbox implements TextToSpeech.OnInitListener {
    final static String TAG = Speakerbox.class.getSimpleName();

    /**
     * Pitch when we have focus
     */
    private static final float FOCUS_PITCH = 1.0f;
    /**
     * Pitch when we should duck audio for another app
     */
    private static final float DUCK_PITCH = 0.5f;

    private final TextToSpeech textToSpeech;

    private final Application application;

    /**
     * Callbacks are registered for upon initialization. Set an activity on the Speakerbox
     * object to have this class take care of shutting down the TextToSpeech object or register
     * for {@link android.app.Application.ActivityLifecycleCallbacks} in your application and
     */
    private final Application.ActivityLifecycleCallbacks callbacks;

    /**
     * If set, this class will shut itself down when the activity is destroyed. Only set an
     * activity if you want the Speakerbox's state to be tied to the activity lifecycle
     */
    private Activity activity = null;

    private boolean initialized = false;
    private boolean muted = false;
    private String playOnInit = null;
    private int queueMode = TextToSpeech.QUEUE_FLUSH;

    private final LinkedHashMap<String, String> samples = new LinkedHashMap<String, String>();
    private final ArrayList<String> unwantedPhrases = new ArrayList<String>();

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    textToSpeech.setPitch(FOCUS_PITCH);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    textToSpeech.setPitch(DUCK_PITCH);
                    break;
            }
        }
    };

    public Speakerbox(final Application application) {
        this.application = application;
        this.textToSpeech = new TextToSpeech(application, this);
        this.callbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (Speakerbox.this.activity == activity) {
                    shutdown();
                }
            }
        };
        application.registerActivityLifecycleCallbacks(callbacks);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true;
            if (playOnInit != null) {
                playInternal(playOnInit);
            }
        } else {
            Log.e(TAG, "Initialization failed.");
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        enableVolumeControl(this.activity);
    }

    public Application.ActivityLifecycleCallbacks getCallbacks() {
        return callbacks;
    }

    public void play(CharSequence text) {
        play(text.toString());
    }

    public void play(String text) {
        if(doesNotContainUnwantedPhrase(text)) {
            text = applyRemixes(text);
            if (initialized) {
                playInternal(text);
            } else {
                playOnInit = text;
            }
        }
    }

    public void stop() {
        textToSpeech.stop();
    }

    private String applyRemixes(String text) {
        for (String key : samples.keySet()) {
            if (text.contains(key)) {
                text = text.replace(key, samples.get(key));
            }
        }

        return text;
    }

    private void playInternal(String text) {
        if (!muted) {
            Log.d(TAG, "Playing: \""+ text + "\"");
            textToSpeech.speak(text, queueMode, null);
        }
    }

    public void dontPlayIfContains(String text) {
        unwantedPhrases.add(text);
    }

    private boolean doesNotContainUnwantedPhrase(String text){
        for(String invalid : unwantedPhrases) {
            if(text.contains(invalid)) {
                return false;
            }
        }
        return true;
    }

    public void mute() {
        muted = true;
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    public void unmute() {
        muted = false;
    }

    public boolean isMuted() {
        return muted;
    }

    public void remix(String original, String remix) {
        samples.put(original, remix);
    }

    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    public void requestAudioFocus() {
        final AudioManager am = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
    }

    public void abandonAudioFocus() {
        AudioManager am = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(audioFocusChangeListener);
    }

    public void enableVolumeControl(Activity activity) {
        if (activity != null) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    public void disableVolumeControl(Activity activity) {
        if (activity != null) {
            activity.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }
    }

    public void setQueueMode(int queueMode) {
        this.queueMode = queueMode;
    }

    /**
     * Shutdown the {@link TextToSpeech} object and unregister activity lifecycle callbacks
     */
    public void shutdown() {
        textToSpeech.shutdown();
        application.unregisterActivityLifecycleCallbacks(callbacks);
    }
}
