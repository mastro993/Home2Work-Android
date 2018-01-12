package it.gruppoinfor.home2work.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2workapi.model.Share;

public class OngoinShareView extends RelativeLayout {

    private Context context;
    private Share share;

    public OngoinShareView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public OngoinShareView(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.context = context;
        initUI();
    }

    public OngoinShareView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context = context;
        initUI();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void initUI() {
        View view = inflate(getContext(), R.layout.item_share_ongoing, this);
        ButterKnife.bind(this, view);
    }

    public void setShare(Share share) {

    }
}
