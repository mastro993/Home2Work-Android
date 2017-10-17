package it.gruppoinfor.home2work.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.models.Achievement;

public class AchievementDetailsDialog extends AlertDialog {


    @BindView(R.id.achievement_icon)
    ImageView achievementIcon;
    @BindView(R.id.achievement_title)
    TextView achievementTitle;
    @BindView(R.id.achievement_karma)
    TextView achievementKarma;
    @BindView(R.id.achievement_description)
    TextView achievementDescription;
    @BindView(R.id.achievement_date)
    TextView achievementDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

    @SuppressLint("InflateParams")
    public AchievementDetailsDialog(Context context, Achievement achievement) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_achievement_details, null);

        ButterKnife.bind(this, view);
        setView(view);

        Glide.with(context)
                .load(achievement.getIconURL())
                .asBitmap()
                .placeholder(R.drawable.ic_trophy)
                .into(achievementIcon);

        achievementTitle.setText(achievement.getName());
        achievementDescription.setText(achievement.getDescription());
        achievementKarma.setText("+" + achievement.getDelta());
        achievementDate.setText("Sbloccato il " + dateFormat.format(achievement.getDate()));

    }

}
