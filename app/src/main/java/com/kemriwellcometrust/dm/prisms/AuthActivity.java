package com.kemriwellcometrust.dm.prisms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kemriwellcometrust.dm.prisms.dependencies.Constants;
import com.kemriwellcometrust.dm.prisms.dependencies.PrismsApplication;
import com.kemriwellcometrust.dm.prisms.fragments.LoginFragment;
import com.kemriwellcometrust.dm.prisms.interfaces.NavigationHost;
import com.kemriwellcometrust.dm.prisms.models.Creds;
import com.kemriwellcometrust.dm.prisms.models.User;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity implements NavigationHost {

//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private User loggedInUser;
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = database.getReference().child(Constants.API_VERSION);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        loggedInUser = (User) Stash.getObject(Constants.USER, User.class);

        PrismsApplication.handleSSLHandshake();


        if (loggedInUser != null){

            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        if (Constants.API_VERSION.equals("prisms_api_v0")){
            Stash.put(Constants.END_POINT, "https://prisms.kemri-wellcome.org/api/");
            Log.e("endpoint:", "https://prisms.kemri-wellcome.org/api/");
        }else {
            Stash.put(Constants.END_POINT, "https://prismsuat.kemri-wellcome.org/api/");
            Log.e("endpoint:", "https://prismsuat.kemri-wellcome.org/api/");
        }

        Fragment newFragment = LoginFragment.getInstance(AuthActivity.this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, newFragment)
                .commitAllowingStateLoss();

//        mAuth = FirebaseAuth.getInstance();
//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//                if (firebaseAuth.getCurrentUser() == null) {
//                    Log.e("FIREBASE::", "Signing in....");
//                    signInAnonymously();
//                }else {
//                    getCreds();
//                }
//
//            }
//        };
    }

//    private void signInAnonymously() {
//        mAuth.signInAnonymously()
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.e("FIREBASE::", "signInAnonymously:success");
//                            getCreds();
//
//                            //FirebaseUser user = mAuth.getCurrentUser();
//                            // updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.e("FIREBASE::", "signInAnonymously:failure", task.getException());
////                            Toast.makeText(AnonymousAuthActivity.this, "Authentication failed.",
////                                    Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                        }
//                    }
//                });
//
//    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commitAllowingStateLoss();
    }

//    private void getCreds() {
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                try {
//                    Log.e("Calling: ","updateDataSnapshot");
//                    updateDataSnapshot(dataSnapshot);
//
//                } catch (NullPointerException ex) {
//                    Log.e("nullpointer detected", ex.toString());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
////                    Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
//    }

    public void updateDataSnapshot(DataSnapshot dataSnapshot) {

        Stash.put(Constants.END_POINT, Objects.requireNonNull(dataSnapshot.getValue(Creds.class)).getEnd_point());

        Log.e("endpoint:", dataSnapshot.getValue(Creds.class).getEnd_point());


        Fragment newFragment = LoginFragment.getInstance(AuthActivity.this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, newFragment)
                .commitAllowingStateLoss();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthStateListener);
    }


    @Override
    public void onStop() {
        super.onStop();
//        if (mAuthStateListener != null) {
//            mAuth.removeAuthStateListener(mAuthStateListener);
//        }
    }

}