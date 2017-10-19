package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.api.APIClient;
import it.gruppoinfor.home2work.api.Account;
import it.gruppoinfor.home2work.models.Match;
import it.gruppoinfor.home2work.models.Share;

public class ShareDetailsDialog extends AlertDialog {

    @BindView(R.id.hostAvatar)
    CircleImageView hostAvatar;
    @BindView(R.id.guestAvatar)
    CircleImageView guestAvatar;
    @BindView(R.id.actionText)
    TextView actionText;
    @BindView(R.id.karmaView)
    TextView karmaView;
    @BindView(R.id.sharedDistanceView)
    TextView sharedDistanceView;
    @BindView(R.id.savedConsumptionView)
    TextView savedConsumptionView;
    @BindView(R.id.savedEmissionView)
    TextView savedEmissionView;

    public ShareDetailsDialog(Context context, Share share) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_share_details, null);
        ButterKnife.bind(this, view);
        setView(view);

        Account host = share.getMatch().getHost();
        Account guest = share.getMatch().getGuest();

        Glide.with(context)
                .load(host.getAvatarURL())
                .asBitmap()
                .placeholder(R.drawable.ic_user)
                .into(hostAvatar);

        Glide.with(context)
                .load(guest.getAvatarURL())
                .asBitmap()
                .placeholder(R.drawable.ic_user)
                .into(guestAvatar);

        if (APIClient.getAccount().getId().equals(guest.getId())) {
            actionText.setText("Condivisione con " + host.toString());
        } else {
            actionText.setText("Condivisione con " + guest.toString());
        }

        Match match = share.getMatch();

        karmaView.setText("+" + share.getKarma());

        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.CEILING);

        sharedDistanceView.setText(df.format(match.getLength()) + " km condivisi");
        savedConsumptionView.setText(df.format(match.getCunsumption()) + " l di carburante risparmiati");
        savedEmissionView.setText(df.format(match.getEmission()) + " g di CO2 evitati");

    }
}
