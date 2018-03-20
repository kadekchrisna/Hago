package com.kadek.tripgo;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    private RecyclerView mNotificationList;
    private View mViewNotif;

    private DatabaseReference mEventDatabase;


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mViewNotif = inflater.inflate(R.layout.fragment_notification, container, false);

        mNotificationList = (RecyclerView) mViewNotif.findViewById(R.id.notification_event_list);
        mNotificationList.setHasFixedSize(true);
        mNotificationList.setLayoutManager(new LinearLayoutManager(getContext()));

        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event");



        return mViewNotif;
    }

    @Override
    public void onStart() {
        super.onStart();

        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event");

        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(
                Event.class,
                R.layout.event_single_layout,
                EventViewHolder.class,
                mEventDatabase.orderByChild("timestamp")

        ) {
            @Override
            protected void populateViewHolder(final EventViewHolder viewHolder, Event model, int position) {

                final String event_id = getRef(position).getKey();
                mEventDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            String name = dataSnapshot.child("name").getValue().toString();
                            String description = dataSnapshot.child("description").getValue().toString();
                            String thumb_image = dataSnapshot.child("event_thumb_image").getValue().toString();
                            String image = dataSnapshot.child("event_image").getValue().toString();


                            viewHolder.setName(name);
                            viewHolder.setDescription(description);
                            viewHolder.setThumb_image(thumb_image, getActivity().getApplicationContext());


                        }

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent editIntent = new Intent(getActivity(), EventDetailActivity.class);
                                //editIntent.putExtra("event_id", event_id);
                                startActivity(editIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        //mNotificationList.setAdapter(firebaseRecyclerAdapter);



    }
    public static class EventViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public EventViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName (String name){

            TextView mPlaceName = (TextView) mView.findViewById(R.id.event_single_name);
            mPlaceName.setText(name);

        }
        public void setDescription(String description) {
            TextView mPlaceStatus = (TextView) mView.findViewById(R.id.event_litle_description);
            mPlaceStatus.setText(description);
        }


        public void setThumb_image(final String thumb_image, final Context ctx){

            final ImageView mPlaceImage = (ImageView) mView.findViewById(R.id.event_image_preview);
            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.palceholder).into(mPlaceImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.palceholder).into(mPlaceImage);

                }
            });

        }


    }

}
