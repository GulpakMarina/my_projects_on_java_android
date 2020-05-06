package shape.gulpak.keely;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


import static android.content.Context.MODE_APPEND;


public class InputNewWords extends Fragment {
    private EditText editText1;
    private TextView textView1;
    private FileOutputStream newWordsOut;
    private FileInputStream newWordsIn;
    private ArrayMap<String, String> arrayMap = new ArrayMap<>();
    private ArrayMap<String, String> arrayMapNext = new ArrayMap<>();
    private String bufferTrans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_words, container, false);
        editText1 = v.findViewById(R.id.editText1);
        textView1 = v.findViewById(R.id.textView_settings);
        ImageView imageV4 = v.findViewById(R.id.imageV4_new_words);
        ImageButton imageButton1 = v.findViewById(R.id.imageButton1);
        final Context context = getContext();
        imageButton1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {//реакнція на натслення галочки
                                                if (editText1.getText().length() != 0) {
                                                    textView1.setText("");
                                                    textView1.setClickable(true);
                                                    textView1.setVisibility(View.VISIBLE);// текст стає видимим
                                                    EverWordIsButton(editText1.getText());//роблю з кожного слова кнопку для вибору
                                                }
                                            }
                                        }
        );
        imageV4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//реакція на натиснення на стрілу переходу до наступного етапу
                if (!arrayMap.isEmpty() && context != null) {//зберігання обраних слів у файл
                    try {
                        newWordsIn = context.openFileInput("word_to_learn");
                        InputWithFile();
                        newWordsIn.close();
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    try {
                        newWordsOut = context.openFileOutput("word_to_learn", MODE_APPEND);
                        OutputInFile();
                        newWordsOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ((MainActivity) Objects.requireNonNull(getActivity())).Start_LearnAfterInput(arrayMap);//перехід до вивчення
                    arrayMap.clear();
                }
            }
        });
        return v;
    }

    private void EverWordIsButton(Editable receivedText) {
        String text = receivedText.toString();
        final Spannable spannableString = new SpannableString(text);
        final String finalText = text + ".";
        final String[] str = finalText.split("");
        StringBuilder str_word = new StringBuilder();
        for (int i = 0; i < str.length; i++) {//шукаю серед тесту слова
            if (!"[. ,!?;:]".contains(str[i])) {
                str_word.append(str[i]);
            }
            if ("[. ,!?;:]".contains(str[i]) && str_word.length() != 0 && !"/n".contains(str[i])) {
                final String str_final = !str_word.toString().equals("i") ? str_word.toString().toLowerCase() : str_word.toString().toLowerCase();
                str_word = new StringBuilder();
                spannableString.setSpan(new ClickableSpan() {//надаю можливість натиску на слова
                    @Override
                    public void updateDrawState(@NotNull TextPaint ds) {
                        ds.setColor(ds.linkColor);
                        ds.setUnderlineText(false);
                        ds.setColor(Color.BLACK);
                    }

                    public void onClick(@NotNull View view) {
                        // new RetrieveFeedTask().execute("https://translate.google.com/translate_a/t?client=x&text={"+str_final+"}&hl=en&sl=en&tl=uk");

                        Toast.makeText(getContext(), bufferTrans, Toast.LENGTH_SHORT).show();
                        AddToArrayMap(str_final, "translate" + ", " + "translate2");
                        final ForegroundColorSpan style = new ForegroundColorSpan(Color.rgb(171, 171, 171));// змінює колір обраного слова
                        int pos=finalText.indexOf(str_final) + str_final.length();
                        spannableString.setSpan(style, finalText.indexOf(str_final), pos, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        textView1.setText(spannableString);
                    }
                },i - str_final.length(), i, 0);
            }

        }
        textView1.setText(spannableString);
        textView1.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onStop() {//призупинення фрагменту
        super.onStop();
        Context context = getContext();
        if (!arrayMap.isEmpty() && context != null) {//зберігання слів у файл
            try {
                newWordsIn = context.openFileInput("word_to_learn");
                InputWithFile();
                newWordsIn.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
            try {
                newWordsOut = context.openFileOutput("word_to_learn", MODE_APPEND);
                OutputInFile();
                newWordsOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  ((MainActivity) Objects.requireNonNull(getActivity())).Start_Learn(arrayMap);
            arrayMap.clear();
        }
    }

    private void AddToArrayMap(String str, String str_) {//зберігання у масив
        if (str_.length() > 0) {
            if (arrayMap.size() == 0) {
                arrayMap.put(str, str_);
                arrayMapNext.put(str, str_);
            } else if (!arrayMap.containsKey(str)) {
                arrayMap.put(str, str_);
                arrayMapNext.put(str, str_);
            }
        }
    }

    private void InputWithFile() throws IOException {//зчитую слова з файлу
        byte[] input = new byte[newWordsIn.available()];
        String res = "";
        if(res.equals("")){
        while ((newWordsIn.read(input)) != -1) {
            res += new String(input, StandardCharsets.UTF_8);
        }}
        if (res.length() > 3) {
            String[] str = res.split("\n");
            for (String s : str) {
                int a = s.indexOf("{");
                int b = s.indexOf("}");
                String[] s_ = s.substring(a + 1, b).split(";");
                arrayMap.put(s_[0], s_[1]);
            }
        }
    }

    private void OutputInFile() throws IOException {//записую оновлений список у файл
        if (arrayMap.size() != 0) {
            StringBuilder toFile = new StringBuilder();
            for (ArrayMap.Entry<String, String> aM : arrayMap.entrySet()) {
                if (!aM.getKey().equals("")) {
                    toFile.append("{").append(aM.getKey()).append(";").append(aM.getValue()).append("}\n");
                }
            }
            newWordsOut.write(toFile.toString().getBytes());
        } else {
            byte buffer = -1;
            newWordsOut.write(buffer);
        }
    }
}
//    class RetrieveFeedTask extends AsyncTask<String, Void, String> {
//        protected String doInBackground(String... urls) {
//            try {
//                URL url = new URL(urls[0]);
//                Context context=getContext();
//                File externalAppDir = new File(context.getPackageName());//Environment.getExternalStorageDirectory() + "/Android/data/"
//                if (!externalAppDir.exists()) {
//                    externalAppDir.mkdir();
//                }
//                File file = new File(externalAppDir , "FileName.txt");
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
//                String stringBuffer;
//                String string = "";
//                while ((stringBuffer = bufferedReader.readLine()) != null) {
//                    string = String.format("%s%s", string, stringBuffer);
//                }
//                return string;
//            } catch (Exception e) {
//                return null;
//            } finally {
//                //bufferedReader.close();
//            }
//        }
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            bufferTrans=s;
//        }
//    }

