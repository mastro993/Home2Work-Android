package it.gruppoinfor.home2work.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2workapi.model.Share;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ShareStatusView extends RelativeLayout {

    @BindView(R.id.to_job_progress)
    ProgressBar toJobProgress;
    @BindView(R.id.to_home_progress)
    ProgressBar toHomeProgress;
    @BindView(R.id.from_home)
    ImageView fromHome;
    @BindView(R.id.to_job)
    ImageView toJob;
    @BindView(R.id.to_home)
    ImageView toHome;
    private Context context;

    public ShareStatusView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public ShareStatusView(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.context = context;
        initUI();
    }

    public ShareStatusView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context = context;
        initUI();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setStatus(int status) {
        switch (status) {
            case Share.TOJOB:
                fromHome.setAlpha(1.0f);
                toJob.setAlpha(0.4f);
                toHome.setAlpha(0.4f);
                toJobProgress.setIndeterminate(true);
                toHomeProgress.setVisibility(INVISIBLE);
                break;
            case Share.ARRIVED:
                fromHome.setAlpha(1.0f);
                toJob.setAlpha(1.0f);
                toHome.setAlpha(0.4f);
                toJobProgress.setIndeterminate(false);
                toJobProgress.setProgress(100);
                toHomeProgress.setVisibility(INVISIBLE);
                break;
            case Share.TOHOME:
                fromHome.setAlpha(1.0f);
                toJob.setAlpha(1.0f);
                toHome.setAlpha(0.4f);
                toJobProgress.setIndeterminate(false);
                toJobProgress.setProgress(100);
                toHomeProgress.setVisibility(VISIBLE);
                toHomeProgress.setIndeterminate(true);
                break;
            case Share.COMPLETED:
                fromHome.setAlpha(1.0f);
                toJob.setAlpha(1.0f);
                toHome.setAlpha(1.0f);
                toJobProgress.setIndeterminate(false);
                toJobProgress.setProgress(100);
                toHomeProgress.setVisibility(VISIBLE);
                toHomeProgress.setIndeterminate(false);
                toHomeProgress.setProgress(100);
                break;
        }
    }

    private void initUI() {
        View view = inflate(getContext(), R.layout.custom_share_status_view, this);
        ButterKnife.bind(this, view);
    }

}
