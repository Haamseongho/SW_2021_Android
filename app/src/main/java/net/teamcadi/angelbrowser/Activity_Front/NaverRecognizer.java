package net.teamcadi.angelbrowser.Activity_Front;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionException;
import com.naver.speech.clientapi.SpeechRecognitionListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.naver.speech.clientapi.SpeechRecognizer;

import net.teamcadi.angelbrowser.R;

/**
 * Created by haams on 2017-12-11.
 */

public class NaverRecognizer implements SpeechRecognitionListener {

    // 2. SpeechRecognitionListener 를 상속한 클래스

    private final static String TAG = net.teamcadi.angelbrowser.Activity_Front.NaverRecognizer.class.getSimpleName();
    private Handler mHandler;
    private SpeechRecognizer mRecognizer;

    public NaverRecognizer(Context context, Handler handler, String clientId) {
        this.mHandler = handler;
        try {
            mRecognizer = new SpeechRecognizer(context, clientId);
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
        mRecognizer.setSpeechRecognitionListener(this);
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return mRecognizer;
    }

    public void recognize() {
        try {
            mRecognizer.recognize(new SpeechConfig(SpeechConfig.LanguageType.KOREAN, SpeechConfig.EndPointDetectType.AUTO));
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
    }

    @Override
    @WorkerThread
    public void onInactive() {
        Message msg = Message.obtain(mHandler, R.id.clientInactive);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onReady() {
        Message msg = Message.obtain(mHandler, R.id.clientReady);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onRecord(short[] speech) {
        Message msg = Message.obtain(mHandler, R.id.audioRecording, speech);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onPartialResult(String result) {
        Message msg = Message.obtain(mHandler, R.id.partialResult, result);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onEndPointDetected() {
        Log.d(TAG, "Event occurred : EndPointDetected");
    }

    @Override
    @WorkerThread
    public void onResult(SpeechRecognitionResult result) {
        Message msg = Message.obtain(mHandler, R.id.finalResult, result);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onError(int errorCode) {
        Message msg = Message.obtain(mHandler, R.id.recognitionError, errorCode);
        msg.sendToTarget();
    }

    @Override
    @WorkerThread
    public void onEndPointDetectTypeSelected(SpeechConfig.EndPointDetectType epdType) {
        Message msg = Message.obtain(mHandler, R.id.endPointDetectTypeSelected, epdType);
        msg.sendToTarget();
    }

}
