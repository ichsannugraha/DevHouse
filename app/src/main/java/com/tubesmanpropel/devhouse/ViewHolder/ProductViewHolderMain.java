package com.tubesmanpropel.devhouse.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tubesmanpropel.devhouse.Interface.ItemClickListner;
import com.tubesmanpropel.devhouse.R;

public class ProductViewHolderMain extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView namaProdukTxt, hargaProdukTxt;
    public ImageView imageView;
    public ItemClickListner listner;


    public ProductViewHolderMain(View itemView)
    {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.imageRumahMain);
        namaProdukTxt = (TextView) itemView.findViewById(R.id.namaProdukMain);
        hargaProdukTxt = (TextView) itemView.findViewById(R.id.hargaProdukMain);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
