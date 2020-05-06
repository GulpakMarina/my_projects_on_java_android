package shape.gulpak.keely;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "UserSettings";// файл для налвштувань
    public static final String APP_PREFERENCES_SOUND = "sound";
    public static final String APP_PREFERENCES_REMIND = "remind";
    public static final String APP_PREFERENCES_HOURS = "hours";
    public static final String APP_PREFERENCES_MINUTE = "minute";
    private SharedPreferences mSettings;
    BottomNavigationView bottomNavigationView;
    public static boolean change = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

//        for(String str: getBaseContext().fileList())
//        getBaseContext().deleteFile(str);

        Fragment nextFragment = new StartLearn();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, nextFragment).commit();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_second);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

    }
    public BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment frag = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_first:
                            if (change) frag = new InputNewWords();
                            break;
                        case R.id.nav_second:
                            if (change) frag = new StartLearn();
                            break;
                        case R.id.nav_third:
                            if (change){ frag = new SettingsAndOther();
                            Bundle bundle = new Bundle();
                                if (mSettings.contains(APP_PREFERENCES_REMIND)) bundle.putBoolean("remind", mSettings.getBoolean(APP_PREFERENCES_REMIND, false));
                                else bundle.putBoolean("remind", false);
                                if (mSettings.contains(APP_PREFERENCES_SOUND)) bundle.putBoolean("sound", mSettings.getBoolean(APP_PREFERENCES_SOUND, false));
                                else bundle.putBoolean("sound", false);
                                if (mSettings.contains(APP_PREFERENCES_HOURS)) bundle.putInt("hours",mSettings.getInt(APP_PREFERENCES_HOURS, 13));
                                else bundle.putInt("hours",13);
                                if (mSettings.contains(APP_PREFERENCES_MINUTE)) bundle.putInt("minute",mSettings.getInt(APP_PREFERENCES_MINUTE, 0));
                                else bundle.putInt("minute",0);
                                frag.setArguments(bundle);}
                            break;
                    }
                    if (change) {
                        assert frag != null;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, frag).commit();
                    }
                    return true;
                }
            };

    public void Write_Words() {
        FileInputStream learntWordsIn;
        ArrayMap<String, String> arrayMapBase = new ArrayMap<>();
        final Context context = getBaseContext();
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String[] files = context.fileList();
        //шукаю файл з словам для повторення
        change = false;
        for (String str : files) {
            if (str.charAt(0) == 'W') {
                try {
                    Date fileDate = dateFormat.parse(str.substring(2));
                    assert fileDate != null;
                    if (fileDate.compareTo(currentDate) <= 0) {
                        String mod1 = null, mod = null;
                        if (str.charAt(1) == '3') {
                            mod1 = "W3";
                            mod = "W7";
                        } else if (str.charAt(1) == '7') {
                            mod1 = "W7";
                            mod = "WM";
                        } else if (str.charAt(1) == 'M') {
                            mod1 = "WM";
                            mod = null;
                        } else if (str.charAt(1) == '0') {
                            mod1 = "W0";
                            mod = "W3";
                        }
                        try {
                            learntWordsIn = context.openFileInput(mod1 + dateFormat.format(fileDate));
                            arrayMapBase.putAll(InputWithFile(arrayMapBase, learntWordsIn));
                            learntWordsIn.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (arrayMapBase.size() > 0) {
                            Fragment nextFragment = new WriteWords();
                            bottomNavigationView.setSelectedItemId(R.id.nav_second);
                            change = true;
                            Bundle bundle = new Bundle();
                            String[] k = new String[arrayMapBase.size()];
                            String[] v = new String[arrayMapBase.size()];
                            int i = 0;
                            for (Map.Entry entry : arrayMapBase.entrySet()) {
                                k[i] = String.valueOf(entry.getKey());
                                v[i] = String.valueOf(entry.getValue());
                                i++;
                            }
                            bundle.putStringArray("keys", k);
                            bundle.putStringArray("values", v);
                            bundle.putString("mod", mod);
                            bundle.putString("mod1", mod1);
                            bundle.putString("fileDate", dateFormat.format(fileDate));
                            nextFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, nextFragment).commit();
                            return;
                        } else getBaseContext().deleteFile(mod1 + dateFormat.format(fileDate));
                    }
                } catch (ParseException | NullPointerException ignored) {
                }
            }
        }
        if (!change) {
            RequiredNewWords();
        }

    }

    public void Start_Learn() {
        FileInputStream learntWordsIn;
        ArrayMap<String, String> arrayMapBase = new ArrayMap<>();
        final Context context = getBaseContext();
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String[] files = context.fileList();
        //шукаю файл з словам для повторення
        change = false;
        for (String str : files) {
            if (str.charAt(0) == 'S') {
                try {
                    Date fileDate = dateFormat.parse(str.substring(2));
                    assert fileDate != null;
                    if (fileDate.compareTo(currentDate) <= 0) {
                        String mod1 = null, mod = null;
                        if (str.charAt(1) == '3') {
                            mod1 = "S3";
                            mod = "S7";
                        } else if (str.charAt(1) == '7') {
                            mod1 = "S7";
                            mod = "SM";
                        } else if (str.charAt(1) == 'M') {
                            mod1 = "SM";
                            mod = null;
                        } else if (str.charAt(1) == '0') {
                            mod1 = "W0";
                            mod = "W3";
                        }
                        try {
                            learntWordsIn = context.openFileInput(mod1 + dateFormat.format(fileDate));
                            arrayMapBase.putAll(InputWithFile(arrayMapBase, learntWordsIn));
                            learntWordsIn.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (arrayMapBase.size() > 0) {
                            Fragment nextFragment = new WriteWords();
                            bottomNavigationView.setSelectedItemId(R.id.nav_second);
                            change = true;
                            Bundle bundle = new Bundle();
                            String[] k = new String[arrayMapBase.size()];
                            String[] v = new String[arrayMapBase.size()];
                            int i = 0;
                            for (Map.Entry entry : arrayMapBase.entrySet()) {
                                k[i] = String.valueOf(entry.getKey());
                                v[i] = String.valueOf(entry.getValue());
                                i++;
                            }
                            bundle.putStringArray("keys", k);
                            bundle.putStringArray("values", v);
                            bundle.putString("mod", mod);
                            bundle.putString("mod1", mod1);
                            bundle.putString("fileDate", dateFormat.format(fileDate));
                            nextFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, nextFragment).commit();
                            return;
                        } else getBaseContext().deleteFile(mod1 + dateFormat.format(fileDate));
                    }
                } catch (ParseException | NullPointerException ignored) {
                }
            }
        }
        if (!change) {
            RequiredNewWords();
        }

    }


    public void RequiredNewWords() {
        Fragment nextFragment = new InputNewWords();
        change = false;
        bottomNavigationView.setSelectedItemId(R.id.nav_first);
        change = true;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, nextFragment).commit();
    }

    public void Start_LearnAfterInput(ArrayMap<String, String> arrayMap) {
        Fragment nextFragment = new StartLearn();
        Bundle bundle = new Bundle();
        String[] k = new String[arrayMap.size()];
        String[] v = new String[arrayMap.size()];
        int i = 0;
        for (Map.Entry entry : arrayMap.entrySet()) {
            k[i] = String.valueOf(entry.getKey());
            v[i] = String.valueOf(entry.getValue());
            i++;
        }
        bundle.putStringArray("keys", k);
        bundle.putStringArray("values", v);
        bundle.putString("mod", "W3");
        bundle.putString("mod1", null);
        bundle.putString("fileDate", null);
        nextFragment.setArguments(bundle);
        change = false;
        bottomNavigationView.setSelectedItemId(R.id.nav_second);
        change = true;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, nextFragment).commit();
    }

    public void Write_WordsAfterStartLearn(ArrayMap<String, String> arrayMap) {
        Fragment nextFragment = new WriteWords();
        Bundle bundle = new Bundle();
        String[] k = new String[arrayMap.size()];
        String[] v = new String[arrayMap.size()];
        int i = 0;
        for (Map.Entry entry : arrayMap.entrySet()) {
            k[i] = String.valueOf(entry.getKey());
            v[i] = String.valueOf(entry.getValue());
            i++;
        }
        bundle.putStringArray("keys", k);
        bundle.putStringArray("values", v);
        nextFragment.setArguments(bundle);
        change = false;
        bottomNavigationView.setSelectedItemId(R.id.nav_second);
        change = true;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_conteiner, nextFragment).commit();
    }

    public ArrayMap<String, String> InputWithFile(ArrayMap<String, String> arrayMap, FileInputStream learntWords) throws IOException {
        if (learntWords != null) {
            byte[] input = new byte[learntWords.available()];
            String res = "";
            if (res.equals("")) {
                while ((learntWords.read(input)) != -1) {
                    res += new String(input, StandardCharsets.UTF_8);
                }
            }
            if (res.length() > 3) {
                String[] str = res.split("\n");
                for (String s : str) {
                    int a = s.indexOf("{");
                    int b = s.indexOf("}");
                    String[] s_ = s.substring(a + 1, b).split(";");
                    arrayMap.put(s_[0], s_[1]);
                }
                return arrayMap;
            }
        }
        return arrayMap;
    }


    public void AfterSettings(boolean r, boolean s, int h, int m){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(APP_PREFERENCES_SOUND, r);
        editor.putBoolean(APP_PREFERENCES_REMIND, s);
        editor.putInt(APP_PREFERENCES_HOURS, h);
        editor.putInt(APP_PREFERENCES_MINUTE, m);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSettings.contains(APP_PREFERENCES_REMIND) && mSettings.getBoolean(APP_PREFERENCES_REMIND, false)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            if (mSettings.contains(APP_PREFERENCES_HOURS)) calendar.set(Calendar.HOUR_OF_DAY,mSettings.getInt(APP_PREFERENCES_HOURS, 13));
            else calendar.set(Calendar.HOUR_OF_DAY,13);
            if (mSettings.contains(APP_PREFERENCES_MINUTE)) calendar.set(Calendar.MINUTE,mSettings.getInt(APP_PREFERENCES_MINUTE, 0));
            else calendar.set(Calendar.MINUTE,0);

            Intent intent = new Intent(this, TimeNotification.class);
            if (mSettings.contains(APP_PREFERENCES_SOUND)) intent.putExtra("sound", mSettings.getBoolean(APP_PREFERENCES_SOUND, false));
            else intent.putExtra("sound", false);
            intent.putExtra("MainActivity", "some text");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}