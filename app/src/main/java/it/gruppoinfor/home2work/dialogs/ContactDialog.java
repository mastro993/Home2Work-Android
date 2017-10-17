package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.gruppoinfor.home2work.R;
import it.gruppoinfor.home2work.Tools;
import it.gruppoinfor.home2work.models.User;


public class ContactDialog extends AlertDialog {

    @BindView(R.id.msgView)
    LinearLayout msgView;
    @BindView(R.id.noContactsView)
    LinearLayout noContactsView;
    @BindView(R.id.phoneText)
    TextView phoneText;
    @BindView(R.id.phoneView)
    LinearLayout phoneView;
    @BindView(R.id.twitterText)
    TextView twitterText;
    @BindView(R.id.twitterView)
    LinearLayout twitterView;
    @BindView(R.id.facebookText)
    TextView facebookText;
    @BindView(R.id.facebookView)
    LinearLayout facebookView;
    @BindView(R.id.telegramText)
    TextView telegramText;
    @BindView(R.id.telegramView)
    LinearLayout telegramView;

    public ContactDialog(final Context context, final User.Contacts contacts) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_contacts, null);
        ButterKnife.bind(this, view);
        setView(view);

        msgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO messaging system
            }
        });

        boolean hasPhone = contacts.getPhone() != null;
        boolean hasTwitter = contacts.getTwitter() != null;
        boolean hasFacebook = contacts.getTwitter() != null;
        boolean hasTelegram = contacts.getTelegram() != null;

        if (hasPhone || hasTelegram || hasFacebook || hasTwitter) {
            noContactsView.setVisibility(View.GONE);
        }

        if (hasPhone) {
            phoneText.setText(contacts.getPhone());
            phoneView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contacts.getPhone()));
                    context.startActivity(intent);
                }
            });

        } else {
            phoneView.setVisibility(View.GONE);
        }

        if (hasTwitter) {
            twitterText.setText("@" + contacts.getTwitter());
            twitterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = Tools.getOpenTwitterIntent(context, contacts.getFacebook());
                    context.startActivity(intent);
                }
            });
        } else {
            twitterView.setVisibility(View.GONE);
        }

        if (hasFacebook) {
            facebookText.setText("fb.me/" + contacts.getFacebook());
            facebookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = Tools.getOpenFacebookIntent(context, contacts.getFacebook());
                    context.startActivity(intent);
                }
            });
        } else {
            facebookView.setVisibility(View.GONE);
        }

        if (hasTelegram) {
            telegramText.setText("@" + contacts.getTelegram());
            telegramView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + contacts.getTelegram()));
                    context.startActivity(intent);
                }
            });
        } else {
            telegramView.setVisibility(View.GONE);
        }


    }
}
