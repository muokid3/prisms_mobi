package com.kemriwellcome.dm.prisms.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fxn.stash.Stash;
import com.google.firebase.auth.FirebaseAuth;

import com.kemriwellcome.dm.prisms.R;
import com.kemriwellcome.dm.prisms.dependencies.Constants;
import com.kemriwellcome.dm.prisms.models.User;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ProfileFragment extends Fragment {


    private Unbinder unbinder;
    private View root;
    private Context context;

    private User loggedInUser;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;




    @BindView(R.id.image)
    CircularImageView image;

    @BindView(R.id.name)
    TextView name;

//
//    @BindView(R.id.resourceCenterLayout)
//    CardView resourceCenterLayout;
//
//    @BindView(R.id.checkinLayout)
//    CardView checkinLayout;
//
//    @BindView(R.id.feedbackLayout)
//    CardView feedbackLayout;
//
//    @BindView(R.id.jitengeLayout)
//    CardView jitengeLayout;




    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        this.context = ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, root);



        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);


        name.setText(loggedInUser.getTitle()+" "+loggedInUser.getFirst_name()+" "+loggedInUser.getLast_name());





        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }




}