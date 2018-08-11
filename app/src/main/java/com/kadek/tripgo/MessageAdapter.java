package com.kadek.tripgo;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by K on 08/03/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private Context context;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText, messageTextOther, timeText, timeTextOther;
        public CircleImageView profileImage;
        public TextView displayName, displayNameOther;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            messageTextOther = (TextView) view.findViewById(R.id.message_text_layout_other);
            //profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            displayNameOther = (TextView) view.findViewById(R.id.name_text_layout_other);
            timeText = (TextView) view.findViewById(R.id.time_text_layout);
            timeTextOther = (TextView) view.findViewById(R.id.time_text_layout_other);

        }
    }



    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {



        final Messages c = mMessageList.get(i);

        mAuth = FirebaseAuth.getInstance();
        Context context = null;

        final String from_user = c.getFrom();
        String message_type = c.getType();
        GetTimeAgo timeAgo = new GetTimeAgo();
        final String lastTimeMessage = timeAgo.getTimeAgo(c.getTime(), context);

        final String current_user_id = mAuth.getCurrentUser().getUid();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();



                //Picasso.with(viewHolder.profileImage.getContext()).load(image).placeholder(R.drawable.avatar_pic).into(viewHolder.profileImage);

                if (from_user.equals(current_user_id)){

                    viewHolder.messageTextOther.setVisibility(View.VISIBLE);
                    viewHolder.displayNameOther.setVisibility(View.VISIBLE);
                    viewHolder.timeTextOther.setVisibility(View.VISIBLE);

                    viewHolder.messageText.setVisibility(View.INVISIBLE);
                    viewHolder.displayName.setVisibility(View.INVISIBLE);
                    viewHolder.timeText.setVisibility(View.INVISIBLE);

                    viewHolder.messageTextOther.setBackgroundResource(R.drawable.message_text_background);
                    viewHolder.messageTextOther.setTextColor(Color.WHITE);
                    viewHolder.messageTextOther.setText(c.getMessage());
                    viewHolder.displayNameOther.setText(name);
                    viewHolder.timeTextOther.setText(lastTimeMessage);


                }else {
                    viewHolder.messageText.setVisibility(View.VISIBLE);
                    viewHolder.displayName.setVisibility(View.VISIBLE);
                    viewHolder.timeText.setVisibility(View.VISIBLE);

                    viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background_other);
                    viewHolder.messageText.setTextColor(Color.BLACK);
                    viewHolder.messageTextOther.setVisibility(View.INVISIBLE);
                    viewHolder.timeTextOther.setVisibility(View.INVISIBLE);
                    viewHolder.displayNameOther.setVisibility(View.INVISIBLE);
                    viewHolder.messageText.setText(c.getMessage());
                    viewHolder.displayName.setText(name);
                    viewHolder.timeText.setText(lastTimeMessage);


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            //Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
           //         .placeholder(R.drawable.avatar_pic).into(viewHolder.messageImage);

        }*/

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }






}
