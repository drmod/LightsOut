package com.safaunalmis.lightsout;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ihsansafa on 4/18/17.
 */

public class GameModel {

    private static final String TAG = GameController.class.getSimpleName();

    private static GameModel mGameModel;
    private ArrayList<Button> mButtons;
    private int mSize;
    private static final int DECIMAL_BASE = 10;

    private GameModel() {}

    public static GameModel getInstance() {
        if (BuildConfig.DEBUG) Log.d(TAG, "getInstance: ");
        if (mGameModel == null) {
            mGameModel = new GameModel();
        }

        return mGameModel;
    }

    public void init(int size) {
        if (BuildConfig.DEBUG) Log.d(TAG, "init: ");
        mSize = size;
        mButtons = new ArrayList<>();

        for (int buttonIndex = 0; buttonIndex < (size * size); buttonIndex++) {
            // Initial button states are true
            Button button = new Button(buttonIndex, true);
            mButtons.add(button);
        }

    }

    public ArrayList<Integer> getTagOfNeighbours(int clickedButtonTag) {
        if (BuildConfig.DEBUG) Log.d(TAG, "getTagOfNeighbours: ");
        int clickedButtonIndex = convertTagToIndex(clickedButtonTag);

        return mButtons.get(clickedButtonIndex).mNeighbours;
    }

    public boolean[] getAllButtonStates() {
        if (BuildConfig.DEBUG) Log.d(TAG, "getAllButtonStates: ");
        int size = mButtons.size();
        boolean[] states = new boolean[size];

        for (int index = 0; index < size; index++) {
            states[index] = mButtons.get(index).mState;
        }

        return states;
    }

    public void setStateOfButton(int tag, boolean newState) {
        if (BuildConfig.DEBUG) Log.d(TAG, "setStateOfButton: ");
        int index = convertTagToIndex(tag);
        mButtons.get(index).mState = newState;
    }

    public boolean getStateOfButton(int tag) {
        if (BuildConfig.DEBUG) Log.d(TAG, "getStateOfButton: ");
        int index = convertTagToIndex(tag);

        return mButtons.get(index).mState;
    }


    public int convertTagToIndex(int tag) {
        if (BuildConfig.DEBUG) Log.d(TAG, "convertTagToIndex: ");
        int row = tag / DECIMAL_BASE;
        int column = tag % DECIMAL_BASE;

        return mSize * (row - 1) + (column - 1);
    }

    public int convertIndexToTag(int index) {
        if (BuildConfig.DEBUG) Log.d(TAG, "convertIndexToTag: ");
        return DECIMAL_BASE * ((index / mSize) + 1 ) + ((index % mSize) + 1);
    }

    public boolean isGameFinished() {
        if (BuildConfig.DEBUG) Log.d(TAG, "isGameFinished: ");
        boolean isFinished = true;

        for (Button button : mButtons) {
            // To be finished no button has left which has state of false.
            if (button.mState) {
                isFinished = false;
                break;
            }
        }

        return isFinished;
    }


    // GameModel has button objects to keep fields
    private class Button {
        private int mTag;
        private boolean mState;
        private ArrayList<Integer> mNeighbours;

        private Button(int index, boolean state) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Button: ");
            mTag = convertIndexToTag(index);
            mState = state;
            mNeighbours = new ArrayList<>();

            // Find neighbours and add mNeighbours list
            int buttonRow = mTag / 10;
            int buttonColumn = mTag % 10;

            // add up neighbour to list if exist
            if (buttonRow -1 > 0) {
                mNeighbours.add(DECIMAL_BASE * (buttonRow - 1) + buttonColumn);
            }

            // add down neighbour to list if exist
            if (buttonRow + 1 < (mSize + 1)) {
                mNeighbours.add(DECIMAL_BASE * (buttonRow + 1) + buttonColumn);
            }

            // add left neighbour to list if exist
            if (buttonColumn - 1 > 0) {
                mNeighbours.add(DECIMAL_BASE * (buttonRow) + buttonColumn - 1);
            }

            // add right neighbour to list if exist
            if (buttonColumn + 1 < (mSize + 1)) {
                mNeighbours.add(DECIMAL_BASE * (buttonRow) + buttonColumn + 1);
            }
        }
    }

}
