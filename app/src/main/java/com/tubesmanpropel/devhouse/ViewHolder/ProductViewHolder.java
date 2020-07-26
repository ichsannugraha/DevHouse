package com.tubesmanpropel.devhouse.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tubesmanpropel.devhouse.Interface.ItemClickListner;
import com.tubesmanpropel.devhouse.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView namaProdukTxt, hargaProdukTxt;
    public ImageView imageView;
    public ItemClickListner listner;


    public ProductViewHolder(View itemView)
    {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.imageRumahPrev);
        namaProdukTxt = (TextView) itemView.findViewById(R.id.namaProdukPrev);
        hargaProdukTxt = (TextView) itemView.findViewById(R.id.hargaProdukPrev);
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
