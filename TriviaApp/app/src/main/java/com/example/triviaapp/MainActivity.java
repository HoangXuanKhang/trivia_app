package com.example.triviaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.triviaapp.data.AnswerListAsyncResponse;
import com.example.triviaapp.data.QuestionBank;
import com.example.triviaapp.model.Question;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView counterTextView,questionTextView,countScoreTextView,countHighScoreTextView;
    private ImageButton nextButton,prevButton;
    private Button trueButton,falseButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private CardView cardView;
    private int score = 0, highScore = 0;
    private static final String HIGH_SCORE = "high score";
    private SharedPreferences sharedPreferences2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counterTextView =  findViewById(R.id.couter_text);
        questionTextView = findViewById(R.id.question_text);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        nextButton =  findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        cardView = findViewById(R.id.cardView);
        countScoreTextView =  findViewById(R.id.countScore_textView);
        countHighScoreTextView = findViewById(R.id.countHightScore);

        sharedPreferences2 = getSharedPreferences("CURRENT_INDEX",MODE_PRIVATE);

        SharedPreferences sharedPreferences3 = getSharedPreferences("CURRENT_INDEX",MODE_PRIVATE);
        currentQuestionIndex = sharedPreferences3.getInt("current_index",0);
        Log.d("TAG", "onCreate: " +currentQuestionIndex);

         questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinnish(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                counterTextView.setText(currentQuestionIndex + " / " + questionArrayList.size());
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(HIGH_SCORE,MODE_PRIVATE);
        highScore = sharedPreferences.getInt("high_score",0);
        countHighScoreTextView.setText(highScore + "");
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);






    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_button:
                goNextQuestion();
                break;
            case R.id.prev_button:
                if(currentQuestionIndex > 0){
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                }
                updateQuestion();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }


    private void checkAnswer(boolean userChoose) {
        boolean answerTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        Log.d("isTRUE", "checkAnswer: " + answerTrue + "currentQuestionIndex" + currentQuestionIndex);
        if(userChoose == answerTrue){
            fadeAnimation();
            score += 10;
            countScoreTextView.setText(score + "");
            processHighScore();
        }
        else{
            shakeAnimation();
            if(score > 0){
                score -= 10;
                countScoreTextView.setText(score + "");
            }
            processHighScore();
        }
    }

    private void processHighScore() {
        if(score > highScore){
            highScore = score;
            countHighScoreTextView.setText(highScore + "");
        }
        SharedPreferences sharedPreferences = getSharedPreferences(HIGH_SCORE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("high_score",highScore);
        editor.apply();
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        counterTextView.setText(currentQuestionIndex + " / " + questionList.size());
    }

    private void shakeAnimation(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake_animation);
        cardView.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.red));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                goNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.green));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                goNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void goNextQuestion() {

        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();


//        Log.d("TAG", "goNextQuestion: " + currentQuestionIndex);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences2.edit().putInt("current_index",currentQuestionIndex).apply();
    }
}