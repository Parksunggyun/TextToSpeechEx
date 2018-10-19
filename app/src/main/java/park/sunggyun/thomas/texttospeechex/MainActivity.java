package park.sunggyun.thomas.texttospeechex;

import android.annotation.TargetApi;
import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import in.codeshuffle.typewriterview.TypeWriterView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import park.sunggyun.thomas.texttospeechex.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    TextToSpeech mTextToSpeech;

    TypeWriterView twViews[];

    private static final String TAG = "MainActivity";

    final String utteranceId = "continue";
    private int mCount = 0;


    private final String[] speaks = {
            "1.  잠드는 시간과 기상 시간을 규칙적으로 합니다.",
            "2.  잠자리에 누워서 책을 보거나 TV를 보지 마세요.",
            "3.  잠들지 않은 상태로 오래 누워있지 마세요.",
            "4.  수면제의 장기적인 사용을 피하세요.",
            "5.  과도한 스트레스와 긴장을 피합니다.",
            "6.  잠자기 전 과도한 음식섭취를 피하고 수면 중 갈증을 느끼지 않을 정도의 수분섭취를 합니다.",
            "7.  낮에 적당한 운동은 좋지만 밤에 하는 운은 수면에 좋지 않습니다.",
            "8.  낮잠은 되도록 자지 않습니다.",
            "9.  잠자리는 최대한 안락하게 소음을 피하고 어둡게 합니다.",
            "10. 카페인, 술, 담배는 피합니다. 술은 일시적으로 졸음을 증가시킬 뿐 아침에 일찍 일어나게 합니다."
    };


    private String[] texts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        hide(this);
        texts = new String[]{
                getString(R.string.meant1),
                getString(R.string.meant2),
                getString(R.string.meant3),
                getString(R.string.meant4),
                getString(R.string.meant5),
                getString(R.string.meant6),
                getString(R.string.meant7),
                getString(R.string.meant8),
                getString(R.string.meant9),
                getString(R.string.meant10)

        };
        twViews = new TypeWriterView[]{binding.meant1Tv, binding.meant2Tv, binding.meant3Tv, binding.meant4Tv, binding.meant5Tv, binding.meant6Tv, binding.meant7Tv, binding.meant8Tv, binding.meant9Tv, binding.meant10Tv};
        for (TypeWriterView twView : twViews) {
            twView.setDelay(100);
            twView.setWithMusic(false);
        }
        mTextToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                mTextToSpeech.setLanguage(Locale.KOREAN);
                callTTS();
            }
        }
                , "com.google.android.tts");

    }

    public void over21(String text) {
        Voice voice = new Voice("ko-kr-x-ism#female_3-local", Locale.KOREAN, Voice.QUALITY_VERY_LOW, Voice.LATENCY_VERY_LOW, false, null);
        mTextToSpeech.setVoice(voice);
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }


    private void commandmentsStartTTS() {
        Log.d("start", "ommandmentsStartTTS()");
        try {
            if (mTextToSpeech != null) {
                String introMeant = speaks[mCount];
                over21(introMeant);
                mCount++;

                mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        Log.i(TAG, "onStart: " + utteranceId);

                        Log.d("flow1", "flow1 = " + mCount);

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (utteranceId.equals("continue") && mCount < 10 && mCount >= 0) {
                            Log.d("flow5", "flow5 = " + mCount);
                            updateText(mCount);
                            callTTS();
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.i(TAG, "onError: ");
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTextToSpeech.stop();
        mTextToSpeech.shutdown();

    }

    Handler handler = new Handler();

    public void callTTS() {
        handler.postDelayed(() -> runOnUiThread(() -> {

                    twViews[mCount].animateText(texts[mCount]);
                    commandmentsStartTTS();
                })
                , 500);
    }

    public void updateText(final int count) {
        handler.post(() -> runOnUiThread(() -> {
            twViews[count - 1].removeAnimation();
        }));
    }


    public static void hide(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("Is on?", "Turning immersive mode mode off. ");
        } else {
            Log.i("Is on?", "Turning immersive mode mode on.");
        }
// 몰입 모드를 꼭 적용해야 한다면 아래의 3가지 속성을 모두 적용시켜야 합니다
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    public SpannableStringBuilder setBold(int count, int src, int dst) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(texts[count]);
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), src, dst, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

}
