package ru.goodibunakov.cartoonquiz;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class MainActivityFragment extends Fragment {

    private static final String TAG = "CartoonQuiz Activity";
    private static final int CARTOONS_IN_QUIZ = 10;

    private Unbinder unbinder;

    private List<String> fileNameList;
    private List<String> quizCountriesList;
    private List<String> quizRegionsList;
    private Set<String> regionsSet;
    private int correctAnswers;
    private String correctAnswer;
    private int totalGuesses;
    private SecureRandom random;
    private Handler handler;
    private List<ImageView> buttons;
    private List<String> customButtonsName;
    private HashMap<ImageView, String> buttonsImage;

    @BindView(R.id.question_number)
    ImageView questionNumber;
    @BindView(R.id.cartoon_image)
    ImageView cartoonImage;
    @BindView(R.id.btn1)
    ImageView btn1;
    @BindView(R.id.btn2)
    ImageView btn2;
    @BindView(R.id.btn3)
    ImageView btn3;
    @BindView(R.id.btn4)
    ImageView btn4;
    @BindView(R.id.quiz_relative_layout)
    RelativeLayout relativeLayout;


    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        regionsSet = new HashSet<>(Arrays.asList(Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.regions_list)));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        fileNameList = new ArrayList<>();
        quizCountriesList = new ArrayList<>();
        quizRegionsList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();
        buttons = Arrays.asList(btn1, btn2, btn3, btn4);
        customButtonsName = new ArrayList<>();
        buttonsImage = new HashMap<>();

        questionNumber.setImageResource(R.drawable.q01);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    void resetQuiz() {
        AssetManager assets = Objects.requireNonNull(getActivity()).getAssets();
        fileNameList.clear();
        try {
            for (String region : regionsSet) {
                String[] paths = assets.list(region);

                if (paths != null) {
                    for (String path : paths) {
                        fileNameList.add(path.replace(".png", ""));
                    }
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "Ошибка получения имен файлов", e);
        }

        correctAnswers = 0;
        totalGuesses = 0;
        quizCountriesList.clear();
        quizRegionsList.clear();
        buttonsImage.clear();

        int flagCounter = 1;
        int flagCount = fileNameList.size();
        Log.d("debug", "flagCount " + flagCount);
        while (flagCounter <= CARTOONS_IN_QUIZ) {
            int randomIndex = random.nextInt(flagCount);
            Log.d("debug", "randomIndex " + randomIndex);
            Log.d("debug", "flagCounter " + flagCounter);

            String fileName = fileNameList.get(randomIndex);
            String quizRegion = String.valueOf(fileName.subSequence(0, fileName.lastIndexOf('-')));
            if (!quizRegionsList.contains(quizRegion)) {
                quizCountriesList.add(fileName);
                quizRegionsList.add(quizRegion);
                ++flagCounter;
            }
        }

        for (int i = 0; i < quizCountriesList.size(); i++) {
            Log.d("debug", i + " " + quizCountriesList.get(i));
        }
        loadNextFlag();
    }

    private void loadNextFlag() {
        String nextImage = quizCountriesList.remove(0);
        correctAnswer = nextImage;

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

        String region = nextImage.substring(0, nextImage.indexOf('-'));
        Log.d("debug", "region = " + region);

        AssetManager assets = Objects.requireNonNull(getActivity()).getAssets();

        try (InputStream stream = assets.open(region + "/" + nextImage + ".png")) {
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            cartoonImage.setImageDrawable(flag);

            animate(false);
        } catch (IOException e) {
            Log.e(TAG, "Ошибка загрузки " + nextImage, e);
        }

        Collections.shuffle(fileNameList);

        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        enableButtons();

        customButtonsName.clear();
        int k = 0;
        while (customButtonsName.size() <= 3) {
            String customButtonName = fileNameList.get(random.nextInt(fileNameList.size()));
            if (customButtonsName.contains(customButtonName) || customButtonName.contentEquals(correctAnswer.subSequence(0, correctAnswer.lastIndexOf('-'))))
                continue;
            customButtonsName.add(customButtonName);
            customButtonName = fileNameList.get(random.nextInt(fileNameList.size()));

            int lastIndex = customButtonName.lastIndexOf('-');
            String nameButton = customButtonName.substring(0, lastIndex);
            String customButtonSelector = "selector_" + nameButton.toLowerCase();
            ImageView imageView = buttons.get(k);
            int imageResource = getResId(customButtonSelector);
            imageView.setImageResource(imageResource);
            buttonsImage.put(imageView, nameButton);
            k++;
            Log.d("debug", "customButtonSelector = " + customButtonSelector);
            Log.d("debug", "getResId = " + getResId(customButtonSelector));
        }

        int correctAnswerButtonNumber = random.nextInt(4);
        int lastIndex = correctAnswer.lastIndexOf('-');
        String nameButtonCorrect = correctAnswer.substring(0, lastIndex);
        String correctButtonSelector = "selector_" + nameButtonCorrect.toLowerCase();
        Log.d("debug", "correctAnswer " + correctAnswer);
        Log.d("debug", "correctButtonSelector " + correctButtonSelector);
        int imageResourceCorrect = getResId(correctButtonSelector);
        ImageView imageViewCorrect = buttons.get(correctAnswerButtonNumber);
        imageViewCorrect.setImageResource(imageResourceCorrect);
        buttonsImage.put(imageViewCorrect, nameButtonCorrect);
    }

    private void enableButtons() {
        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btn3.setEnabled(true);
        btn4.setEnabled(true);
    }

    private void disableButtons() {
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btn4.setEnabled(false);
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
                        loadNextFlag();
                    }
                });
            } else {
                animator = ViewAnimationUtils.createCircularReveal(relativeLayout, centerX, centerY, 0, radius);
            }
            animator.setDuration(700);
            animator.start();
        } else {
            loadNextFlag();
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

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4})
    void onClick(View view) {
        ImageView imageView = (ImageView) view;
        ++totalGuesses;
        String rightAnswer = String.valueOf(correctAnswer.subSequence(0, correctAnswer.lastIndexOf('-')));
        String nameOfClickedButton = "";
        if (buttonsImage.containsKey(imageView)) {
            nameOfClickedButton = buttonsImage.get(imageView);
        }
        if (nameOfClickedButton != null) {
            Log.d("debug", "nameOfClickedButton " + nameOfClickedButton);
            Log.d("debug", "rightAnswer " + rightAnswer);
            if (nameOfClickedButton.equals(rightAnswer)) {
                ++correctAnswers;
                Log.d("debug", "dfdfdfdf " + "btn_yes_" + nameOfClickedButton);
                imageView.setImageResource(getResId("btn_yes__" + nameOfClickedButton));
                disableButtons();

                if (correctAnswers == CARTOONS_IN_QUIZ) {
                    GameComplitedDialog gameComplitedDialog = new GameComplitedDialog(Objects.requireNonNull(getActivity()));
                    Objects.requireNonNull(gameComplitedDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    gameComplitedDialog.show();
                } else {
                    handler.postDelayed(
                            () -> animate(true), 1200);
                }
            } else {
                ((ImageView) view).setImageResource(getResId("btn_disabled__" + nameOfClickedButton));
                imageView.setEnabled(false); // disable incorrect answer
            }
        }
    }}