package com.example.bd.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bd.Logic.BDWords;
import com.example.bd.Logic.LanguageWord;
import com.example.bd.Logic.PlayWithWords;
import com.example.bd.Logic.SaveGameResults;
import com.example.bd.Logic.Sorter;
import com.example.bd.Logic.WordStatistic;
import com.example.bd.Logic.WriteRead;
import com.example.bd.R;
import com.example.bd.databinding.FragmentPlayBinding;

import java.util.ArrayList;
import java.util.Objects;

public class Fragment_Play extends Fragment {

    private String saveGame;

    private Fragment_AlertDialog fragmentAlertDialog;  //Отобразить статистику

    private FragmentPlayBinding binding;               //Связыватель

    private LanguageWord priorityTranslate;            //Какое слово в какое поле
    private EditText translate;                        //писать перевод слова сюда

    private FPlay_Animation playAnimation;             //различные анимации для этого фрагмента
    private PlayWithWords playWithWords;               //Здесь выбирается случайное слово по какому либо фильтру


    //Привязать View и инициализировать поля
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPlayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        playAnimation = new FPlay_Animation(root);
        setHasOptionsMenu(true);     //Задать справа сверху небольшое меню для выбора сортировки слов

        BDWords bdWords = new BDWords(getContext());      //Получить базу со словами
        playWithWords = new PlayWithWords(bdWords); //Отправить базу в сортировщик
        playWithWords.refillWords();
        //playWithWords.setRandomWord();

        translate =  root.findViewById(R.id.translate);

        fragmentAlertDialog = new Fragment_AlertDialog("Game1","Game2");

        return root;
    }
    //Обработать события
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveGame = getString(R.string.save_game);

        ImageButton lastGame = view.findViewById(R.id.last_game);

        lastGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveGameResults saveGameResults =  WriteRead.read(Objects.requireNonNull(getContext()),saveGame);
                FragmentManager manager = getParentFragmentManager();
                Fragment_AlertDialog lastGameStatistic = new Fragment_AlertDialog("last","last");
                lastGameStatistic.setDeletedWords(saveGameResults.getDeletedWords());
                lastGameStatistic.setWordStatistics(saveGameResults.getWordStatistics());

                lastGameStatistic.show(manager,"lastGame");
            }
        });


        ImageButton refresh = view.findViewById(R.id.refresh);
        //Перезаполнить список слов
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               playWithWords.saveGame(getContext(), saveGame);
               playWithWords.refillWords();
               updateRandomWordAndFields();
            }
        });

        ImageButton statistic = view.findViewById(R.id.see_statistic);
        //Посмотреть статистику (правильные и неправильные слова)
        statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager manager = getParentFragmentManager();

                ArrayList<WordStatistic> listWords = playWithWords.getPriorityWords();

                //Не оптимизированно
                listWords.sort(Sorter.getWordStatisticSortCorrectComparator());
                fragmentAlertDialog.setWordStatistics(listWords);
                fragmentAlertDialog.setDeletedWords(playWithWords.getCorrectWords());

                fragmentAlertDialog.show(manager, "myDialog");

            }
        });

        Button check = view.findViewById(R.id.check);
        //по нажатию на кнопку, проверить слово
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(playWithWords.getCurrentWord()==null){                   //Если слово существует, то
                    Toast.makeText(getContext(),"No Words",Toast.LENGTH_SHORT).show();
                    return;
                }

                String userInput = translate.getText().toString().trim();  //получить слово от пользователя
                boolean correct = false;
                WordStatistic currentWord = playWithWords.getCurrentWord();

                if(priorityTranslate == LanguageWord.ENGLISH){        //если нужно перевести на русский
                    if(currentWord.getRuWord().equals(userInput)){
                        correct = true;
                    }
                }else {                                            //если нужно перевести на английский
                    if(currentWord.getEnglishWord().equals(userInput)){
                        correct = true;
                    }
                }

                animateBackgroundCheck(correct);                        //анимировать правильный ответ или нет
                if(!playWithWords.upgradeWordsStatistic(correct)) //обновить статистику слов
                    Toast.makeText(getContext(), "Can't update word upgradeWordsStatistic(correct) Play", Toast.LENGTH_SHORT).show();

                updateRandomWordAndFields(); //получить новое случайное слово
                // Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //обновить поля, в которые слова записываютя и получить случайное слово
        updateRandomWordAndFields();
    }

    //получить случайное слово и записать его
    private void updateRandomWordAndFields(){
        View view = getView();

        TextView allWords = view.findViewById(R.id.count_words);
        allWords.setText(String.valueOf(playWithWords.
                getSizePriorityWords())); //upgrade counter
        TextView textView = view.findViewById(R.id.description);

        playWithWords.setRandomWord();   //получить случайное слово
        WordStatistic currentWord = playWithWords.getCurrentWord();

        String description = "";
        if(currentWord!=null) {   //Если слово выпало, то
            description = currentWord.getDescription();  //записываем его описание
            textView.setText(!description.equals("")?"description: " + description:"-");
        }else {
            textView.setText("-");
        }

        writeRandomWord();
    }
    //анимировать (правильно или неправильно ответили) при нажатии на кнопку проверить
    private void animateBackgroundCheck(boolean isCorrect){
        int colorFrom = 0;
        int colorTo = 0;

        colorTo = getResources().getColor(R.color.divider);

        if(isCorrect){
            colorFrom  = getResources().getColor(R.color.light_green200);
            playAnimation.changeBackgroundColor(colorFrom,colorTo);

        }else {
            colorFrom  = getResources().getColor(R.color.deep_orange500);
            playAnimation.changeBackgroundColor(colorFrom,colorTo);
        }
    }
    //записать случайно выпавшее слово в поля и задать длы польвоателя на каком языке ответ писать
    private void writeRandomWord(){
        View view = getView();

        TextView word = view.findViewById(R.id.word);
        WordStatistic currentWord = playWithWords.getCurrentWord();

        translate.setText("");
        word.setText("-");
        if(currentWord!=null){
            int enRu = playWithWords.getRandomInt(2);
            translate.setText("");

            switch (enRu){
                case 0:   //en word need translate
                    priorityTranslate = LanguageWord.ENGLISH;
                    word.setText(currentWord.getEnglishWord());
                    break;
                case 1:   //ru word need translate
                    priorityTranslate = LanguageWord.RUSSIAN;
                    word.setText(currentWord.getRuWord());
                    break;
            }
        }
   // Toast.makeText(getContext(), word.toString(), Toast.LENGTH_SHORT).show();
}
    //Создать меню опций и обработать события
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_play,menu);


        MenuItem all = menu.findItem(R.id.sort_all);
        MenuItem priority = menu.findItem(R.id.sort_priority);

        //В соответствии с выбранным пунктом заполнить массив со словами для игры
        MenuItem.OnMenuItemClickListener on = (new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.sort_all:
                        playWithWords.setWordSPriority(PlayWithWords.WordSPriority.ALL);
                        break;
                    case R.id.sort_priority:
                       playWithWords.setWordSPriority(PlayWithWords.WordSPriority.PRIORITY);
                        break;
                }

                playWithWords.refillWords();


                updateRandomWordAndFields();
                item.setChecked(true);
                return false;
            }
        });

        all.setOnMenuItemClickListener(on);
        priority.setOnMenuItemClickListener(on);
    }

    //Уничтожить View
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        playWithWords.saveGame(getContext(), saveGame);
    }
}
