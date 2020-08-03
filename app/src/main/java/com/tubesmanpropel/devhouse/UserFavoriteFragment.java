package com.tubesmanpropel.devhouse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tubesmanpropel.devhouse.Model.Favorites;
import com.tubesmanpropel.devhouse.Model.Products;
import com.tubesmanpropel.devhouse.Prevalent.Prevalent;
import com.tubesmanpropel.devhouse.ViewHolder.ProductViewHolder;

import java.text.DecimalFormat;

public class UserFavoriteFragment extends Fragment {

    private DatabaseReference FavoritesRef;
    private DatabaseReference ProductsRef;
    private DatabaseReference SellersRef;
    private DataSnapshot dataSnapshot;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_favorite, container, false);

        FavoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        SellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        recyclerView = rootView.findViewById(R.id.userFavRecyclerView);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Favorites> options =
                new FirebaseRecyclerOptions.Builder<Favorites>()
                        .setQuery(FavoritesRef.child(Prevalent.currentOnlineUser.getPhone()), Favorites.class)
                        .build();


        FirebaseRecyclerAdapter<Favorites, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Favorites, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int i, @NonNull final Favorites model) {
                        ProductsRef.child(model.getPid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Products products = dataSnapshot.getValue(Products.class);

                                    holder.namaProdukTxt.setText(products.getNama());
                                    holder.hargaProdukTxt.setText("Rp. " + formatNumber(products.getHarga()));
                                    Picasso.get().load(products.getImage()).fit().centerCrop().into(holder.imageView);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //holder.namaProdukTxt.setText(ProductsRef.child(model.getPid()).child("nama").toString());
                        //holder.hargaProdukTxt.setText("Rp." + ProductsRef.child(model.getPid()).child("harga").toString());
                        //Picasso.get().load(model.getImage()).fit().centerCrop().into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Lihat Detail",
                                        "Hapus dari Favorit"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Intent i = new Intent(getActivity(), ProductDetailsActivity.class);
                                            i.putExtra("idProduk", model.getPid());
                                            i.putExtra("sid", model.getSid());
                                            i.putExtra("userType", "User");
                                            startActivity(i);
                                        }
                                        if (which == 1) {
                                            FavoritesRef.child(Prevalent.currentOnlineUser.getPhone()).child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getActivity(), "Produk telah dihapus dari favorit!", Toast.LENGTH_SHORT).show();
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


    private static String formatNumber(String number) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        return formatter.format(Double.parseDouble(number));
    }

}