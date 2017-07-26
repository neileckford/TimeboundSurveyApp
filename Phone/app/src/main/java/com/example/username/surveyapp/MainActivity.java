package com.example.username.surveyapp;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView questionIdentity;
        public TextView selectQuestion;
        public TextView category;
        public TextView datePosted;
        public TextView deadline;

        public MessageViewHolder(View v) {
            super(v);
            questionIdentity = (TextView) itemView.findViewById(R.id.questionIdentity);
            selectQuestion = (TextView) itemView.findViewById(R.id.selectQuestion);
            category = (TextView) itemView.findViewById(R.id.category);
            datePosted = (TextView) itemView.findViewById(R.id.datePosted);
            deadline = (TextView) itemView.findViewById(R.id.deadline);
        }
    }

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final String TAG = "MainActivity";
    public static final String multipleNode = "multiple_choice";
    private static final int REQUEST_INVITE = 1;
    public static final int maxLength = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String MULTIPLE_MSG_LENGTH = "multiple_msg_length";
    private String mUsername;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    NotificationCompat.Builder notificationBuilder;
    NotificationManager nm;

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Question, MessageViewHolder> mFirebaseAdapter;

    private static final String FORMAT = "%02d:%02d:%02d";

    int seconds , minutes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser == null){
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }else{
            mUsername = mFirebaseUser.getDisplayName();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setStackFromEnd(true);
        //mLinearLayoutManager.setReverseLayout(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        notificationBuilder = new NotificationCompat.Builder(this);
        pushNotification();
        populateRecyclerView();
        registerAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void pushNotification(){
        mFirebaseDatabaseReference.child("question_details").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Question question = dataSnapshot.getValue(Question.class);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setContentTitle("Survey App");
                notificationBuilder.setContentText(question.getQuestion());
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notificationBuilder.build());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateRecyclerView(){
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Question, MessageViewHolder>(
                Question.class,
                R.layout.activity_question_list,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child("question_details").orderByChild("deadline/time")) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              Question question, int position) {
                if (question.getDeadline().getTime() > System.currentTimeMillis()) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                viewHolder.selectQuestion.setText(question.getQuestion());
                viewHolder.category.setText("Category: "+question.getCategory());
                viewHolder.datePosted.setText("Posted: "+question.getTimeStamp().getDate()+"/"+
                        (question.getTimeStamp().getMonth()+1)+"/"+
                        (question.getTimeStamp().getYear()-100)+" - "+
                        String.format("%02d:%02d", question.getTimeStamp().getHours(), question.getTimeStamp().getMinutes())+" by "+question.getPostedBy());
                viewHolder.deadline.setText("Available until: "+question.getDeadline().getDate()+"/"+
                        (question.getDeadline().getMonth()+1)+"/"+
                        (question.getDeadline().getYear()-100)+" - "+
                        String.format("%02d:%02d", question.getDeadline().getHours(), question.getDeadline().getMinutes()));
                final String questionID = this.getRef(position).getKey();
                final String quesType = question.getType();
                viewHolder.selectQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, AnswerQuestion.class);
                        String message = questionID;
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);
                    }
                });
                }else{
                    viewHolder.itemView.setLayoutParams((new LinearLayout.LayoutParams(0,0)));
                }
            }
        };
    }

    private void registerAdapter(){
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount){
                super.onItemRangeInserted(positionStart, itemCount);
                int multipleChoiceCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (multipleChoiceCount -1) &&
                                lastVisiblePosition == (positionStart -1))){
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
