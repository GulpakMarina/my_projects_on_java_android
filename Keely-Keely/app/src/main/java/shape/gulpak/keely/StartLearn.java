package shape.gulpak.keely;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class StartLearn extends Fragment {
    private ArrayMap<String, String> arrayMapBase = new ArrayMap<>();
    private ArrayMap<String, String> arrayMap_for_next = new ArrayMap<>();
    private ArrayMap<String, String> arrayMap_for_write = new ArrayMap<>();
    ConstraintLayout[] cl;
    TextView[] tv1;
    EditText[] ev2;
    String mod="";
    private int flag=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.start_learn, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {//якщо було отримано готовий набір для вивчення
            String[] keys= bundle.getStringArray("keys");
            String[] values= bundle.getStringArray("values");
            int i=0;
            assert keys != null;
            for(; i<keys.length; i++){
                assert values != null;
                arrayMapBase.put(keys[i],values[i]);
            }
        }//компоненти ынтерфейсу
        cl= new ConstraintLayout[]{v.findViewById(R.id.CL1), v.findViewById(R.id.CL2), v.findViewById(R.id.CL3), v.findViewById(R.id.CL4), v.findViewById(R.id.CL5)};
        tv1= new TextView[]{v.findViewById(R.id.tV11), v.findViewById(R.id.tV21), v.findViewById(R.id.tV31), v.findViewById(R.id.tV41), v.findViewById(R.id.tV51)};
        ev2= new EditText[]{v.findViewById(R.id.tV12),v.findViewById(R.id.tV22),v.findViewById(R.id.tV32),v.findViewById(R.id.tV42),v.findViewById(R.id.tV52)};
        final Context context=getContext();
        FileOutputStream learntWordsOut;
        FileInputStream learntWordsIn;
        if(!arrayMapBase.isEmpty()){//якщо було отримано готовий набір для вивчення
            ToLayout();//виводжу на екран
            mod="S3";
            try{//перезаписую файл з словами не вивчиними
                learntWordsIn = context.openFileInput("word_to_learn");
                arrayMapBase.putAll(InputWithFile(arrayMapBase, learntWordsIn));
                learntWordsIn.close();
            } catch (IOException e) { e.printStackTrace(); }
            try {
                learntWordsOut = context.openFileOutput("word_to_learn", MODE_PRIVATE);
                for(Map.Entry<String, String> am: arrayMap_for_next.entrySet()){
                    arrayMapBase.remove(am.getKey());
                }
                OutputInFile(arrayMapBase, learntWordsOut);
                learntWordsOut.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
        else {//шукаю слова для повтору
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            assert context != null;
            String[] files=context.fileList();
            for(String str: files){
                if(str.charAt(0)=='S'){
                    try {
                        Date fileDate = dateFormat.parse(str.substring(2));
                        assert fileDate != null;
                        if(fileDate.compareTo(currentDate) <= 0){
                            String mod1=null,mod2=null;
                            //визначаємо через скільки днів зробити запис
                            if(str.charAt(1)=='3'){ mod1="S3"; mod2="S7"; }
                            else if(str.charAt(1)=='7'){ mod1="S7"; mod2="SM"; }
                            else if(str.charAt(1)=='M'){ mod1="SM"; mod2=null; }
                            learntWordsIn = context.openFileInput(mod1+dateFormat.format(fileDate));
                            arrayMapBase.putAll(InputWithFile(arrayMapBase, learntWordsIn));
                            learntWordsIn.close();
                            ToLayout();//вивожу на єеран 5 слів з файлу або скільки є
                            learntWordsOut = context.openFileOutput(mod1+dateFormat.format(fileDate), MODE_PRIVATE);
                            OutputInFile(arrayMapBase, learntWordsOut);
                            learntWordsOut.close();
                            if(flag!=0 && mod2!=null){
                                mod=mod2;
                            }
                        }
                    } catch (ParseException | IOException | NullPointerException ignored) {
                    }

                }}
            if(flag==0){
                try {//якщо слів для повтору немає, беру нові
                    learntWordsIn = context.openFileInput("word_to_learn");
                    arrayMapBase.putAll(InputWithFile(arrayMapBase, learntWordsIn));
                    learntWordsIn.close();
                    ToLayout();
                    learntWordsOut = context.openFileOutput("word_to_learn", MODE_PRIVATE);
                    OutputInFile(arrayMapBase, learntWordsOut);
                    learntWordsOut.close();
                    if(flag!=0){
                        mod="S3";
                    }
                } catch (FileNotFoundException e4) {//якщо нових немає пробую шукати для написання, тому переходжу у WriteWords, через activity
                } catch (IOException e) { } } }

        //якщовзагалі немає ніяких слів для вивчення, переходжу у WriteWords, через activity
        if(flag==0){ ((MainActivity) Objects.requireNonNull(getActivity())).Write_Words();}
        //при натиснення вивчені слова передаються у Write_Words\
        ImageView imV2=v.findViewById(R.id.imageView2);
        imV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayMap_for_write.putAll(arrayMap_for_next);
                if(mod==""){
                    int day=0;
                if(mod.charAt(1)=='M')day=30;
                else if(mod.charAt(1)=='7')day=7;
                else if(mod.charAt(1)=='3')day=3;
                Calendar cal = Calendar.getInstance();//отримує сьогоднішню дату
                cal.add(Calendar.DAY_OF_MONTH, day);//додаю дні
                Date currentDate = cal.getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String dateText = dateFormat.format(currentDate);
                assert context != null;
                try {//створюємо файл для цих слів щоб повторити їх через 3 дні
                    FileInputStream learntWordsInNext;
                    learntWordsInNext = context.openFileInput(mod+dateText);
                    arrayMap_for_write.putAll(InputWithFile(arrayMap_for_write, learntWordsInNext));//зчитує слова у масив з файлу, якщо він є, і додає нові слова з перевіркою, чи такі вже записані(слова перемішуються)
                    learntWordsInNext.close();}
                catch (IOException e) { e.printStackTrace(); }
                try {
                    FileOutputStream learntWordsOutNext;
                    learntWordsOutNext = context.openFileOutput(mod+dateText, MODE_PRIVATE);
                    OutputInFile(arrayMap_for_write, learntWordsOutNext);//записує слова з масиву у файл, якщо він є
                    learntWordsOutNext.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }}
                ((MainActivity) Objects.requireNonNull(getActivity())).Write_WordsAfterStartLearn(arrayMap_for_write);//перехід до перевірки виченого
            }
        });
        return v;
    }


    private void ToLayout(){//вивід слів
            Object[] keys=arrayMapBase.keySet().toArray();
            for (final Object key : keys) {
                if (flag >=5) break;
                tv1[flag].setText(key.toString());
                ev2[flag].setVisibility(View.VISIBLE);
                arrayMap_for_next.put(key.toString(),arrayMapBase.get(key));
                ev2[flag].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void afterTextChanged(Editable s) {
                        arrayMap_for_next.setValueAt(arrayMap_for_next.indexOfKey(key.toString()),s.toString());
                    }
                });
                flag++;
                arrayMapBase.remove(key);
            }
    }
    private ArrayMap<String, String> InputWithFile(ArrayMap<String, String> arrayMap, FileInputStream learntWords) throws IOException {//зчитуємо слова з файлу
        byte[] input = new byte[learntWords.available()];
        StringBuilder res= new StringBuilder();
        if(res.equals("")){
        while ((learntWords.read(input)) != -1) {
            res.append(new String(input, StandardCharsets.UTF_8));
        }}
        if(res.length()>3) {
            String[] str = res.toString().split("\n");
            for (String s : str) {
                int a = s.indexOf("{");
                int b = s.indexOf("}");
                String[] s_ = s.substring(a + 1, b).split(";");
                arrayMap.put(s_[0], s_[1]);
            }
            return  arrayMap;
        }
        return  arrayMap;
    }

    private void OutputInFile(ArrayMap<String, String> arrayMap, FileOutputStream learntWords) throws IOException {//перезаписую файл з словами
        if(arrayMap.size()!=0){
            StringBuilder toFile= new StringBuilder();
            for (ArrayMap.Entry<String,String> aM : arrayMap.entrySet()) {
                if(!aM.getKey().equals("")){
                    toFile.append("{").append(aM.getKey()).append(";").append(aM.getValue()).append("}\n");
                }}
            learntWords.write(toFile.toString().getBytes());
            }
        else{
            byte buffer = 0;
            learntWords.write(buffer); } }
}

