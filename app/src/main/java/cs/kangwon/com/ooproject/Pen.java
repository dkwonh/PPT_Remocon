package cs.kangwon.com.ooproject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015-11-24.
 */
public class Pen extends Activity {
    private float x = 0;
    private float y = 0;
    private float px = 0;
    private float py = 0;
    private float tx = 0;
    private float ty = 0;
    private String msg = null;
    protected String k;
    byte [] send;
    private static int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private static final int SHOW_PRESS =1;
    private static final int LONG_PRESS=2;
    private static final int TAP = 3;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pen);

        ImageView mouseArea = (ImageButton) findViewById(R.id.penButton);
        Button b1 = (Button)findViewById(R.id.button);
        b1.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent ev){
                switch (ev.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        String pen = "pen";
                        byte [] p = pen.getBytes();
                        MousePointer.mc.write(p);
                        System.out.println(pen);
                        break;
                    case MotionEvent.ACTION_UP:
                        String up = "drag";
                        byte [] s = up.getBytes();
                        MousePointer.mc.write(s);
                        System.out.println(up);
                        break;
                }
                return true;
            }
        });
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
                                MousePointer.mc.write(send);
                                System.out.println(msg);
                            }
                        }.start();
                        //mc.write(send);
                        tx = px;
                        ty = py;
                        break;
                    }
                }
                return true;
            }
        });
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        switch (keycode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                k = "previousBtn";
                byte[] s = k.getBytes();
                MousePointer.mc.write(s);
                break;

            case KeyEvent.KEYCODE_VOLUME_UP:
                k = "nextBtn";
                byte[] p = k.getBytes();
                MousePointer.mc.write(p);
                break;
            case KeyEvent.KEYCODE_BACK:
                String m = "false";
                byte[] e = m.getBytes();
                MousePointer.mc.write(e);
                super.onBackPressed();
        }
        return true;
    }
}
