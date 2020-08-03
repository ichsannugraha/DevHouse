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

public class SellerEditProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private EditText mFullnameTxt, mAlamatTxt, mDeskripsiTxt, mPhoneTxt, mRtRwTxt;
    private Button mEditBtn;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_edit_profile);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Seller Profile Pictures");

        mProfileImage = (CircleImageView) findViewById(R.id.sellerProfileImageEdit);
        mFullnameTxt = (EditText) findViewById(R.id.namaSellerEdit);
        mAlamatTxt = (EditText) findViewById(R.id.alamatSellerEdit);
        mPhoneTxt = (EditText) findViewById(R.id.phoneSellerEdit);
        mRtRwTxt = (EditText) findViewById(R.id.rtrwSellerEdit);
        mDeskripsiTxt = (EditText) findViewById(R.id.deskripsiSellerEdit);
        mEditBtn = (Button) findViewById(R.id.editSellerBtn);

        userInfoDisplay(mProfileImage, mFullnameTxt, mAlamatTxt, mPhoneTxt, mRtRwTxt, mDeskripsiTxt);


        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
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
                        .start(SellerEditProfileActivity.this);
            }
        });
    }


    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sellers");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("username", mFullnameTxt.getText().toString());
        userMap. put("alamat", mAlamatTxt.getText().toString());
        userMap. put("phoneOrder", mPhoneTxt.getText().toString());
        userMap. put("rtrw", mRtRwTxt.getText().toString());
        userMap. put("deskripsi", mDeskripsiTxt.getText().toString());
        ref.child(Prevalent.currentOnlineSeller.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SellerEditProfileActivity.this, SellerMainActivity.class));
        Toast.makeText(SellerEditProfileActivity.this, "Berhasil update info profile!", Toast.LENGTH_SHORT).show();
        finish();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            mProfileImage.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SellerEditProfileActivity.this, SellerEditProfileActivity.class));
            finish();
        }
    }


    private void validateInfo(){
        if (TextUtils.isEmpty(mFullnameTxt.getText().toString()))
        {
            Toast.makeText(this, "Name tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mAlamatTxt.getText().toString()))
        {
            Toast.makeText(this, "Alamat tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mPhoneTxt.getText().toString()))
        {
            Toast.makeText(this, "No HP tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mRtRwTxt.getText().toString()))
        {
            Toast.makeText(this, "RT/RW tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mDeskripsiTxt.getText().toString()))
        {
            Toast.makeText(this, "Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else {
            updateOnlyUserInfo();
        }
    }


    private void userInfoSaved() {
        if (TextUtils.isEmpty(mFullnameTxt.getText().toString()))
        {
            Toast.makeText(this, "Name tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mAlamatTxt.getText().toString()))
        {
            Toast.makeText(this, "Alamat tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mPhoneTxt.getText().toString()))
        {
            Toast.makeText(this, "No HP tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mRtRwTxt.getText().toString()))
        {
            Toast.makeText(this, "RT/RW tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mDeskripsiTxt.getText().toString()))
        {
            Toast.makeText(this, "Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }


    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Data profile anda sedang diubah...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentOnlineSeller.getPhone() + ".jpg");

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

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sellers");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("username", mFullnameTxt.getText().toString());
                                userMap. put("alamat", mAlamatTxt.getText().toString());
                                userMap. put("phoneOrder", mPhoneTxt.getText().toString());
                                userMap. put("rtrw", mRtRwTxt.getText().toString());
                                userMap. put("deskripsi", mDeskripsiTxt.getText().toString());
                                userMap. put("image", myUrl);
                                ref.child(Prevalent.currentOnlineSeller.getPhone()).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(SellerEditProfileActivity.this, SellerMainActivity.class));
                                Toast.makeText(SellerEditProfileActivity.this, "Update info profile berhasil!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SellerEditProfileActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "gambar tidak terpilih!", Toast.LENGTH_SHORT).show();
        }
    }



    private void userInfoDisplay(final CircleImageView mProfileImage, final EditText mFullnameTxt, final EditText mAlamatTxt, final EditText mPhoneTxt, final EditText mRtRwTxt, final EditText mDeskripsiTxt)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(Prevalent.currentOnlineSeller.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("username").getValue().toString();
                        String alamat = dataSnapshot.child("alamat").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String rtrw = dataSnapshot.child("rtrw").getValue().toString();
                        String deskripsi = dataSnapshot.child("deskripsi").getValue().toString();

                        Picasso.get().load(image).into(mProfileImage);
                        mFullnameTxt.setText(name);
                        mAlamatTxt.setText(alamat);
                        mPhoneTxt.setText(phone);
                        mRtRwTxt.setText(rtrw);
                        mDeskripsiTxt.setText(deskripsi);
                    }
                    else if (dataSnapshot.child("alamat").exists()) {
                        String name = dataSnapshot.child("username").getValue().toString();
                        String alamat = dataSnapshot.child("alamat").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String rtrw = dataSnapshot.child("rtrw").getValue().toString();
                        String deskripsi = dataSnapshot.child("deskripsi").getValue().toString();


                        mFullnameTxt.setText(name);
                        mAlamatTxt.setText(alamat);
                        mPhoneTxt.setText(phone);
                        mRtRwTxt.setText(rtrw);
                        mDeskripsiTxt.setText(deskripsi);
                    }
                    else {
                        String name = dataSnapshot.child("username").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();

                        mFullnameTxt.setText(name);
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
