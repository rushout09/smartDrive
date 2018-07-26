package ml.rushabh.smartdrive;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference mDisplayRef;
    private FirebaseUser mUser;
    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRecyclerView = (RecyclerView)findViewById(R.id.list_rv2);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mProgressBar = (ProgressBar)findViewById(R.id.loading_pb2);


        String searchQuery = getIntent().getStringExtra("searchText");
        Log.e("search query", "onCreate: "+searchQuery);
        mDisplayRef = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid()).child("tags");
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


        final Query query = mDisplayRef
                .child(searchQuery);


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
