package cs.kangwon.com.ooproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Administrator on 2015-11-21.
 */
public class MousePointer extends Activity {
    protected static Connect mc;
    private float x = 0;
    private float y = 0;
    private float px = 0;
    private float py = 0;
    private float tx = 0;
    private float ty = 0;
    private String msg = null;
    protected String k;
    byte[] send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pointer);

        ImageView mouseArea = (ImageView) findViewById(R.id.mouseArea);
        mouseArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        tx = event.getX();
                        ty = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        px = event.getX();
                        py = event.getY();
                        x = px-tx;
                        y = py-ty;
                        msg = x + "," + y;
                        send = msg.getBytes();
                        new Thread(){
                            public void run() {
                                mc.write(send);
                                System.out.println(msg);
                            }
                        }.start();
                        //mc.write(send);
                        tx = px;
                        ty = py;
                        break;
                    }
                    case MotionEvent.ACTION_OUTSIDE: {
                    }
                }
                return true;
            }
        });
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        switch (keycode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                k = "previousBtn";
                byte[] s = k.getBytes();
                mc.write(s);
                break;

            case KeyEvent.KEYCODE_VOLUME_UP:
                k = "nextBtn";
                byte[] p = k.getBytes();
                mc.write(p);
                break;
            case KeyEvent.KEYCODE_BACK:
                String m = "false";
                byte[] e = m.getBytes();
                mc.write(e);
                super.onBackPressed();
        }
        return true;
    }
}
