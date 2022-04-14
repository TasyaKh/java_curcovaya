package com.example.bd.Logic;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveGameResults implements Serializable {
    private static  final long serialVersionUID = 1L;

    private ArrayList<WordStatistic> wordStatistics;    //слова со статистикой
    private ArrayList<WordStatistic> deletedWords;      //слова, с которыми закончили игру

    SaveGameResults(){
        wordStatistics = new ArrayList<>();
        deletedWords = new ArrayList<>();
    }

    public void setDeletedWords(ArrayList<WordStatistic> deletedWords) {
        this.deletedWords = deletedWords;
    }

    public void setWordStatistics(ArrayList<WordStatistic> wordStatistics) {
        this.wordStatistics = wordStatistics;
    }

    public ArrayList<WordStatistic> getDeletedWords() {
        return deletedWords;
    }

    public ArrayList<WordStatistic> getWordStatistics() {
        return wordStatistics;
    }
}
