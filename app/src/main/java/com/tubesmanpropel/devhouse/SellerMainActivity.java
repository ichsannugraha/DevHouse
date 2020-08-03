package com.tubesmanpropel.devhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tubesmanpropel.devhouse.Adapter.PagerAdapter;
import com.tubesmanpropel.devhouse.Model.Products;
import com.tubesmanpropel.devhouse.Prevalent.Prevalent;
import com.tubesmanpropel.devhouse.ViewHolder.ProductViewHolder;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerMainActivity extends AppCompatActivity {

    private Button mSettingsBtn;
    private TextView alamatSeller, namaSeller;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private TabItem mProdukTab, mDiskusiTab;
    private PagerAdapter pagerAdapter;
    private long backPressedTime;
    private Toast backToast;

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);

        //mViewPager = (ViewPager) findViewById(R.id.sellerViewPager);
        //tabLayout = (TabLayout) findViewById(R.id.sellerTabBar);
        //mProdukTab = (TabItem) findViewById(R.id.sellerProdukTab);
        //mDiskusiTab = (TabItem) findViewById(R.id.sellerDiskusiTab);

        mSettingsBtn = (Button) findViewById(R.id.sellerMainSettingsBtn);
        alamatSeller = (TextView) findViewById(R.id.alamatSellerMain);
        namaSeller = (TextView) findViewById(R.id.namaSellerMain);
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = findViewById(R.id.sellerRecyclerView);

        //pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        //mViewPager.setAdapter(pagerAdapter);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        userInfoDisplay(alamatSeller);

        /*
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mViewPager.setCurrentItem(tab.getPosition());
                } else if (tab.getPosition() == 1) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        */

        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerMainActivity.this, SellerSettingsActivity.class);
                startActivity(i);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef.orderByChild("sid").startAt(Prevalent.currentOnlineSeller.getPhone()).endAt(Prevalent.currentOnlineSeller.getPhone()), Products.class)
                        .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull final Products model) {
                        holder.namaProdukTxt.setText(model.getNama());
                        holder.hargaProdukTxt.setText("Rp. " + formatNumber(model.getHarga()));
                        Picasso.get().load(model.getImage()).fit().centerCrop().into(holder.imageView);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Lihat Detail",
                                        "Edit Produk",
                                        "Hapus Produk"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(SellerMainActivity.this);

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which==0) {
                                            Intent i = new Intent(SellerMainActivity.this, ProductDetailsActivity.class);
                                            i.putExtra("idProduk", model.getPid());
                                            i.putExtra("sid", model.getSid());
                                            i.putExtra("userType", "Seller");
                                            startActivity(i);
                                        }
                                        if (which==1) {
                                            Intent i = new Intent(SellerMainActivity.this, SellerEditProdukActivity.class);
                                            i.putExtra("idProduk", model.getPid());
                                            startActivity(i);
                                        }
                                        if (which==2) {
                                            ProductsRef.child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SellerMainActivity.this, "Produk berhasil dihapus!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void userInfoDisplay(final TextView alamatSeller)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Sellers").child(Prevalent.currentOnlineSeller.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String username = dataSnapshot.child("username").getValue().toString();
                    namaSeller.setText(username);

                    if (dataSnapshot.child("alamat").exists())
                    {
                        String alamat = dataSnapshot.child("alamat").getValue().toString();
                        alamatSeller.setText(alamat);
                    }
                    else {
                        
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private static String formatNumber(String number) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        return formatter.format(Double.parseDouble(number));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }
}
