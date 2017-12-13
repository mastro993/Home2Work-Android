package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.custom.ShareStatusView;
import it.gruppoinfor.home2workapi.Client;
import it.gruppoinfor.home2workapi.model.Share;
import it.gruppoinfor.home2workapi.model.User;

public class OngoingShareInfoDialog extends AlertDialog.Builder {
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.user_name_view)
    TextView userNameView;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.karma_preview)
    TextView karmaPreview;
    @BindView(R.id.exp_preview)
    TextView expPreview;
    @BindView(R.id.share_status_view)
    ShareStatusView shareStatusView;
    @BindView(R.id.status_text)
    TextView statusText;

    public OngoingShareInfoDialog(Context context, Share share) {
        super(context);
        View view = View.inflate(getContext(), R.layout.dialog_ongoing_share_info, null);
        ButterKnife.bind(this, view);
        setView(view);

        shareStatusView.setStatus(share.getStatus());

        User user;
        boolean ishost;

        if (share.getBooking().getBookedMatch().getGuest().getId() == Client.getSignedUser().getId()) {
            user = share.getBooking().getBookedMatch().getHost();
            ishost = false;
        } else {
            user = share.getBooking().getBookedMatch().getGuest();
            ishost = true;
        }

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar_placeholder);
        Glide.with(context)
                .load(user.getAvatarURL())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .into(userAvatar);

        userNameView.setText(user.toString());

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        Double kmDistance = share.getBooking().getBookedMatch().getDistance() / 1000.0;
        int karmaPoints;
        int exp;

        if (ishost) {
            karmaPoints = (int) (kmDistance.intValue() * 1.2);
            exp = (int) (kmDistance * 12);
        } else {
            karmaPoints = (int) (kmDistance.intValue() * 1.2);
            exp = (int) (kmDistance * 12);
        }

        karmaPreview.setText(String.format(context.getResources().getString(R.string.match_karma_preview), karmaPoints));
        expPreview.setText(String.format(context.getResources().getString(R.string.match_exo_preview), exp));
    }
}
