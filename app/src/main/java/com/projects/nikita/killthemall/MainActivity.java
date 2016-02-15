package com.projects.nikita.killthemall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class MainActivity extends Activity implements GameView.GameOverListener {
    GameView game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d("cycle", "onCreate()");
        game = new GameView(this);

        setContentView(game);
        game.startGame();
}


    @Override
    protected void onPause() {
        super.onPause();
        game.stopGame();
        game = null;
        Log.d("cycle", "onPause()");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("cycle", "onStop()");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("cycle", "onDestroy()");
    }




    public void Fatality(final int score, final int result){
        game.suspendGame();
        AlertDialog.Builder dialog = new AlertDialog.Builder(
                MainActivity.this);

        dialog.setTitle(result < 0 ?
                ("Ты ДЦП. Людей на экране стало слишком много."):
                ("Да ты монстр!!! Мне аж страшно."));

        dialog.setMessage("Твой счет " + score);
        dialog.setCancelable(false);

        dialog.setPositiveButton("Снова", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                game.repeatGame();
            }
        });

        dialog.setNegativeButton(result < 0 ? "Сложнаааа" : "Не"
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
               System.exit(0);
            }
        });

        dialog.show();
    }

}

