package cs.kangwon.com.ooproject;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2015-11-08.
 */
public class Next extends Activity {

    private static final String TAG = "Select";
    private static final boolean D = true;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private BluetoothAdapter bluetooth;
    private static final int REQUEST_ENABLE_BT = 1;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private String Device_name = null;
    public Connect mConnect = null;
    String msg;
    String status = "STOP";
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int TIMERSTART = 10;
    private static final int TIMEREND = 20;
    int time = 0;
    int min = 0;
    int sec = 0;
    int min2 = 0;
    int sec2 = 0;
    TextView timeText;
    Button b5;
    Button reSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button b1 = (Button) findViewById(R.id.Button01);
        Button b2 = (Button) findViewById(R.id.Button02);
        Button b3 = (Button) findViewById(R.id.Button03);
        Button b4 = (Button) findViewById(R.id.Button04);
        b5 = (Button) findViewById(R.id.stop);
        reSet = (Button)findViewById(R.id.Reset);
        ImageView i1 = (ImageView) findViewById(R.id.imageView1);
        timeText = (TextView) findViewById(R.id.textView1);

        bluetooth = BluetoothAdapter.getDefaultAdapter();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = "previousBtn";
                sendMessage(msg);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = "nextBtn";
                sendMessage(msg);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Next.this, Pen.class);
                sendMessage("true");
                startActivity(intent);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Next.this, MousePointer.class);
                sendMessage("true");
                startActivity(intent);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      if(status.equals("STOP"))
                                      {
                                          b5.setText("STOP");
                                          b5.setBackgroundResource(R.drawable.main_btn);
                                      }
                                      else
                                      {
                                          b5.setText("START");
                                          b5.setBackgroundResource(R.drawable.red);

                                      }
                                      new Thread() {
                                          public void run() {
                                                  if (status.equals("STOP")) {
                                                      status = "START";
                                                      while(true){
                                                          if(status.equals("START")){
                                                      try {
                                                          sleep(1000);
                                                          time++;
                                                          min = (time / 60) % 10;
                                                          sec = (time % 60) % 10;
                                                          min2 = (time / 60) / 10;
                                                          sec2 = (time % 60) / 10;
                                                          mHandler.sendEmptyMessage(TIMERSTART);
                                                      } catch (Exception e) {
                                                      }
                                                  }
                                                      else
                                                      break;}

                                                  }
                                                  else {
                                                      status = "STOP";
                                                      mHandler.sendEmptyMessage(TIMEREND);
                                                  }
                                              }
                                          }.start();
                                  }
                              }
        );

        reSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time =0;
                min =0;
                min2 = 0;
                sec = 0;
                sec2 = 0;
                timeText.setText(" 00:00");
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        if (!bluetooth.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mConnect == null) {
            setConnect();
        }
    }

    private void setConnect() {
        Log.d(TAG, "setupConnect()");
        mConnect = new Connect(this, mHandler);
        MousePointer.mc = mConnect;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Discoverable: {
                if (bluetooth.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                    startActivity(discoverableIntent);
                }
                break;
            }
            case R.id.Search: {
                Intent search = new Intent(Next.this, Search.class);
                startActivityForResult(search, REQUEST_CONNECT_DEVICE);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setStatus(int resId) {
        Activity activity = this;
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        Activity activity = this;
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    protected void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mConnect.getState() != Connect.STATE_CONNECTED) {
            Toast.makeText(this, "연결안됨", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothfeatureService to write
            byte[] send = message.getBytes();
            mConnect.write(send);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case Connect.STATE_CONNECTED:
                            setStatus("connected to device");
                            break;
                        case Connect.STATE_CONNECTING:
                            setStatus("connecting...");
                            break;
                        case Connect.STATE_LISTEN:
                        case Connect.STATE_NONE:
                            setStatus("not connected");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    Device_name = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + Device_name, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case TIMERSTART:
                    timeText.setText(" "+min2 + min + ":" + sec2 + sec);
                    break;
                case TIMEREND:
                    Toast.makeText(getApplicationContext(),"발표시간 : "+min2+min+":"+sec2+sec,Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setConnect();
                } else {
                    Toast.makeText(this, R.string.not_enable_bt, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(Search.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = bluetooth.getRemoteDevice(address);
                    mConnect.connect(device);

                    /*Intent game = new Intent(Select.this,Game.class);
                    startActivity(game);*/

                }
        }
    }

}
