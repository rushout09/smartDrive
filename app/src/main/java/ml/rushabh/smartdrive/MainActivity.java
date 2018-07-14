package ml.rushabh.smartdrive;

import android.content.Intent;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button mSend;
    private EditText mMessage;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;


    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    private LinearLayoutManager mLinearLayoutManager;

    private FirebaseRecyclerAdapter mAdapter;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        else {
            setContentView(R.layout.activity_main);

            mProgressBar = (ProgressBar)findViewById(R.id.loading_pb);
            mMessage = (EditText) findViewById(R.id.message_ET);
            mSend = (Button) findViewById(R.id.submit_btn);

            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid());


            mRecyclerView = (RecyclerView)findViewById(R.id.list_rv);
            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            if(savedInstanceState != null){
                Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            }


            mSend.setEnabled(true);

            mSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMessage.getText() == null || mMessage.getText().toString().trim().isEmpty()) {
                    } else {
                        mSend.setEnabled(false);
                        Message message = new Message(mMessage.getText().toString().trim(), mUser.getUid().toString(), mUser.getDisplayName().toString());
                        mDatabase.child("messages").push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mSend.setEnabled(true);
                                mMessage.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Message not sent.", Toast.LENGTH_SHORT).show();
                                mSend.setEnabled(true);
                            }
                        });
                    }
                }
            });

            class MessageHolder extends RecyclerView.ViewHolder {

                TextView messageTextView;
                TextView timeTextView;

                public MessageHolder(View itemView) {
                    super(itemView);

                    messageTextView = (TextView) itemView.findViewById(R.id.message_tv);
                    timeTextView = (TextView) itemView.findViewById(R.id.time_tv);
                }

                public void setValues(Message message) {
                    messageTextView.setText(message.getBody());
                    timeTextView.setText(DateFormat.format("HH:mm",
                            message.getTimeStamp()));
                }
            }

             final Query query = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(mUser.getUid())
                    .child("messages")
                    .limitToLast(30);

            FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                    .setQuery(query, Message.class)
                    .build();


            mAdapter = new FirebaseRecyclerAdapter<Message, MessageHolder>(options){
                @NonNull
                @Override
                public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_message, parent, false);
                    return new MessageHolder(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message model) {

                    Message message = getItem(position);

                    holder.setValues(message);
                }

                @Override
                public int getItemCount() {
                    return super.getItemCount();
                }
            };

            mRecyclerView.setAdapter(mAdapter);


            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    mLinearLayoutManager.onRestoreInstanceState(savedInstanceState);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // ...
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            };
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mProgressBar.setVisibility(View.INVISIBLE);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            query.addChildEventListener(childEventListener);
            mRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount());



        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if(R.id.sign_out == item.getItemId()){
            AuthUI.getInstance()
                    .signOut(this);
            intent = new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
            finish();

        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
        mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount());

    }



    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
