package com.example.p1t2_android_app;

import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;


public class Die extends AppCompatActivity {

    // Store the Thread sleep time in an integer variable
    int delayTime = 20;
    // Store the number of Dice roll animations per execution
    int rollAnimations = 40;
    // Store the ids for Dice images in an integer array
    int[] diceImages = new int[]{R.drawable.die1, R.drawable.die2, R.drawable.die3, R.drawable.die4, R.drawable.die5, R.drawable.die6};
    // Define a Random object
    Random random = new Random();
    // Declare View object references
    ImageView die1;
    ImageView die2;
    int total;
    public Die(ImageView die1, ImageView die2){
        this.die1 = die1;
        this.die2 = die2;
    }


    public interface OnDiceRollCompleteListener {
        void onDiceRollComplete(int total);
    }

    private OnDiceRollCompleteListener listener;

    public void setOnDiceRollCompleteListener(OnDiceRollCompleteListener listener) {
        this.listener = listener;
    }

    public void rollDice() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < rollAnimations; i++) {
                    int dice1 = random.nextInt(6) + 1;
                    int dice2 = random.nextInt(6) + 1;
                    total = dice1 + dice2;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update the ImageView on the UI thread
                            die1.setImageResource(diceImages[dice1 - 1]);
                            die2.setImageResource(diceImages[dice2 - 1]);
                        }
                    });
                    try {
                        Thread.sleep(delayTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Notify the listener on the UI thread after rolling is complete
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onDiceRollComplete(total);
                        }
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}