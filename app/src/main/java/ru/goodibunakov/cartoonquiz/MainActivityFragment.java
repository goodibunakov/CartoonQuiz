package ru.goodibunakov.cartoonquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Unbinder;

import static android.content.Context.AUDIO_SERVICE;


public class MainActivityFragment extends Fragment implements LoadingTaskFinishedListener {

    private static final String TAG = "cartoon";
    private static final int CARTOONS_IN_QUIZ = 10;

    private Unbinder unbinder;

    private static ArrayList<String> fileNameList;
    private static List<String> quizCartoonsList;
    private static List<String> quizCartoonsFoldersList;
    private int correctAnswers;
    private String correctAnswer;
    private int totalGuesses;
    private SecureRandom random;
    private Handler handler;
    private List<TextView> buttons;
    private List<String> customButtonsName;
    private HashMap<TextView, String> buttonsText;
    private SoundPool soundPool;
    private int soundIDYes, soundIDNo;
    private boolean loaded = false;
    private float volume;
    private final int MAX_STREAMS = 5;
    private Bundle bundle;
    private String multFolder;

    private Animation in, out;

    @BindView(R.id.question_number)
    ImageView questionNumber;
    @BindView(R.id.cartoon_image)
    ImageView cartoonImage;
    @BindView(R.id.btn1_text)
    TextView btn1;
    @BindView(R.id.btn2_text)
    TextView btn2;
    @BindView(R.id.btn3_text)
    TextView btn3;
    @BindView(R.id.btn4_text)
    TextView btn4;
    @BindView(R.id.btn1_layout)
    FrameLayout btn1_layout;
    @BindView(R.id.btn2_layout)
    FrameLayout btn2_layout;
    @BindView(R.id.btn3_layout)
    FrameLayout btn3_layout;
    @BindView(R.id.btn4_layout)
    FrameLayout btn4_layout;
    @BindView(R.id.quiz_relative_layout)
    RelativeLayout relativeLayout;


    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        in = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
        out = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("correctAnswer", correctAnswer);
//        outState.putInt("correctAnswers", correctAnswers);
//        outState.putString("multFolder", multFolder);
//        outState.putStringArrayList("fileNameList", fileNameList);
//        outState.putInt("btn1_drawable", (Integer) btn1.getTag());
//        outState.putIntArray("btn1_state", btn1.getDrawableState());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("debug", "oncreateview fragment");
        bundle = savedInstanceState;

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        fileNameList = new ArrayList<>();
        quizCartoonsList = new ArrayList<>();
        quizCartoonsFoldersList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();
        buttons = Arrays.asList(btn1, btn2, btn3, btn4);
        customButtonsName = new ArrayList<>();
        buttonsText = new HashMap<>();

        AudioManager audioManager = (AudioManager) Objects.requireNonNull(getActivity()).getSystemService(AUDIO_SERVICE);
        // Current volume Index of particular stream type.
        float currentVolumeIndex = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // Get the maximum volume index for a particular stream type.
        float maxVolumeIndex = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        // Volume (0 --> 1)
        volume = currentVolumeIndex / maxVolumeIndex;

        // Suggests an audio stream whose volume should be changed by
        // the hardware volume controls.
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // For Android SDK >= 21
        if (Build.VERSION.SDK_INT >= 21) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> loaded = true);
        soundIDYes = soundPool.load(getActivity(), R.raw.yes, 1);
        soundIDNo = soundPool.load(getActivity(), R.raw.no, 1);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("debug", "onresume fragment");
//        if (bundle != null) {
//            correctAnswer = bundle.getString("correctAnswer");
//            correctAnswers = bundle.getInt("correctAnswers");
//            switch (correctAnswers) {
//                case 0:
//                    questionNumber.setImageResource(R.drawable.q01);
//                    break;
//                case 1:
//                    questionNumber.setImageResource(R.drawable.q02);
//                    break;
//                case 2:
//                    questionNumber.setImageResource(R.drawable.q03);
//                    break;
//                case 3:
//                    questionNumber.setImageResource(R.drawable.q04);
//                    break;
//                case 4:
//                    questionNumber.setImageResource(R.drawable.q05);
//                    break;
//                case 5:
//                    questionNumber.setImageResource(R.drawable.q06);
//                    break;
//                case 6:
//                    questionNumber.setImageResource(R.drawable.q07);
//                    break;
//                case 7:
//                    questionNumber.setImageResource(R.drawable.q08);
//                    break;
//                case 8:
//                    questionNumber.setImageResource(R.drawable.q09);
//                    break;
//                case 9:
//                    questionNumber.setImageResource(R.drawable.q10);
//                    break;
//            }
//
//            multFolder = bundle.getString("multFolder");
//            AssetManager assets = Objects.requireNonNull(getActivity()).getAssets();
//
//            try (InputStream stream = assets.open(multFolder + "/" + correctAnswer + ".png")) {
//                Drawable mult = Drawable.createFromStream(stream, correctAnswer);
//                cartoonImage.setImageDrawable(mult);
//            } catch (IOException e) {
//                Log.e(TAG, "Ошибка загрузки " + correctAnswer, e);
//            }
//
//            fileNameList = bundle.getStringArrayList("fileNameList");
//
//            btn1.setImageResource(bundle.getInt("btn1_drawable"));
//            int[] btn1ImageState = bundle.getIntArray("btn1_state");
//            btn1.setImageState(btn1ImageState, false);
//            if (btn1ImageState != null && btn1ImageState.length == 2){
//                btn1.setEnabled(true);
//            } else {
//                btn1.setEnabled(false);
//            }
//            Log.d("debug", "bundle.getIntArray(\"btn1_state\") " + Arrays.toString(bundle.getIntArray("btn1_state")));
//        } else {
        resetQuiz();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    private void resetQuiz() {
        fileNameList.clear();
        correctAnswers = 0;
        totalGuesses = 0;
        quizCartoonsList.clear();
        quizCartoonsFoldersList.clear();
        buttonsText.clear();
        new ImagesForQuiz(getActivity(), MainActivityFragment.this).execute();
    }

    @OnTouch({R.id.btn1_layout, R.id.btn2_layout, R.id.btn3_layout, R.id.btn4_layout})
    boolean onTouch(View v, MotionEvent event) {
        if (v.isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.startAnimation(out);
                    break;

                case MotionEvent.ACTION_UP:
                    v.startAnimation(in);
                    v.clearAnimation();
                    break;
            }
        }
        return false;
    }

    private void initArrays(Result result) {
        fileNameList = result.fileNameListBackground;
        quizCartoonsList = result.quizCartoonsListBackground;
        quizCartoonsFoldersList = result.quizCartoonsFoldersListBackground;

        loadNextMult();
    }

    private void loadNextMult() {
        resetImageButtons();
        correctAnswer = quizCartoonsList.remove(0);

        switch (correctAnswers) {
            case 0:
                questionNumber.setImageResource(R.drawable.q01);
                break;
            case 1:
                questionNumber.setImageResource(R.drawable.q02);
                break;
            case 2:
                questionNumber.setImageResource(R.drawable.q03);
                break;
            case 3:
                questionNumber.setImageResource(R.drawable.q04);
                break;
            case 4:
                questionNumber.setImageResource(R.drawable.q05);
                break;
            case 5:
                questionNumber.setImageResource(R.drawable.q06);
                break;
            case 6:
                questionNumber.setImageResource(R.drawable.q07);
                break;
            case 7:
                questionNumber.setImageResource(R.drawable.q08);
                break;
            case 8:
                questionNumber.setImageResource(R.drawable.q09);
                break;
            case 9:
                questionNumber.setImageResource(R.drawable.q10);
                break;
        }

        multFolder = correctAnswer.substring(0, correctAnswer.indexOf('-'));
        Log.d("debug", "multFolder = " + multFolder);

        AssetManager assets = Objects.requireNonNull(getActivity()).getAssets();

        try (InputStream stream = assets.open(multFolder + "/" + correctAnswer + ".png")) {
            Drawable mult = Drawable.createFromStream(stream, correctAnswer);
            cartoonImage.setImageDrawable(mult);
            animate(false);
        } catch (IOException e) {
            Log.e(TAG, "Ошибка загрузки " + correctAnswer, e);
        }

        Collections.shuffle(fileNameList);

        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        enableButtons();

        customButtonsName.clear();
        int k = 0;
        while (customButtonsName.size() <= 3) {
            String customButtonName = fileNameList.get(random.nextInt(fileNameList.size()));

            int lastIndex = customButtonName.lastIndexOf('-');
            String nameButton = customButtonName.substring(0, lastIndex);

            if (customButtonsName.contains(nameButton) || nameButton.contains(this.correctAnswer.substring(0, this.correctAnswer.lastIndexOf('-')))) {
                continue;
            }

            customButtonsName.add(nameButton);
//            String customButtonSelector = "selector_" + nameButton.toLowerCase();
            TextView textView = buttons.get(k);
//            int imageResource = getResId(customButtonSelector);
//            imageView.setImageResource(imageResource);
//            imageView.setTag(imageResource);
            buttonsText.put(textView, nameButton);
            k++;
        }

        int correctAnswerButtonNumber = random.nextInt(4);
        int lastIndex = correctAnswer.lastIndexOf('-');
        String nameButtonCorrect = correctAnswer.substring(0, lastIndex);
//        String correctButtonSelector = "selector_" + nameButtonCorrect.toLowerCase();
//        int imageResourceCorrect = getResId(correctButtonSelector);
        TextView textViewCorrect = buttons.get(correctAnswerButtonNumber);
        textViewCorrect.setText("");
//        imageViewCorrect.setImageResource(imageResourceCorrect);
//        imageViewCorrect.setTag(imageResourceCorrect);
        buttonsText.put(textViewCorrect, nameButtonCorrect);
    }

    private void enableButtons() {
        disable(btn1_layout, false);
        disable(btn2_layout, false);
        disable(btn3_layout, false);
        disable(btn4_layout, false);
    }

    private void disableButtons() {
        disable(btn1_layout, true);
        disable(btn2_layout, true);
        disable(btn3_layout, true);
        disable(btn4_layout, true);
    }

    private void animate(boolean animateOut) {
        if (correctAnswers == 0) {
            return;
        }

        int centerX = (relativeLayout.getLeft() + relativeLayout.getRight()) / 2;
        int centerY = (relativeLayout.getTop() + relativeLayout.getBottom()) / 2;

        int radius = Math.max(relativeLayout.getWidth(), relativeLayout.getHeight());

        Animator animator;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (animateOut) {
                animator = ViewAnimationUtils.createCircularReveal(relativeLayout, centerX, centerY, radius, 0);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadNextMult();
                    }
                });
            } else {
                animator = ViewAnimationUtils.createCircularReveal(relativeLayout, centerX, centerY, 0, radius);
            }
            animator.setDuration(700);
            animator.start();
        } else {
            loadNextMult();
        }
    }

    private static int getResId(String resName) {
        try {
            Field idField = R.drawable.class.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    int getTotalGuesses() {
        return totalGuesses;
    }

    @OnClick({R.id.btn1_layout, R.id.btn2_layout, R.id.btn3_layout, R.id.btn4_layout})
    void onClick(FrameLayout view) {
        TextView textView;
        switch (view.getId()) {
            case R.id.btn1_layout:
                textView = btn1;
                break;
            case R.id.btn2_layout:
                textView = btn2;
                break;
            case R.id.btn3_layout:
                textView = btn3;
                break;
            default:
                textView = btn4;
                break;
        }
        ++totalGuesses;
        String rightAnswer = String.valueOf(correctAnswer.subSequence(0, correctAnswer.lastIndexOf('-')));
        String nameOfClickedButton = "";
        if (buttonsText.containsKey(textView)) {
            nameOfClickedButton = buttonsText.get(textView);
        }
        if (nameOfClickedButton != null) {
            Log.d("debug", "nameOfClickedButton " + nameOfClickedButton);
            Log.d("debug", "rightAnswer " + rightAnswer);
            if (nameOfClickedButton.equals(rightAnswer)) {
                playSound(soundIDYes);
                ++correctAnswers;
                Log.d("debug", "dfdfdfdf " + "btn_yes_" + nameOfClickedButton);
                setYesImage(view);
                textView.setText("угадал блин");

                disableButtons();

                if (correctAnswers == CARTOONS_IN_QUIZ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Игра закончена")
                            .setMessage("ура!")
                            .setCancelable(false)
                            .setPositiveButton("Заново", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    totalGuesses = 0;
                                    resetQuiz();
                                }
                            });
                    AlertDialog alert = builder.create();
                    //Set the dialog to not focusable (makes navigation ignore us adding the window)
                    Objects.requireNonNull(alert.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    //Show the dialog!
                    alert.show();
                    //Set the dialog to immersive
                    alert.getWindow().getDecorView().setSystemUiVisibility(
                            Objects.requireNonNull(getActivity()).getWindow().getDecorView().getSystemUiVisibility());
                    //Clear the not focusable flag from the window
                    alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                } else {
                    handler.postDelayed(
                            () -> animate(true), 1200);
                }
            } else {
                playSound(soundIDNo);
                disable(view, true);
//                ((ImageView) view).setImageResource(getResId("btn_disabled__" + nameOfClickedButton));
//                imageView.setEnabled(false); // disable incorrect answer
            }
        }
    }


    private static class ImagesForQuiz extends AsyncTask<Void, Void, Result> {


        private final WeakReference<Activity> weakActivity;
        private final LoadingTaskFinishedListener finishedListener;
        private AlertDialog alert;

        ImagesForQuiz(Activity activity, LoadingTaskFinishedListener finishedListener) {
            weakActivity = new WeakReference<>(activity);
            this.finishedListener = finishedListener;
        }

        @SuppressLint("InflateParams")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Activity activity = weakActivity.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(activity.getLayoutInflater().inflate(R.layout.dialog_loading, null))
                    .setCancelable(false);
            alert = builder.create();
            Objects.requireNonNull(alert.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //Set the dialog to not focusable (makes navigation ignore us adding the window)
            Objects.requireNonNull(alert.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            //Set the dialog to immersive
            alert.show();
            alert.getWindow().getDecorView().setSystemUiVisibility(
                    Objects.requireNonNull(weakActivity.get()).getWindow().getDecorView().getSystemUiVisibility());
            //Clear the not focusable flag from the window
            alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            Activity activity = weakActivity.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
            if (alert != null) alert.dismiss();
            finishedListener.onTaskFinished(result);
        }

        @Override
        protected Result doInBackground(Void... voids) {
            ArrayList<String> fileNameListBackground = new ArrayList<>();
            List<String> quizCartoonsListBackground = new ArrayList<>();
            List<String> quizCartoonsFoldersListBackground = new ArrayList<>();

            Set<String> regionsSet = new HashSet<>(Arrays.asList(Objects.requireNonNull(weakActivity.get()).getResources().getStringArray(R.array.cartoons_list)));

            AssetManager assets = Objects.requireNonNull(weakActivity.get()).getAssets();

            try {
                for (String region : regionsSet) {
                    String[] paths = assets.list(region);

                    if (paths != null) {
                        for (String path : paths) {
                            fileNameListBackground.add(path.replace(".png", ""));
                        }
                    }
                }

            } catch (IOException e) {
                Log.e(TAG, "Ошибка получения имен файлов", e);
            }

            int multCounter = 1;
            int multCount = fileNameListBackground.size();
            Log.d("debug", "multCount " + multCount);
            SecureRandom random = new SecureRandom();
            while (multCounter <= CARTOONS_IN_QUIZ) {
                int randomIndex = random.nextInt(multCount);
                String fileName = fileNameListBackground.get(randomIndex);
                String quizRegion = String.valueOf(fileName.subSequence(0, fileName.lastIndexOf('-')));
                if (!quizCartoonsFoldersListBackground.contains(quizRegion)) {
                    quizCartoonsListBackground.add(fileName);
                    quizCartoonsFoldersListBackground.add(quizRegion);
                    ++multCounter;
                }
            }
//            for (int i = 0; i < quizCartoonsListBackground.size(); i++){
//                Log.d("debug", "quizCartoonsListBackground " + i + " = " + quizCartoonsListBackground.get(i));
//            }
//            for (int i = 0; i < quizCartoonsFoldersListBackground.size(); i++){
//                Log.d("debug", "quizCartoonsFoldersListBackground " + i + " = " + quizCartoonsFoldersListBackground.get(i));
//            }
//            for (int i = 0; i < fileNameListBackground.size(); i++){
//                Log.d("debug", "fileNameListBackground " + i + " = " + fileNameListBackground.get(i));
//            }
            return new Result(fileNameListBackground, quizCartoonsListBackground, quizCartoonsFoldersListBackground);
        }
    }

    static class Result {
        ArrayList<String> fileNameListBackground;
        List<String> quizCartoonsListBackground;
        List<String> quizCartoonsFoldersListBackground;

        Result(ArrayList<String> fileNameListBackground, List<String> quizCartoonsListBackground, List<String> quizCartoonsFoldersListBackground) {
            this.fileNameListBackground = fileNameListBackground;
            this.quizCartoonsListBackground = quizCartoonsListBackground;
            this.quizCartoonsFoldersListBackground = quizCartoonsFoldersListBackground;
        }

    }

    @Override
    public void onTaskFinished(Result result) {
        initArrays(result);
    }

    private void playSound(int soundID) {
        if (loaded) {
            soundPool.play(soundID, volume, volume, 1, 0, 1f);
        }
    }

    private void disable(FrameLayout layout, boolean isDisable) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            child.setEnabled(!isDisable);
        }
        layout.setEnabled(!isDisable);
    }

    private void setYesImage(FrameLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ImageView)
                ((ImageView) child).setImageResource(R.drawable.btn_yes);
        }
    }

    private void resetImageButtons() {
        for (int i = 0; i < btn1_layout.getChildCount(); i++) {
            View child = btn1_layout.getChildAt(i);
            if (child instanceof ImageView)
                ((ImageView) child).setImageResource(R.drawable.btn_selector);
        }
        for (int i = 0; i < btn2_layout.getChildCount(); i++) {
            View child = btn2_layout.getChildAt(i);
            if (child instanceof ImageView)
                ((ImageView) child).setImageResource(R.drawable.btn_selector);
        }
        for (int i = 0; i < btn3_layout.getChildCount(); i++) {
            View child = btn3_layout.getChildAt(i);
            if (child instanceof ImageView)
                ((ImageView) child).setImageResource(R.drawable.btn_selector);
        }
        for (int i = 0; i < btn4_layout.getChildCount(); i++) {
            View child = btn4_layout.getChildAt(i);
            if (child instanceof ImageView)
                ((ImageView) child).setImageResource(R.drawable.btn_selector);
        }
    }
}