package com.example.facechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.time.Instant;

public class findpeopleActivity extends AppCompatActivity {
    private RecyclerView findfriendlist;
    private EditText searchET;
    private String str="";
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpeople);

        userRef= FirebaseDatabase.getInstance().getReference().child("users");

        searchET=findViewById(R.id.search_user_text);
        findfriendlist=findViewById(R.id.find_friends_list);
        findfriendlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchET.getText().toString().equals("")){
                    Toast.makeText(findpeopleActivity.this, "please write name to search", Toast.LENGTH_SHORT).show();
                }
                else{
                    str=s.toString();
                    onStart();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts>options=null;
        if(str.equals(null))
        {
            options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(userRef,Contacts.class).build();

        }
        else{
            options=new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(userRef.orderByChild("name")
                    .startAt(str)
                            .endAt(str + "\uf8ff")
                            ,Contacts.class)
                    .build();
        }
        FirebaseRecyclerAdapter<Contacts,findFriendsViewHolder>firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Contacts, findFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull findFriendsViewHolder holder, final int position, @NonNull final Contacts contacts) {
                holder.userNameTxt.setText(contacts.getName());
                Picasso.get().load(contacts.getImage()).into(holder.profileImageView);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id=getRef(position).getKey();
                        Intent intent=new Intent(findpeopleActivity.this,ProfileActivity.class);
                        intent.putExtra("visit_user_id",visit_user_id);
                        intent.putExtra("profile_image",contacts.getImage());
                        intent.putExtra("profile_name",contacts.getName());
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public findFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cantact_design,parent,false);
                findFriendsViewHolder viewHolder=new findFriendsViewHolder(view);
                return viewHolder;
            }
        };
        findfriendlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class findFriendsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userNameTxt;
        Button videoCallBtn;
        ImageView profileImageView;
        RelativeLayout cardView;

        public findFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTxt=itemView.findViewById(R.id.name_contact);
            videoCallBtn=itemView.findViewById(R.id.call_btn);
            profileImageView=itemView.findViewById(R.id.image_contact);
            cardView=itemView.findViewById(R.id.card_view);

            videoCallBtn.setVisibility(View.GONE);

        }
    }
}
