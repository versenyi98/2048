package peter.versenyi.a2048;

import android.widget.TextView;

public class Field {

    int value;
    boolean fresh;
    TextView t;

    Field(){
        value = 0;
        fresh = false;
    }
}
