package ml.rushabh.smartdrive;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Declaring Instances of widgets on Main Activity.
    private ImageButton mSend;
    private EditText mMessage;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private LinearLayoutManager mLinearLayoutManager;
    private android.support.v7.widget.SearchView mSearchView;

    //Declaring Firebase Instances.
    private MessageAdapter mAdapter;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;

    private List<Message> mMessageList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Getting current user, if any.
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        //If user is null, start SignIn Activity.
        if (mUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        //Else, continue to Main Activity.
        else {
            setContentView(R.layout.activity_main);
            //Initializing Main Activity widgets.
            mProgressBar = (ProgressBar)findViewById(R.id.loading_pb);
            mMessage = (EditText) findViewById(R.id.message_ET);
            mSend = (ImageButton) findViewById(R.id.submit_btn);


            //Setting Database reference.
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(mUser.getUid()).child("messages");
            //Initializing RecyclerView and Layout Manager.
            mRecyclerView = (RecyclerView)findViewById(R.id.list_rv);
            mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mMessageList = new ArrayList<Message>();

            mSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMessage.getText() == null || mMessage.getText().toString().trim().isEmpty()) {
                    } else {
                        //Create message String, message Object and push into database.
                        String msgText = mMessage.getText().toString().trim();
                        Message message = new Message(msgText.trim(), mUser.getUid().toString(), mUser.getDisplayName().toString());
                        mDatabase.push().setValue(message);
                        //Clear EditText after send.
                        mMessage.setText("");
                    }
                }
            });


            Query query = mDatabase;

            mAdapter = new MessageAdapter(getApplicationContext(),mMessageList);
            mRecyclerView.setAdapter(mAdapter);


            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Message temp = dataSnapshot.getValue(Message.class);
                    mMessageList.add(temp);
                    Log.d("message list values", "onChildAdded: "+ mMessageList.size());
                   mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                   mAdapter.notifyDataSetChanged();

                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

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
    // Create and inflate Menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    // Sign-out menu option.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if(R.id.sign_out == item.getItemId()){
            //Back to Sign-in Activity.
            AuthUI.getInstance()
                    .signOut(this);
            intent = new Intent(MainActivity.this,SignInActivity.class);
            startActivity(intent);
            finish();

        }
        else if(item.getItemId() == R.id.action_search){
            return true;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
