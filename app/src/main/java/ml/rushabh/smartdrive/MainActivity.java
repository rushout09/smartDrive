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
    private Button mHash;
    private EditText mMessage;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private LinearLayoutManager mLinearLayoutManager;

    private FirebaseRecyclerAdapter mAdapter;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mTagReference;

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
            mHash = (Button)findViewById(R.id.hash_btn);

            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid()).child("messages");
            mTagReference = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid()).child("tags");



            mRecyclerView = (RecyclerView)findViewById(R.id.list_rv);
            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mSend.setEnabled(true);

            mHash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMessage.append(mMessage.getText().toString()+"#");
                }
            });

            mSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMessage.getText() == null || mMessage.getText().toString().trim().isEmpty()) {
                    } else {
                        String msgText = mMessage.getText().toString().trim();
                        String tagName = "general";
                        Message message;
                        Tag tag;
                        if(msgText.startsWith("#")) {

                            tagName = msgText.substring(1, msgText.indexOf(" "));
                        }

                        message = new Message(msgText.trim(), mUser.getUid().toString(), mUser.getDisplayName().toString(),tagName);
                        tag = new Tag(tagName,message);
                        mDatabase.push().setValue(message);
                        mTagReference.child(tagName).push().setValue(message);

                        mMessage.setText("");
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


             final Query query = mDatabase;

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
                    mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount());

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
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
        //mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
