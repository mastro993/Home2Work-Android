package it.gruppoinfor.home2work.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.models.Karma;

/**
 * Created by Federico on 26/02/2017.
 * <p>
 * Dialog per visualizzare le informazioni sul karma
 */

public class KarmaDetailsDialog extends AlertDialog.Builder {

    @BindView(R.id.karmaTotal)
    TextView karmaTotal;
    @BindView(R.id.karmaProgress)
    DonutProgress karmaProgress;
    @BindView(R.id.karmaToNextLevel)
    TextView karmaToNextLevel;

    @SuppressLint("InflateParams")
    public KarmaDetailsDialog(Context context, Karma karma) {
        super(context);

        View view = View.inflate(context, R.layout.dialog_karma_details, null);
        ButterKnife.bind(this, view);
        setView(view);

        karmaTotal.setText(karma.getKarma().toString());
        karmaProgress.setProgress(karma.getProgress());
        karmaToNextLevel.setText((karma.getForNextLevel() - karma.getKarma()) + " \n punti al prossimo livello");
    }

}
