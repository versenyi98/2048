package peter.versenyi.a2048;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private int POINTS;

    private ImageView restart;
    private Field[][] fields = new Field[4][4];
    private TextView points;

    GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        points = findViewById(R.id.Points);

        restart = findViewById(R.id.Restart);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        detector = new GestureDetector(this, this);

        start();
    }

    public void start(){

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                String textviewID = "tw" + i + j;
                int resID = getResources().getIdentifier(textviewID, "id", getPackageName());

                fields[i][j] = new Field();
                fields[i][j].t = findViewById(resID);
                setColor(fields[i][j]);
            }
        }
        POINTS = 0;
        points.setText("Points: " + POINTS);

        newField();
        newField();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    public void newField(){

        if(!checkFilled()) {
            Random random = new Random();
            int col, row;

            do {
                col = random.nextInt(4);
                row = random.nextInt(4);
            } while (fields[col][row].value != 0);

            fields[col][row].value = 1 + ((random.nextDouble() > 0.8) ? 1 : 0);
            setColor(fields[col][row]);
        }
        if (checkFilled() && !checkPossibilities()){
            Toast.makeText(this, "Game over!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkFilled(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(fields[i][j].value == 0){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkPossibilities(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                boolean n, e, s, w;

                n = (i - 1 >= 0) && fields[i][j].value == fields[i - 1][j].value;
                e = (j + 1 <= 3) && fields[i][j].value == fields[i][j + 1].value;
                s = (i + 1 <= 3) && fields[i][j].value == fields[i + 1][j].value;
                w = (j - 1 >= 0) && fields[i][j].value == fields[i][j - 1].value;

                if(n || e || s || w){
                    return true;
                }
            }
        }
        return false;
    }

    public void setColor(Field field){

        if(field.value > 0){
            field.t.setText("" + (int)Math.pow(2, field.value));
        } else{
            field.t.setText(null);
        }

        if(field.value > 12){
            field.t.setBackgroundResource(getResources().getIdentifier("b10", "drawable", getPackageName()));
        } else{
            field.t.setBackgroundResource(getResources().getIdentifier("b" + field.value,"drawable", getPackageName()));
        }
    }

    @Override
    public boolean onDown(MotionEvent e){return false;}
    @Override
    public void onShowPress(MotionEvent e){}
    @Override
    public boolean onSingleTapUp(MotionEvent e){return false;}
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){return false;}
    @Override
    public void onLongPress(MotionEvent e){}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        float diffX = e1.getX() - e2.getX();
        float diffY = e1.getY() - e2.getY();

        if(Math.abs(diffX) > Math.abs(diffY)){
            if(Math.abs(diffX) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (diffX > 0) {
                    onLeftSwipe();
                } else {
                    onRightSwipe();
                }
            }
        } else {
            if(Math.abs(diffY) > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                if (diffY > 0) {
                    onUpSwipe();
                } else {
                    onDownSwipe();
                }
            }
        }

        return false;
    }

    private void onRightSwipe() {
        swipeAnywhere(false, true);
    }
    private void onLeftSwipe() {
        swipeAnywhere(false, false);
    }
    private void onDownSwipe() {
        swipeAnywhere(true, true);
    }
    private void onUpSwipe() {
        swipeAnywhere(true, false);
    }

    public void swipeAnywhere(boolean vertical, boolean positive){

        boolean movement = false;

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                fields[i][j].fresh = false;
            }
        }

        int n = (positive ? 3 : 0);
        int k_diff = (positive ? 1 : -1);
        int col_old, col_new = 0, row_new = 0, row_old;

        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (vertical) {
                    row_old = Math.abs(n - i);
                    col_old = j;
                    col_new = j;
                } else {
                    row_old = j;
                    row_new = j;
                    col_old = Math.abs(n - i);
                }
                if (fields[row_old][col_old].value != 0) {
                    for (int k = i - 1; k >= 0; k--) {
                        int col_diff, row_diff;
                        if (vertical) {
                            row_new = Math.abs(n - k);
                            row_diff = row_new + k_diff;
                            col_diff = col_new;
                        } else {
                            col_new = Math.abs(n - k);
                            row_diff = row_new;
                            col_diff = col_new + k_diff;
                        }
                        if (fields[row_new][col_new].value == fields[row_old][col_old].value && !fields[row_new][col_new].fresh) {
                            fields[row_new][col_new].value++;

                            POINTS += Math.pow(2, fields[row_new][col_new].value);
                            points.setText("Points: " + POINTS);

                            fields[row_old][col_old].value = 0;
                            fields[row_new][col_new].fresh = true;

                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setColor(fields[row_new][col_new]);
                            setColor(fields[row_old][col_old]);

                            movement = true;

                            break;
                        }else if (fields[row_new][col_new].value == 0 && ((vertical ? row_new == n : col_new == n)
                                || (fields[row_diff][col_diff].value != 0 && fields[row_diff][col_diff].value != fields[row_old][col_old].value)
                                || (fields[row_diff][col_diff].fresh))) {
                            fields[row_new][col_new].value = fields[row_old][col_old].value;
                            fields[row_old][col_old].value = 0;

                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setColor(fields[row_new][col_new]);
                            setColor(fields[row_old][col_old]);

                            movement = true;

                            break;
                        }else if(fields[row_new][col_new].value != 0){
                            break;
                        }
                    }
                }
            }
        }
        if(movement){
            newField();
        }else if (checkFilled() && !checkPossibilities()){
            Toast.makeText(this, "Game over!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("POINTS", POINTS);

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                outState.putInt("fields" + i + j, fields[i][j].value);
            }
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        POINTS = savedInstanceState.getInt("POINTS");
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                fields[i][j].value = savedInstanceState.getInt("fields" + i + j);
                setColor(fields[i][j]);
            }
        }
    }
}