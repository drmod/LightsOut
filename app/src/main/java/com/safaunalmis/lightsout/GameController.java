package com.safaunalmis.lightsout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by ihsansafa on 4/17/17.
 */

public class GameController extends AppCompatActivity {

    private static final String TAG = GameController.class.getSimpleName();

    private static final int SIZE = 5;
    private GameModel mGameModel;
    private TextView mTextViewScore;
    private Toast mToastExit;
    private int mMovesCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        initModel();
        initView();
    }

    private void initModel() {
        if (BuildConfig.DEBUG) Log.d(TAG, "initModel: ");
        mGameModel = GameModel.getInstance();
        mGameModel.init(SIZE);
    }

    private void initView() {
        if (BuildConfig.DEBUG) Log.d(TAG, "initView: ");
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMovesCount = 0;
        mTextViewScore = (TextView) findViewById(R.id.textViewScore);
        mTextViewScore.setText(getString(R.string.moves, mMovesCount));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRestartDialog();
            }
        });
    }

    public void onClickButton(View view) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onClickButton: ");
        // Update move count of view
        mMovesCount++;
        mTextViewScore.setText(getString(R.string.moves, mMovesCount));

        // Get neighbours from grid model
        String tagOfClickedButtonStr = view.getTag().toString();
        int tagOfClickedButton = Integer.valueOf(tagOfClickedButtonStr);
        ArrayList<Integer> tagsOfNeighbours = mGameModel.getTagOfNeighbours(tagOfClickedButton);

        // Update state of clicked button on model
        boolean newState = updateStateOfModel(tagOfClickedButton);
        // Update view of neighbour button
        updateStateOfView(view, newState, tagOfClickedButton);

        for (int tagOfNeighbour : tagsOfNeighbours) {
            // Update state of neighbour button on model
            newState = updateStateOfModel(tagOfNeighbour);

            // Update view of neighbour button
            updateStateOfView(view, newState, tagOfNeighbour);
        }

        // Check is game finished
        if (mGameModel.isGameFinished()) {
            Toast.makeText(getApplicationContext(), R.string.win, Toast.LENGTH_LONG).show();
            showFinishDialog();
        }
    }

    private boolean updateStateOfModel(int tag) {
        if (BuildConfig.DEBUG) Log.d(TAG, "updateStateOfModel: ");
        boolean newState = !mGameModel.getStateOfButton(tag);
        mGameModel.setStateOfButton(tag, newState);
        return newState;
    }

    private void updateStateOfView(View view, boolean newState, int neighbour) {
        if (BuildConfig.DEBUG) Log.d(TAG, "updateStateOfView: ");
        String neighbourTag = String.valueOf(neighbour);
        ToggleButton neighbourButton = (ToggleButton) view.getRootView().findViewWithTag(neighbourTag);
        neighbourButton.setChecked(newState);
    }

    private void restartGame() {
        if (BuildConfig.DEBUG) Log.d(TAG, "restartGame: ");
        initModel();
        initView();
    }

    private void showRestartDialog() {
        if (BuildConfig.DEBUG) Log.d(TAG, "showRestartDialog: ");
        AlertDialog.Builder restartDialogBuilder = new AlertDialog.Builder(GameController.this, R.style.Theme_AppCompat_Dialog_Alert);
        restartDialogBuilder.setIcon(android.R.drawable.ic_menu_rotate);
        restartDialogBuilder.setTitle(R.string.warning_restart_title);
        restartDialogBuilder.setMessage(R.string.warning_restart_description);
        restartDialogBuilder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartGame();
            }
        });
        restartDialogBuilder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do not anything!
            }
        });
        restartDialogBuilder.show();
    }

    private void showFinishDialog() {
        if (BuildConfig.DEBUG) Log.d(TAG, "showFinishDialog: ");
        AlertDialog.Builder startNewGameDialogBuilder = new AlertDialog.Builder(GameController.this, R.style.Theme_AppCompat_Dialog_Alert);
        startNewGameDialogBuilder.setIcon(android.R.drawable.btn_star_big_on);
        startNewGameDialogBuilder.setTitle(R.string.warning_start_new_game_title);
        startNewGameDialogBuilder.setMessage(R.string.warning_start_new_game_description);
        startNewGameDialogBuilder.setCancelable(false);
        startNewGameDialogBuilder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartGame();
            }
        });
        startNewGameDialogBuilder.setNegativeButton(R.string.button_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAndRemoveTask();
            }
        });
        startNewGameDialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onOptionsItemSelected: ");
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent intent = new Intent(GameController.this, AboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onBackPressed: ");
        if (mToastExit != null && mToastExit.getView().isShown()) {
            finish();
        } else {
            mToastExit = Toast.makeText(getApplicationContext(), R.string.warning_exit, Toast.LENGTH_SHORT);
            mToastExit.show();
        }
    }

}
