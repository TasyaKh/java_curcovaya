package com.example.bd.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;

import com.example.bd.Logic.Word;
import com.example.bd.Logic.WordStatistic;
import com.example.bd.R;

import java.util.ArrayList;

//Всплывающий диалог (вызывается, когда во время игры хотим узнать результат наших попыток)
public class Fragment_AlertDialog extends AppCompatDialogFragment {

    private enum SeeTranslate{
        WITH, WITHOUT
    }

    private ArrayList<WordStatistic> wordStatistics;    //слова со статистикой
    private ArrayList<WordStatistic> deletedWords;      //слова, с которыми закончили игру
    private SeeTranslate seeTranslate;                  //Можно ли просмотреть перевод слова на родном языке

    private DialogListAdapter myAdapter;                //адаптер для листа для просмотра списка слов

    private String titleOne;
    private String titleTwo;

    public Fragment_AlertDialog(){
        titleOne = titleTwo = "";
        seeTranslate = SeeTranslate.WITHOUT;
    }
    public Fragment_AlertDialog(String titleOne, String titleTwo){
       this.titleOne = titleOne;
       this.titleTwo = titleTwo;
        seeTranslate = SeeTranslate.WITHOUT;
    }

    //Задать View и обработаь событие  по нажатию кнопки сверху
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_alert_dialog,container);
       // wordStatistics = new ArrayList<>();
        myAdapter = new DialogListAdapter(getContext(), wordStatistics);
        ListView listView = root.findViewById(R.id.listView);

        listView.setAdapter(myAdapter);
        //При нажатии на любой элемент разрешить просмотр перевода на родной язык для всех элементов
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(myAdapter.getArrayMyData()!=deletedWords) {
                    if (seeTranslate == SeeTranslate.WITHOUT) seeTranslate = SeeTranslate.WITH;
                    else seeTranslate = SeeTranslate.WITHOUT;
                    updateList();
                }
            }
        });

        ImageButton seeWords = root.findViewById(R.id.correct_all);
        TextView title = root.findViewById(R.id.title);
        //Просмотреть слова (правильные или все оставшиеся)
        seeWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder y = new NotificationCompat.Builder(getContext(),"1");
                int idIcon = 0;

                if(myAdapter.getArrayMyData()==wordStatistics) {
                    idIcon = R.drawable.ic_navigate_before;
                    myAdapter.setArrayMyData(deletedWords);
                    seeTranslate = SeeTranslate.WITH;
                    title.setText(titleOne);
                }
                else {
                    idIcon = R.drawable.ic_navigate_next;
                    myAdapter.setArrayMyData(wordStatistics);
                    seeTranslate = SeeTranslate.WITHOUT;
                    title.setText(titleTwo);
                }

                if(idIcon>0)
                    y.setSmallIcon(idIcon);

                seeWords.setImageIcon(y.build().getSmallIcon());
                updateList();
            }
        });

        return root;
    }
    //обновить ListView
    private void updateList () {
        //каждый раз отправляем данные по новой
        myAdapter.notifyDataSetChanged();
    }
    //Задать массив со словами
    public void setWordStatistics(ArrayList<WordStatistic> wordStatistics){
        this.wordStatistics = wordStatistics;
    }
    //фзфадать массив с удаленными словами (зпаершенными)
    public void setDeletedWords(ArrayList<WordStatistic> deletedWords){
        this.deletedWords = deletedWords;
    }

//    public void setTitle {
//           TextView title = getView().findViewById(R.id.title);
//
//    }

    public ArrayList<WordStatistic> getDeletedWords() {
        return deletedWords;
    }

    //    //По событию show
//    @Override
//    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
//        super.show(manager, tag);
//    }
    //Срабатывает при закрытии Dialog
    @Override
    public void dismiss() {
        super.dismiss();
        wordStatistics = null;
        deletedWords = null;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        seeTranslate = SeeTranslate.WITHOUT;
    }

    //адаптер отвечающий за показ слов и текуще  статистики
    class DialogListAdapter extends BaseAdapter {

        private final int correctMin = 3;                 //Допустимое число некорректных слов
        private LayoutInflater mLayoutInflater;           //привязывает все лайоуты (прямоугольники со словами к фрагменту)
        private ArrayList<WordStatistic> arrayMyWords;    //Массив со словами статистики

        public DialogListAdapter(Context ctx, ArrayList<WordStatistic> arr) {

            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayMyData(arr);

        }
        //получть слова
        public ArrayList<WordStatistic> getArrayMyData() {
            return arrayMyWords;
        }
        //задать слова
        public void setArrayMyData(ArrayList<WordStatistic> arrayMyData) {
            this.arrayMyWords = arrayMyData;
        }
        //получить размер массива со словами
        public int getCount() {
            if(arrayMyWords!=null)
            return arrayMyWords.size();
            else return 0;
        }
        //Получить элемент с массива со словами по индексу
        public Word getItem(int position) {
            Word wr = arrayMyWords.get(position);

            return wr;
        }
        //Получить id элемента по индексу
        public long getItemId(int position) {
            Word wr = arrayMyWords.get(position);

            if (wr != null) {
                return wr.getId();
            }
            return 0;
        }
        //Получить Layout, где прописываются слова
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_design, null);
            }

            TextView id = convertView.findViewById(R.id.id_num);
            TextView ru= convertView.findViewById(R.id.ru);
            TextView en =convertView.findViewById(R.id.en);
            LinearLayout wordsLayout = convertView.findViewById(R.id.words_theme);

            WordStatistic wr = arrayMyWords.get(position);

            id.setBackgroundColor(getColorByPriority(wr.getPriority()));

            String txt = "";
            int uncorrectWords = wr.getAllAttempts()-wr.getCountCorrect();
            id.setText(wr.getCountCorrect()+"/\n"+uncorrectWords);

            txt = wr.getEnglishWord();
            int maxLength = getResources().getInteger(R.integer.max_length_list);

            en.setText(constraintSizeWord(maxLength,txt));

            txt = wr.getRuWord();
            if(seeTranslate==SeeTranslate.WITH)
                ru.setText(constraintSizeWord(maxLength,txt));
            else ru.setText("");

            //получить цвет количества правильных и неправильных слов
            int colorCorrIncorr = getColorCorrectIncorrect(wr);
            //установить полученный цвет
            id.setTextColor(colorCorrIncorr);
            wordsLayout.setBackgroundColor(colorCorrIncorr);

            return convertView;
        }
        //ограничить размер отображения слов
        private String constraintSizeWord(int maxLength, String word){
            if (word.length() > maxLength - 3)
                word = word.substring(0, maxLength - 4) + "...";
            return word;
        }
        //Получить цвет в соответствии с количеством правильных и неправильныъ слов
        private int getColorCorrectIncorrect(WordStatistic wr){
            int color = -1;

                int incorrect = wr.getAllAttempts()-wr.getCountCorrect();
                int correct = wr.getCountCorrect();

                if (incorrect< correct)
                    color = getResources().getColor(R.color.blue_A100);
                else if(incorrect==correct)color = getResources().getColor(R.color.white);
                else color = getResources().getColor(R.color.pink_A100);

            return color;

        }

        //в зависимости от приоритета слова добавляем цветной блок слева
        private int getColorByPriority(int priority) {
            Resources res = getResources();
            int color = 0;

            switch (priority) {
                case 0:
                    color = 0;
                    break;
                case 1:
                    color= res.getColor(R.color.teal_A100, res.newTheme());
                    break;
                case 2:
                    color = res.getColor(R.color.green_A100, res.newTheme());
                    break;
            }

            return color;
        }
    } // end myAdapter
}
