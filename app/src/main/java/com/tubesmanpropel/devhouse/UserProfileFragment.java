package com.tubesmanpropel.devhouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tubesmanpropel.devhouse.Prevalent.Prevalent;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class UserProfileFragment extends Fragment {

    private CircleImageView mProfileImage;
    private EditText mFullnameTxt, mAlamatTxt, mEmailTxt, mPhoneTxt;
    private Button mEditBtn, mUserLogoutBtn;
    private TextView mRegisterSellerBtn;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        mProfileImage = (CircleImageView) rootView.findViewById(R.id.imageUser);
        mFullnameTxt = (EditText) rootView.findViewById(R.id.namaUser);
        mEmailTxt = (EditText) rootView.findViewById(R.id.emailUser);
        mAlamatTxt = (EditText) rootView.findViewById(R.id.alamatUser);
        mPhoneTxt = (EditText) rootView.findViewById(R.id.phoneUser);
        mEditBtn = (Button) rootView.findViewById(R.id.editUserBtn);
        mUserLogoutBtn = (Button) rootView.findViewById(R.id.userLogoutBtn);
        mRegisterSellerBtn = (TextView) rootView.findViewById(R.id.registerSeller);

        userInfoDisplay(mProfileImage, mFullnameTxt, mEmailTxt, mAlamatTxt, mPhoneTxt);


        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UserEditProfileActivity.class);
                startActivity(i);
            }
        });

        mRegisterSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SellerRegisterActivity.class);
                startActivity(i);
            }
        });

        mUserLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();

                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        return rootView;
    }




    private void userInfoDisplay(final CircleImageView mProfileImage, final EditText mFullnameTxt, final EditText mEmailTxt, final EditText mAlamatTxt, final EditText mPhoneTxt) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String username = dataSnapshot.child("username").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String alamat = dataSnapshot.child("alamat").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();


                        Picasso.get().load(image).into(mProfileImage);
                        mFullnameTxt.setText(username);
                        mEmailTxt.setText(email);
                        mAlamatTxt.setText(alamat);
                        mPhoneTxt.setText(phone);
                    }
                    else if (dataSnapshot.child("alamat").exists()) {
                        String username = dataSnapshot.child("username").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String alamat = dataSnapshot.child("alamat").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();


                        mFullnameTxt.setText(username);
                        mEmailTxt.setText(email);
                        mAlamatTxt.setText(alamat);
                        mPhoneTxt.setText(phone);
                    }
                    else {
                        String username = dataSnapshot.child("username").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();


                        mFullnameTxt.setText(username);
                        mEmailTxt.setText(email);
                        mPhoneTxt.setText(phone);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}