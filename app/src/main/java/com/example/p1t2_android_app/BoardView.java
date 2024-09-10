package com.example.p1t2_android_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.model.Game;
import com.example.model.Player;

/**
 * Class creates game board (custom) view
 * learned from youtube tutorial
 * uses attrs values resource folder
 * Paint and Canvas used to draw board and colour cells
 */
public class BoardView extends View {

    private final int boardColour;
    private final int Colour1;
    private final int Colour2;
    private final int Colour3;

    private boolean winningLine;
    private int cellSize = getWidth() / 6;

    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint(); // New paint object for text

    GameObserver gameObserver;

    //game from model
    public  Game game;

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //getting attributes from attrs file + 2 default values
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BoardView, 0, 0);


        try {
            boardColour = a.getInteger(R.styleable.BoardView_boardColour, 0);
            Colour1 = a.getInteger(R.styleable.BoardView_Colour1, 0);
            Colour2 = a.getInteger(R.styleable.BoardView_Colour2, 0);
            Colour3 = a.getInteger(R.styleable.BoardView_Colour3, 0);

        } finally {
            a.recycle();
        }

        // Initialize text paint properties
        textPaint.setColor(Color.BLACK); // Set text color
        textPaint.setTextAlign(Paint.Align.CENTER); // Center text alignment
        textPaint.setTextSize(40); // Set text size (adjust as needed)
    }

    /**
     * defines dimensions of board
     */
    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);

        //find smallest dimension of user screen size (either width or height ) to make a square.
        int dimension = Math.min(getMeasuredWidth(), getMeasuredHeight());
        cellSize = dimension / 6;
        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onDraw(Canvas canvas) { //canvas = area of view
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        drawGameBoard(canvas);
        putColours(canvas);
        drawCellNumbers(canvas); // Draw cell numbers
    }

    private void drawGameBoard(Canvas canvas) {
        paint.setColor(boardColour);
        paint.setStrokeWidth(8);
        //draws columns
        for (int c = 1; c < 6; c++) {
            canvas.drawLine(cellSize * c, 0, cellSize * c, canvas.getHeight(), paint);
        }

        //draws rows
        canvas.drawLine(0, 0, canvas.getWidth(), 0, paint);

        int r;
        for (r = 1; r < 6; r++) {
            canvas.drawLine(0, cellSize * r, canvas.getWidth(), cellSize * r, paint);
        }

        canvas.drawLine(0, cellSize * r, canvas.getWidth(), cellSize * r, paint);
    }

    private void putColours(Canvas canvas) {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                if (!game.board.isCellEmpty(r, c)) { //if cell isn't empty
                    Player curr = game.getPlayerById(game.board.getToken(r,c)); //get player who took position
                    if(curr.getColour().equals("Red")){
                        putRed(canvas,r,c);
                    }
                    else if(curr.getColour().equals("Blue")){
                        putBlue(canvas,r,c);
                    }
                    else {
                        putGreen(canvas,r,c);
                    }
                }
            }
        }
    }

    private void drawCellNumbers(Canvas canvas) {
        textPaint.setTextSize(90);
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                int cellNumber = game.board.getCellNumber(r, c);
                float x = (c * cellSize) + (cellSize >> 1); // Center x (cellSize >>1 = cellSize/2)
                float y = (r * cellSize) + (cellSize >> 1) - ((textPaint.descent() + textPaint.ascent()) / 2); // Center y
                canvas.drawText(String.valueOf(cellNumber), x, y, textPaint); // Draw cell number
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            int row = (int) Math.ceil(y / cellSize);
            int col = (int) Math.ceil(x / cellSize);

            if (!winningLine) {
                boolean placed = game.handleRoll(game.players.get(game.currentPlayerIndex), gameObserver.diceTotal, row - 1, col - 1);
                invalidate(); // Invalidate redraws the view

                if (gameObserver.winnerCheck()) {
                    winningLine = true;
                    invalidate(); // Redraw to show the winning state
                } else {
                    if(placed){
                        gameObserver.oneTurn();
                        game.setPlayer();
                        gameObserver.onTurnStarted(game.players.get(game.currentPlayerIndex));
                    }

                }
            }
            return true;
        }
        return false;
    }

    private void putRed(Canvas canvas, int row, int col) {
        paint.setColor(Colour1);
        // set the paint style to fill the inside of the rectangle
        paint.setStyle(Paint.Style.FILL);

        float left = (float) (col * cellSize + cellSize * 0.05); // some reduction so that the borders stay black
        float top = (float) (row * cellSize + cellSize * 0.05);
        float right = (float) ((col + 1) * cellSize - cellSize * 0.05);
        float bottom = (float) ((row + 1) * cellSize - cellSize * 0.05);

        // Draw a filled rectangle covering the entire cell area, input must be float
        canvas.drawRect(left, top, right, bottom, paint);
    }

    private void putBlue(Canvas canvas, int row, int col) {
        paint.setColor(Colour2);
        // set the paint style to fill the inside of the rectangle
        paint.setStyle(Paint.Style.FILL);
        float left = (float) (col * cellSize + cellSize * 0.05); // some reduction so that the borders stay black
        float top = (float) (row * cellSize + cellSize * 0.05);
        float right = (float) ((col + 1) * cellSize - cellSize * 0.05);
        float bottom = (float) ((row + 1) * cellSize - cellSize * 0.05);

        // Draw a filled rectangle covering the entire cell area, input must be float
        canvas.drawRect(left, top, right, bottom, paint);
    }

    private void putGreen(Canvas canvas, int row, int col) {
        paint.setColor(Colour3);
        // set the paint style to fill the inside of the rectangle
        paint.setStyle(Paint.Style.FILL);

        float left = (float) (col * cellSize + cellSize * 0.05); // some reduction so that the borders stay black
        float top = (float) (row * cellSize + cellSize * 0.05);
        float right = (float) ((col + 1) * cellSize - cellSize * 0.05);
        float bottom = (float) ((row + 1) * cellSize - cellSize * 0.05);

        // Draw a filled rectangle covering the entire cell area, input must be float
        canvas.drawRect(left, top, right, bottom, paint);
    }

    public void setUpGame(Button playAgain, Button exit, TextView playerDisplay, TextView dieText, ImageView d1, ImageView d2,Context context) {
        gameObserver = new GameObserver(playAgain, exit, playerDisplay, dieText,d1,d2,context);
        gameObserver.game = game;
    }

    public void resetGameBoard() {
        game.resetGame();
        winningLine = false;
    }
}
