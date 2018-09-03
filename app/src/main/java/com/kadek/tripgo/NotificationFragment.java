package com.kadek.tripgo;


import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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


        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mNotificationList.setHasFixedSize(true);
        mNotificationList.setLayoutManager(linearLayoutManager);



        return mViewNotif;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i("Fragment","onStart");
        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event");
        mEventDatabase.keepSynced(true);

        FirebaseRecyclerAdapter<Event, EventAllViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventAllViewHolder>(
                Event.class,
                R.layout.event_single_layout,
                EventAllViewHolder.class,
                mEventDatabase
        ) {
            @Override
            protected void populateViewHolder(final EventAllViewHolder viewHolder, Event event, int i) {

                final String event_id = getRef(i).getKey();

                viewHolder.setName(event.getName());
                viewHolder.setDescription(event.getEvent_start());
                viewHolder.setThumb_image(event.getEvent_thumb_image(), getContext());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent editIntent = new Intent(getContext(), EventDetailActivity.class);
                        editIntent.putExtra("event_id", event_id);
                        startActivity(editIntent);

                    }
                });

            }
        };

        mNotificationList.setAdapter(firebaseRecyclerAdapter);




    }
    public static class EventAllViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public EventAllViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName (String name){

            TextView mEventName = (TextView) mView.findViewById(R.id.event_single_name);
            mEventName.setText(name);

        }
        public void setDescription(String description) {
            TextView mEventDescription = (TextView) mView.findViewById(R.id.event_litle_description);
            mEventDescription.setText("Tanggal: "+description);
        }


        public void setThumb_image(final String thumb_image, final Context ctx){

            final ImageView mEventImage = (ImageView) mView.findViewById(R.id.event_image_preview);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.placeholder).into(mEventImage);

        }


    }

}
