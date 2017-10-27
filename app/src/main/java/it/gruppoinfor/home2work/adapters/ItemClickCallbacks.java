package it.gruppoinfor.home2work.adapters;

import android.view.View;


public interface ItemClickCallbacks {
    void onItemClick(View view, int position);

    boolean onLongItemClick(View view, int position);
}
