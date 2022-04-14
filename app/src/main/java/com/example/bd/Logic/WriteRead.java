package com.example.bd.Logic;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class WriteRead {

    public static SaveGameResults read(Context context, String saveGame){
        SaveGameResults saveGameResults = null;

        try {
            FileInputStream outputStream = context.openFileInput(saveGame);//openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectInputStream os = new ObjectInputStream(outputStream);
            saveGameResults = (SaveGameResults) os.readObject();
            //EditText editText = getView().findViewById(R.id.last_game);
            //editText.setText(str);
            os.close();
            outputStream.close();
        } catch (IOException | ClassNotFoundException | ClassCastException e ) {
            e.printStackTrace();
        }

        return saveGameResults;
    }

    public static boolean saveGame(Context context, String fileName, Object saveGameResults){
        boolean result = true;

            try {
                FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(outputStream);
                os.writeObject(saveGameResults);
                os.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }

        return result;
    }
}
