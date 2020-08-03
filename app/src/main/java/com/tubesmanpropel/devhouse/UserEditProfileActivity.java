package com.tubesmanpropel.devhouse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class UserEditProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private EditText mFullnameTxt, mAlamatTxt, mEmailTxt, mPhoneTxt;
    private Button mSimpanBtn;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        mProfileImage = (CircleImageView) findViewById(R.id.imageUserEdit);
        mFullnameTxt = (EditText) findViewById(R.id.namaUserEdit);
        mEmailTxt = (EditText) findViewById(R.id.emailUserEdit);
        mAlamatTxt = (EditText) findViewById(R.id.alamatUserEdit);
        mPhoneTxt = (EditText) findViewById(R.id.phoneUserEdit);
        mSimpanBtn = (Button) findViewById(R.id.simpanEditUserBtn);

        userInfoDisplay(mProfileImage, mFullnameTxt, mEmailTxt, mAlamatTxt, mPhoneTxt);


        mSimpanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }
                else {
                    validateInfo();
                }
            }
        });


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(UserEditProfileActivity.this);
            }
        });
    }



    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("username", mFullnameTxt.getText().toString());
        userMap. put("alamat", mAlamatTxt.getText().toString());
        userMap. put("phoneOrder", mPhoneTxt.getText().toString());
        userMap. put("email", mEmailTxt.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(UserEditProfileActivity.this, MainActivity.class));
        Toast.makeText(this, "Berhasil mengupdate info profile!", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            mProfileImage.setImageURI(imageUri);
        }
        else {
            Toast.makeText(UserEditProfileActivity.this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(UserEditProfileActivity.this, UserEditProfileActivity.class));
            finish();
        }
    }


    private void validateInfo() {
        if (TextUtils.isEmpty(mFullnameTxt.getText().toString()))
        {
            Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mAlamatTxt.getText().toString()))
        {
            Toast.makeText(this, "Alamat tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mEmailTxt.getText().toString()))
        {
            Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mPhoneTxt.getText().toString()))
        {
            Toast.makeText(this, "No HP tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else {
            updateOnlyUserInfo();
        }
    }


    private void userInfoSaved() {
        if (TextUtils.isEmpty(mFullnameTxt.getText().toString()))
        {
            Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mAlamatTxt.getText().toString()))
        {
            Toast.makeText(this, "Alamat tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mEmailTxt.getText().toString()))
        {
            Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mPhoneTxt.getText().toString()))
        {
            Toast.makeText(this, "No HP tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }



    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Mohon tunggu, data profil anda sedang diubah.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(Prevalent.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = (Uri) task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("username", mFullnameTxt.getText().toString());
                                userMap. put("alamat", mAlamatTxt.getText().toString());
                                userMap. put("phoneOrder", mPhoneTxt.getText().toString());
                                userMap. put("email", mEmailTxt.getText().toString());
                                userMap. put("image", myUrl);
                                ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(UserEditProfileActivity.this, MainActivity.class));
                                Toast.makeText(UserEditProfileActivity.this, "Update profile berhasil!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(UserEditProfileActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "Gambar tidak terpilih!", Toast.LENGTH_SHORT).show();
        }
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
