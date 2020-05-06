package shape.gulpak.keely;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class SettingsAndOther extends Fragment {
    TextView currentDateTime;
    CheckBox reminderCheck;
    CheckBox soundCheck;
    TextView soundTV;
    TextView timeTV;
    Calendar dateAndTime=Calendar.getInstance();
    Button bbt;
    public boolean sound;
    public boolean remind;
    public int min;
    public int hours;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {//якщо було отримано готовий набір для вивчення
            sound= bundle.getBoolean("sound");
            remind= bundle.getBoolean("remind");
            hours=bundle.getInt("hours");
            min=bundle.getInt("minute");
        }
        View v=inflater.inflate(R.layout.settings_and_other, container, false);
        reminderCheck = v.findViewById(R.id.reminderСheck);
        soundCheck = v.findViewById(R.id.soundСheck);
        soundTV=v.findViewById(R.id.sound);
        timeTV=v.findViewById(R.id.timeTV);
        currentDateTime=v.findViewById(R.id.timeCheck);
        currentDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), t,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE), true)
                        .show();
            }
        });
        Calendar timeOld=Calendar.getInstance();
        timeOld.set(Calendar.HOUR_OF_DAY,hours);
        timeOld.set(Calendar.MINUTE,min);
        setInitialDateTime(timeOld);
        if(!remind) WithoutRemid();
        else reminderCheck.setChecked(true);
        if(sound) soundCheck.setChecked(true);
        else soundCheck.setChecked(false);
        reminderCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    remind=true;
                    WithRemid();
                    setInitialDateTime(dateAndTime);
                } else {
                    remind=false;
                    WithoutRemid();
                }
            }
        });
        soundCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sound=true;
                } else {
                    sound=false;
                }
            }
        });




        return v;
    }

    private void WithoutRemid(){
        reminderCheck.setChecked(false);
        soundTV.setEnabled(false);
        soundCheck.setEnabled(false);
        currentDateTime.setEnabled(false);
        timeTV.setEnabled(false);
    }

    private void WithRemid(){
        reminderCheck.setChecked(true);
        soundTV.setEnabled(true);
        soundCheck.setEnabled(true);
        currentDateTime.setEnabled(true);
        timeTV.setEnabled(true);
    }

    // отображаем диалоговое окно для выбора времени

    // установка начальных даты и времени
    private void setInitialDateTime(Calendar time) {
        currentDateTime.setText(DateUtils.formatDateTime(getContext(),
                time.getTimeInMillis(),
                 DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime(dateAndTime);
        }
    };


    @Override
    public void onStop() {
        super.onStop();
        String time=currentDateTime.getText().toString();
        hours=Integer.parseInt(time.subSequence(0,time.indexOf(':')).toString());
        min=Integer.parseInt(time.subSequence(time.indexOf(':')+1,time.indexOf(' ')).toString());
        ((MainActivity)getActivity()).AfterSettings(remind,sound,hours,min);
    }
}
