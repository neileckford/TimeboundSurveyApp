package com.example.username.surveyapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;

public class AnswerQuestion extends AppCompatActivity implements OnRatingBarChangeListener{
    private DatabaseReference mFirebaseDatabaseReference;
    private TextView messageTextView;
    private String questionType = "question_details/";
    private static final String FORMAT = "%02d:%02d:%02d";
    private long responseTime;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    LinearLayout answerBox;
    Button submitAnswer;
    TextView mTextField;
    String message;
    SimpleDateFormat sdf=null;
    String st;
    String nameExists;

    private long timeRemaining;
    String answered;

    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        messageTextView = (TextView) findViewById(R.id.messageTextView);

        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        answerBox = (LinearLayout) findViewById(R.id.radioLayout);
        submitAnswer = new Button(this);
        mTextField = (TextView) findViewById(R.id.mTextField);
        answered = "false";

        initComponents();
    }

    private void initComponents(){
        mFirebaseDatabaseReference.child(questionType+message).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question question = dataSnapshot.getValue(Question.class);
                timeRemaining = question.getDeadline().getTime()- System.currentTimeMillis();
                MultipleChoice multipleChoice = dataSnapshot.getValue(MultipleChoice.class);
                messageTextView.setText(question.getQuestion());


                if(question.getType().equals("multiple_choice")) {
                    createMultiple(message);
                }else if(question.getType().equals("rating")){
                    createRating();
                }else{
                    openQuestion();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Answer question", "loadPost:onCancelled", databaseError.toException());
            }


        });
    }

    private void createMultiple(String message){

        mFirebaseDatabaseReference.child("question_details/"+message).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MultipleChoice multipleChoice = dataSnapshot.getValue(MultipleChoice.class);
                List<String> optionList = multipleChoice.getOptions();
                createMultipleChoice(optionList.toArray(new String[optionList.size()]));
                timerClass();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }


        });
    }

    private void createMultipleChoice(String... options) {
        int index=0;
        final RadioButton[] rb = new RadioButton[options.length];
        final RadioGroup rg = new RadioGroup(this);
        for (String r : options){
            rb[index] = new RadioButton(this);
            rg.addView(rb[index]);
            rb[index++].setText(r);
        }
        submitAnswer.setText("Respond");
        rg.addView(submitAnswer);
        answerBox.addView(rg);

        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = rg.getCheckedRadioButtonId();
                final RadioButton rb = (RadioButton) findViewById(selectedId);

                MultipleAnswer multipleAnswer = new MultipleAnswer(FirebaseAuth.getInstance().getCurrentUser().getEmail(),rb.getText().toString(),new Date(),responseTime);

                mFirebaseDatabaseReference.child("answers/"+message).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String answered = "false";
                        for (DataSnapshot answerSnapshot: snapshot.getChildren()){
                            MultipleAnswer multipleAnswer = answerSnapshot.getValue(MultipleAnswer.class);
                            if(multipleAnswer.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                answered = "true";
                            }
                        }

                        if(answered=="true"){
                            //Toast.makeText(getApplicationContext(), "Already answered this question", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            MultipleAnswer multipleAnswer = new MultipleAnswer(FirebaseAuth.getInstance().getCurrentUser().getEmail(),rb.getText().toString(),new Date(),responseTime);
                            mFirebaseDatabaseReference.child("answers/"+message).push().setValue(multipleAnswer);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

                nameExists="false";
                try{
                    mFirebaseDatabaseReference.child("respondents/").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            for (DataSnapshot userSnapshot: snapshot.getChildren()){
                                Users users = userSnapshot.getValue(Users.class);
                                if(users.getUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                    nameExists="true";
                                }
                            }


                            if(nameExists.equals("true")) {
                                Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                            }else {
                                users = new Users(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                mFirebaseDatabaseReference.child("respondents/").push().setValue(users);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });

                }catch(Exception e){
                    users = new Users(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    mFirebaseDatabaseReference.child("respondents/").push().setValue(users);
                }

            }
        });
    }

    private void createRating() {
        mFirebaseDatabaseReference.child("question_details/"+message).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Rating rating = dataSnapshot.getValue(Rating.class);
                int numStars = parseInt(rating.getStars());
                createRatingView(numStars);
                timerClass();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }


        });
    }

    public void createRatingView(int stars){
        RatingBar rBar = new RatingBar(this, null);
        rBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        rBar.setNumStars(stars);
        rBar.setStepSize(1.0f);
        LayerDrawable drawable = (LayerDrawable) rBar.getProgressDrawable();
        drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFFDEC00"), PorterDuff.Mode.SRC_ATOP);
        answerBox.addView(rBar);
        rBar.setOnRatingBarChangeListener(this);
        timerClass();
    }

    public void onRatingChanged(RatingBar rateBar, float rat,
                                boolean fromUser) {


        final float rating = rat;
        if(mTextField.getText().equals("Timeout!")){
            Toast.makeText(AnswerQuestion.this,
                    "Sorry, you can no longer reply to this question", Toast.LENGTH_SHORT).show();
        }else {
            mFirebaseDatabaseReference.child("answers/"+message).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    answered = "false";
                    for (DataSnapshot answerSnapshot: snapshot.getChildren()){
                        MultipleAnswer multipleAnswer = answerSnapshot.getValue(MultipleAnswer.class);
                        if(multipleAnswer.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                            answered = "true";
                        }
                    }


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            });
            nameExists="false";
            try{
                mFirebaseDatabaseReference.child("respondents/").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot userSnapshot: snapshot.getChildren()){
                            Users users = userSnapshot.getValue(Users.class);
                            if(users.getUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                nameExists="true";
                            }
                        }
                        if(nameExists.equals("true")) {
                            //Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                        }else {
                            users = new Users(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            mFirebaseDatabaseReference.child("respondents/").push().setValue(users);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

            }catch(Exception e){
                users = new Users(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                mFirebaseDatabaseReference.child("respondents/").push().setValue(users);
            }
            if(answered.equals("true")){
                //Toast.makeText(getApplicationContext(), "Already answered this question", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(AnswerQuestion.this,
                        "New Rating: " + rating, Toast.LENGTH_SHORT).show();
                MultipleAnswer multipleAnswer = new MultipleAnswer(FirebaseAuth.getInstance().getCurrentUser().getEmail(), Float.toString(rating), new Date(), responseTime);
                mFirebaseDatabaseReference.child("answers/" + message)
                        .push().setValue(multipleAnswer);
            }
        }
    }

    public void openQuestion(){
        final EditText textBox = new EditText(this);
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        textBox.setSingleLine(false);
        textBox.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);;
        answerBox.addView(textBox);
        answerBox.addView(submitAnswer);
        timerClass();

        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultipleAnswer multipleAnswer = new MultipleAnswer(FirebaseAuth.getInstance().getCurrentUser().getEmail(),textBox.getText().toString(),new Date(),responseTime);

                mFirebaseDatabaseReference.child("answers/"+message).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String answered = "false";
                        for (DataSnapshot answerSnapshot: snapshot.getChildren()){
                            MultipleAnswer multipleAnswer = answerSnapshot.getValue(MultipleAnswer.class);
                            if(multipleAnswer.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                answered = "true";
                            }
                        }

                        if(answered.equals("true")){
                            //Toast.makeText(getApplicationContext(), "Already answered this question", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            MultipleAnswer multipleAnswer = new MultipleAnswer(FirebaseAuth.getInstance().getCurrentUser().getEmail(),textBox.getText().toString(),new Date(),responseTime);
                            mFirebaseDatabaseReference.child("answers/"+message).push().setValue(multipleAnswer);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

                nameExists="false";
                try{
                    mFirebaseDatabaseReference.child("respondents/").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            for (DataSnapshot userSnapshot: snapshot.getChildren()){
                                Users users = userSnapshot.getValue(Users.class);
                                if(users.getUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                    nameExists="true";
                                }
                            }

                            if(nameExists.equals("true"))
                                Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                            else {
                                users = new Users(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                mFirebaseDatabaseReference.child("respondents/").push().setValue(users);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });


                }catch(Exception e){
                    users = new Users(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    mFirebaseDatabaseReference.child("respondents/").push().setValue(users);
                }

            }
        });
    }

    private void timerClass(){
        new CountDownTimer(timeRemaining, 1000) {
            public void onTick(long millisUntilFinished) {
                responseTime = timeRemaining - millisUntilFinished;
                mTextField.setText("Time remaining: "+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                mTextField.setText("Timeout!");
                submitAnswer.setEnabled(false);
            }

        }.start();
    }
}
