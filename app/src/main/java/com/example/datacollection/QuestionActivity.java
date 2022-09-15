package com.example.datacollection;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {


    private TextView question, questionNum;
    private RadioGroup radioGroup;
    private RadioButton option1, option2, option3, option4;
    private Button nextButton;

    int counter = 0;

    private List<QuestionModel> questionList;
    String[] answers = new String[10];

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionList = new ArrayList<>();

        question = findViewById(R.id.questionText);
        questionNum = findViewById(R.id.questionCount);
        radioGroup = findViewById(R.id.radioGroup);

        option1 = findViewById(R.id.button1);
        option2 = findViewById(R.id.button2);
        option3 = findViewById(R.id.button3);
        option4 = findViewById(R.id.button4);

        nextButton = findViewById(R.id.nextButton);

        questionList.clear();
        addQuestions();
        showNextQuestion();

        nextButton.setOnClickListener((View v) -> {
                if (counter == 6)
                    nextButton.setText("Finish");

                if (option1.isChecked() || option2.isChecked() || option3.isChecked() || option4.isChecked()) {
                    addAnswer();
                    showNextQuestion();
                }
                else {
                    Toast.makeText(QuestionActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                }
        });
    }

    private void addAnswer() {
        if (option1.isChecked()) {
            answers[counter] = "a";
        }
        else if (option2.isChecked()) {
            answers[counter] = "b";
        }
        else if (option3.isChecked()) {
            answers[counter] = "c";
        }
        else if (option4.isChecked()) {
            answers[counter] = "d";
        }
    }

    private void addQuestions() {
        questionList.add(new QuestionModel("What is your gender?", "Female", "Male", "Non-binary / third gender", "Prefer not to say"));
        questionList.add(new QuestionModel("What is your age?", "18 - 30 years old", "31 - 40 years old", "41 years or older", "Prefer not to say"));
        questionList.add(new QuestionModel("What is your current study/working status?", "Undergraduate Program", "Graduate Program", "Working", "Other"));
        questionList.add(new QuestionModel("What is the OS of your current phone?", "IOS", "Android", "Windows", "Other"));
        questionList.add(new QuestionModel("What is your dominant hand?", "Right", "Left", "Both", "I don't know"));
        questionList.add(new QuestionModel("What is your most common gesture when you swipe on your phone?", "Use dominant hand only", "Use non-dominant hand only",
                "Hold the phone with non-dominant hand and swipe with dominant hand", "Hold the phone with dominant hand and swipe with non-dominant hand"));
        questionList.add(new QuestionModel("What is your preferred method to unlock you phone?", "Complex password", "Short passcode", "Gesture pattern", "Biometrics(e.g. Fingerprints, Face ID)"));
    }


    @SuppressLint("SetTextI18n")
    private void showNextQuestion() {

        radioGroup.clearCheck();

        if (counter < 7) {
            QuestionModel currentQuestion = questionList.get(counter);
            question.setText(currentQuestion.getQuestion());
            option1.setText(currentQuestion.getOption1());
            option2.setText(currentQuestion.getOption2());
            option3.setText(currentQuestion.getOption3());
            option4.setText(currentQuestion.getOption4());

            counter++;
            questionNum.setText("Question: " + counter + "/7");
        }
        else {
            Toast.makeText(QuestionActivity.this, "Answers saved", Toast.LENGTH_SHORT).show();

            // switch to next activity
            Bundle b = new Bundle();
            b.putStringArray("answers", answers);

            Intent intent = new Intent(QuestionActivity.this, SwipeActivity.class);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

}