package com.peacocktech.imagepuzzle.pintu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.peacocktech.imagepuzzle.R;
import com.peacocktech.imagepuzzle.pintu.view.GamePintuLayout;

public class GameActivity extends Activity {

    private GamePintuLayout mGamePintuLayout;
    private TextView mLevel;
    private TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ����û�б���
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mTime = (TextView) findViewById(R.id.id_time);
        mLevel = (TextView) findViewById(R.id.id_level);

        mGamePintuLayout = (GamePintuLayout) findViewById(R.id.id_game_pintu);
        mGamePintuLayout.setTimeEabled(true);
        mGamePintuLayout.setOnGamePintuListener(new GamePintuLayout.GamePintuListener() {

            @Override
            public void timeChanged(int currentTime) {
                mTime.setText("" + currentTime);
            }

            @Override
            public void nextLevel(final int nextLevel) {
                switch (nextLevel) {
                    case 2:
                        Toast.makeText(GameActivity.this, "Complete Level", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(GameActivity.this, "����������~", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(GameActivity.this, "����ôô��~", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(GameActivity.this, "����������~", Toast.LENGTH_SHORT).show();
                        break;
                }
                new AlertDialog.Builder(GameActivity.this)
                        .setCancelable(false)

                        .setTitle("Jigsaw puzzle").setMessage("Are You Sure Next Level")
                        .setIcon(R.drawable.icon)
                        .setPositiveButton("Go to  " + nextLevel, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGamePintuLayout.nextLevel();
                                mLevel.setText("" + nextLevel);
                            }
                        }).setNegativeButton("Exit", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        System.exit(0);
                    }
                }).show();
            }

            @Override
            public void gameOver() {
                new AlertDialog.Builder(GameActivity.this)
                        .setCancelable(false)
                        .setTitle("Jigsaw puzzle").setMessage("Are You Sure Next Level")
                        .setIcon(R.drawable.icon)
                        .setPositiveButton("Try again", new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGamePintuLayout.reStart();
                            }
                        }).setNegativeButton("Exit", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        System.exit(0);
                    }
                }).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGamePintuLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGamePintuLayout.reSume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mGamePintuLayout.pause();

    }
}
