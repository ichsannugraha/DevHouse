package com.tubesmanpropel.devhouse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tubesmanpropel.devhouse.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerRegisterActivity extends AppCompatActivity {

    private Button mRegisterSeller;
    private String namaSeller, passwordSeller, emailSeller, phoneSeller;
    private EditText mUsernameTxt, mPasswordTxt, mEmailTxt, mPhoneTxt;
    private ImageView mFotoKTP, mFotoWajah;
    private ProgressDialog loadingBar;

    private String downloadImageUrl;
    private String checker = "";
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageSellerKTPRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_register);

        storageSellerKTPRef = FirebaseStorage.getInstance().getReference().child("Seller KTP");

        mRegisterSeller = (Button) findViewById(R.id.registerSellerBtn);
        mUsernameTxt = (EditText) findViewById(R.id.usernameSellerTxt);
        mPasswordTxt = (EditText) findViewById(R.id.passwordSellerTxt);
        mEmailTxt = (EditText) findViewById(R.id.emailSellerTxt);
        mPhoneTxt = (EditText) findViewById(R.id.phoneSellerTxt);
        mFotoKTP = (ImageView) findViewById(R.id.fotoKTP);

        loadingBar = new ProgressDialog(this);

        userInfoDisplay(mUsernameTxt, mEmailTxt, mPhoneTxt);


        mRegisterSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateSellerData();
            }
        });


        mFotoKTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SellerRegisterActivity.this);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            mFotoKTP.setImageURI(imageUri);
        }
        //else
        //{
        //    Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();
        //
        //   startActivity(new Intent(SellerRegisterActivity.this, SellerRegisterActivity.class));
        //    finish();
        //}
    }


    private void ValidateSellerData() {
        namaSeller = mUsernameTxt.getText().toString();
        passwordSeller = mPasswordTxt.getText().toString();
        emailSeller = mEmailTxt.getText().toString();
        phoneSeller = mPhoneTxt.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Gambar KTP tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(namaSeller)) {
            Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(passwordSeller)) {
            Toast.makeText(this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emailSeller)) {
            Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneSeller)) {
            Toast.makeText(this, "No HP tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreSellerInformation(namaSeller, passwordSeller, emailSeller, phoneSeller);
        }
    }


    private void StoreSellerInformation(final String namaSeller, final String passwordSeller, final String emailSeller, final String phoneSeller) {

        loadingBar.setTitle("Membuat Akun Seller");
        loadingBar.setMessage("Mohon Tunggu Sebentar...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final StorageReference filePath = storageSellerKTPRef.child(imageUri.getLastPathSegment() + phoneSeller + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(SellerRegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                //Toast.makeText(SellerRegisterActivity.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            //Toast.makeText(SellerRegisterActivity.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();

                            SaveSellerInfoToDatabase(namaSeller, passwordSeller, emailSeller, phoneSeller);
                        }
                    }
                });
            }
        });
    }


    private void SaveSellerInfoToDatabase(final String namaSeller, final String passwordSeller, final String emailSeller, final String phoneSeller) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Sellers").child(phoneSeller).exists())) {
                    HashMap<String, Object> sellerdataMap = new HashMap<>();
                    sellerdataMap.put("phone", phoneSeller);
                    sellerdataMap.put("email", emailSeller);
                    sellerdataMap.put("password", passwordSeller);
                    sellerdataMap.put("username", namaSeller);
                    sellerdataMap.put("imageKTP", downloadImageUrl);

                    RootRef.child("Sellers").child(phoneSeller).updateChildren(sellerdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SellerRegisterActivity.this, "Akun seller berhasil dibuat!", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent i = new Intent(SellerRegisterActivity.this, MainActivity.class);
                                        startActivity(i);
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(SellerRegisterActivity.this, "Terjadi kesalahan, silakan coba kembali!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(SellerRegisterActivity.this, "Akun anda sudah memiliki akun seller!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(SellerRegisterActivity.this, "Gagal membuat akun seller!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SellerRegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void userInfoDisplay(final EditText mUsernameTxt, final EditText mEmailTxt, final EditText mPhoneTxt) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();

                    mUsernameTxt.setText(username);
                    mEmailTxt.setText(email);
                    mPhoneTxt.setText(phone);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
