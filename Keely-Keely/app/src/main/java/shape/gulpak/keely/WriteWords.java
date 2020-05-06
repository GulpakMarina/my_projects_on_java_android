package shape.gulpak.keely;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


import static android.content.Context.MODE_PRIVATE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class WriteWords extends Fragment {
    private ArrayMap<String, String> arrayMapBase = new ArrayMap<>();
    private ArrayMap<String, String> arrayMap_rep = new ArrayMap<>();
    private ArrayMap<String, String> arrayMap_for_next = new ArrayMap<>();
    private TextView textView1, tv;
    private static int hints=0;
    private EditText editText;
    private ImageView image;
    private ImageView iV_check,iV;
    private View div;
    private FileInputStream learntWordsInNext;
    private FileOutputStream learntWordsOut,learntWordsOutNext;
    private String mod="",key,mod1="",fileDate,fileDateOutput;
    private int maxHints,d,f,flag=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String[] keys= bundle.getStringArray("keys");
            String[] values= bundle.getStringArray("values");
            assert keys != null;
            for(int i = 0; i<keys.length; i++){
                assert values != null;
                arrayMapBase.put(keys[i],values[i]);
            }
        }
        assert bundle != null;
        mod=bundle.getString("mod");
        mod1=bundle.getString("mod1");
        fileDate=bundle.getString("fileDate");
        View v=inflater.inflate(R.layout.write_words, container, false);
        textView1 = v.findViewById(R.id.tV_write_words);
        image=v.findViewById(R.id.iV2_write_words);
        editText = v.findViewById(R.id.eT_write_words);
        tv=v.findViewById(R.id.textView_settings);
        div=v.findViewById(R.id.divider_write);
        final Context context=getContext();
        if(!arrayMapBase.isEmpty()){//якщо було отримано готовий набір для вивчення
            ToLayout(arrayMapBase);
            f=0;
            if(mod==null || mod1==null)mod="W3";
        }
        else {  ((MainActivity) Objects.requireNonNull(getActivity())).Write_Words(); }
        editText=v.findViewById(R.id.eT_write_words);
        iV=v.findViewById(R.id.iV_hint);
        iV.setOnClickListener(new View.OnClickListener() {//обробник події натиснення підказки
            @Override
            public void onClick(View v) {
                if(key.length()>0) {//якщо ключ існує
                    if(hints<maxHints)OnClickForHint();
                    else
                    {
                        assert context != null;
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        arrayMapBase.remove(key);
                        arrayMap_rep.put(key,textView1.getText().toString());
                        String k=key;
                        ChangeToNextWord();
                        Toast.makeText(getContext(), k+" - це слово варто повторити", Toast.LENGTH_SHORT).show();
                    } } }});
        iV_check=v.findViewById(R.id.iV_check);
        iV_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val=textView1.getText().toString();
                if (key.length() > 0) {
                    if(hints>maxHints){
                        assert context != null;
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        arrayMapBase.remove(key);
                        arrayMap_rep.put(key,textView1.getText().toString());
                        String k=key;
                        ChangeToNextWord();
                        Toast.makeText(getContext(), k+" - це слово варто повторити", Toast.LENGTH_SHORT).show();
                    }
                    else {if (key.equals(editText.getText().toString().trim())) {
                        arrayMap_for_next.put(key,val);
                        arrayMapBase.remove(key);
                        ChangeToNextWord();
                    } else editText.selectAll();} } }});
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!arrayMapBase.isEmpty() || !arrayMap_rep.isEmpty()) {
            arrayMapBase.putAll(arrayMap_rep);
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Context context = getContext();
            if (f == 0) {
                try {
                    assert context != null;
                    FileInputStream learntWordsIn = context.openFileInput("W0" + dateFormat.format(currentDate));
                    arrayMapBase.putAll(InputWithFile(arrayMapBase, learntWordsIn));
                    learntWordsIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    learntWordsOut = context.openFileOutput("W0" + dateFormat.format(currentDate), MODE_PRIVATE);
                    fileDateOutput="W0" + dateFormat.format(currentDate);
                    OutputInFile(arrayMapBase, learntWordsOut);
                    learntWordsOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } } }
        if(!arrayMap_for_next.isEmpty())
            if (!mod.equals("")) {
                int i;
                if (mod.contains("3")) i = 3;
                else if (mod.contains("7")) i = 7;
                else i = 30;
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, i);
                Date currentDate = cal.getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String dateText = dateFormat.format(currentDate);
                Context context = getContext();
                try{
                    assert context != null;
                    learntWordsInNext = context.openFileInput(mod + dateText);
                arrayMap_for_next.putAll(InputWithFile(arrayMap_for_next, learntWordsInNext));
                learntWordsInNext.close();}
                catch (IOException e) { e.printStackTrace(); }
                try{
                learntWordsOutNext = context.openFileOutput(mod + dateText, MODE_PRIVATE);
                fileDateOutput=mod + dateText;
                OutputInFile(arrayMap_for_next, learntWordsOutNext);
                learntWordsOutNext.close();}
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    private void ToLayout(ArrayMap<String, String> arrayMap) {
        for(Map.Entry<String, String> word : arrayMap.entrySet()) {//виводимо ОДНЕ слово на екран
            textView1.setText(word.getValue());
            editText.setText("");
            key=word.getKey();
            if(d==0)image.setImageResource(R.drawable.sherlock);
            else image.setImageResource(R.drawable.training);
            hints=0;
            if(key.length()>5)maxHints=3;
            else if(key.length()>=2)maxHints=1;
                else maxHints=0;
            break; }
    }
    private void ChangeToNextWord(){
        if(!arrayMapBase.isEmpty()){
            ToLayout(arrayMapBase);
        }
        else{
            if(!arrayMap_rep.isEmpty()){
                arrayMapBase.putAll(arrayMap_rep);
                d=1;
                arrayMap_rep.clear();flag++;
                ChangeToNextWord();
            }
            else{
                image.setImageResource(R.drawable.greeting);
                LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) image.getLayoutParams();
                params.width = MATCH_PARENT;
                params.height = MATCH_PARENT;
                image.setLayoutParams(params);
                textView1.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                if(f==1)tv.setText("Повторено усі слова на сьогодні");
                else tv.setText("Обрані слова вивчені і будуть повторенні через 3 дні");
                div.setVisibility(View.GONE);
                iV.setVisibility(View.GONE);
                iV_check.setVisibility(View.GONE);
                try{
                    Context context = getContext();
                    assert context != null;
                    learntWordsOut = context.openFileOutput(mod1+fileDate, MODE_PRIVATE);
                    fileDateOutput=mod1+fileDate;
                    OutputInFile(arrayMapBase,learntWordsOut);
                    learntWordsOut.close();
                }
                catch (IOException | NullPointerException e ) { e.printStackTrace(); }}
                if(mod!=null){
                    int i;
                    if(mod.contains("3"))i=3;
                    else if(mod.contains("7")) i=7;
                    else i=30;
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_MONTH, i);
                    Date currentDate = cal.getTime();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String dateText = dateFormat.format(currentDate);
                    Context context=getContext();
                    try {
                        assert context != null;
                        learntWordsInNext = context.openFileInput(mod + dateText);
                        arrayMap_for_next.putAll(InputWithFile(arrayMap_for_next,learntWordsInNext));
                        learntWordsInNext.close();
                    } catch (IOException e) { e.printStackTrace(); }
                    try{
                        learntWordsOutNext = context.openFileOutput(mod + dateText, MODE_PRIVATE);
                        fileDateOutput=mod + dateText;
                        OutputInFile(arrayMap_for_next,learntWordsOutNext);
                        learntWordsOutNext.close();
                        arrayMap_for_next.clear();
                    } catch (IOException e) { e.printStackTrace(); }
                    //((MainActivity)getActivity()).Write_Words();
                }
        }
    }
    private void OnClickForHint(){
        StringBuilder res = new StringBuilder(editText.getText().toString());
        char[] key_ = key.toCharArray();
        int r = key_.length - res.length();
        int t = 0;
        while (r > t) {
            res.append(" "); t++;}// додаємо пробіли щоб вирівнчти довжину введеного слова і ключа
        char[] res_ = res.toString().toCharArray();
        if (key_.length == res_.length) {//якщо введене слово має таку ж довжину як ключ
            int j = 0;
            while (key_.length > j) {
                if (key_[j] == res_[j]) j++;//знаходмо розбіжність між словами
                else {
                    String new_res = res.substring(0, j) + key_[j];//якщщо розбіжність є, змінюємо на текст до першої розбіжності і додаємо вірний символ
                    editText.setText(new_res);
                    editText.setSelection(new_res.length());
                    hints++;
                    break; } } }
        else {//якщо введене слово довше ніж ключ
            int j = 0;
            String new_res="";
            while (key_.length > j) {
                if (key_[j] == res_[j]){  new_res+=key_[j];j++;}//знаходмо розбіжність між словами
                else {
                    new_res+=key_[j];//якщщо розбіжність є, змінюємо на текст до першої розбіжності і додаємо вірний символ
                    editText.setText(new_res);
                    editText.setSelection(new_res.length());
                    hints++;
                    break;
                }
            }
            if (key_.length == j) {//якщо додано лишні символи
               //якщщо розбіжність є, змінюємо на текст до першої розбіжності
                editText.setText(new_res);
                editText.setSelection(new_res.length());
                hints++;
            }
        }
    }
    private ArrayMap<String, String> InputWithFile(ArrayMap<String, String> arrayMap, FileInputStream learntWords) throws IOException {
        byte[] input = new byte[learntWords.available()];
        StringBuilder res = new StringBuilder();
        if (res.equals("")) {
            while ((learntWords.read(input)) != -1) {
                res.append(new String(input, StandardCharsets.UTF_8));
            }
        }
        if (res.length() > 3) {
            String[] str = res.toString().split("\n");
            for (String s : str) {
                int a = s.indexOf("{");
                int b = s.indexOf("}");
                String[] s_ = s.substring(a + 1, b).split(";");
                arrayMap.put(s_[0], s_[1]);
            }
            return arrayMap;
        }
        return arrayMap;
    }

    private void OutputInFile(ArrayMap<String, String> arrayMap, FileOutputStream learntWords) throws IOException {
        if(arrayMap.size()!=0){
            StringBuilder toFile= new StringBuilder();
            for (ArrayMap.Entry<String,String> aM : arrayMap.entrySet()) {
                if(!aM.getKey().equals("")){
                    toFile.append("{").append(aM.getKey()).append(";").append(aM.getValue()).append("}\n");
                }
            }
            learntWords.write(toFile.toString().getBytes());
        }
        else{
            Objects.requireNonNull(getContext()).deleteFile(fileDateOutput);
        }
    }
}