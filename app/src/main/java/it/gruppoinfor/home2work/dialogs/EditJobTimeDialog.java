package it.gruppoinfor.home2work.dialogs;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.gruppoinfor.home2work.Converters;
import it.gruppoinfor.home2work.R;


public class EditJobTimeDialog extends AlertDialog {

    @BindView(R.id.startInput)
    EditText startInput;
    @BindView(R.id.endInput)
    EditText endInput;

    private Context context;
    private Calendar calendar;
    private Callback callback;

    public EditJobTimeDialog(Context context, Callback callback) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_edit_job_time, null);
        ButterKnife.bind(this, view);
        setView(view);
        setTitle("Modifica orari lavoro");

        this.context = context;
        this.calendar = Calendar.getInstance();
        this.callback = callback;

        startInput.clearFocus();
        endInput.clearFocus();
    }

    @OnClick(R.id.startInput)
    void editStart() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String formattedMinute = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                startInput.setText(selectedHour + ":" + formattedMinute);
            }
        }, hour, minute, true);

        mTimePicker.setTitle(R.string.config_job_start_selection);
        mTimePicker.show();
    }

    @OnClick(R.id.endInput)
    void editEnd() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        final TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String formattedMinute = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                endInput.setText(selectedHour + ":" + formattedMinute);

            }
        }, hour, minute, true);

        mTimePicker.setTitle(R.string.config_job_end_selection);
        mTimePicker.show();
    }

    @OnClick(R.id.saveButton)
    void save() {
        String startText = startInput.getText().toString();
        String endText = endInput.getText().toString();

        Long start = Converters.timeToTimestamp(startText);
        Long end = Converters.timeToTimestamp(endText);

        callback.onSave(this, start, end);
    }


    public interface Callback {
        void onSave(AlertDialog dialog, Long start, Long end);
    }
}
