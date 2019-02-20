package cs.kangwon.com.ooproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class Search extends Activity {

    private static final String TAG = "Search";
    private static final boolean D = true;
    private BluetoothAdapter  mbtAdapter;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayAdapter<String> new_ArrayAdapter;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.search);

        setResult(Activity.RESULT_CANCELED);




            Button scanButton = (Button) findViewById(R.id.button_scan);
            scanButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    doDiscovery();
                    v.setVisibility(View.GONE);
                }
            });
            mArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);//페어링된 디바이스
            new_ArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);//새로운 디바이스

            //페어링 된 장치 조회
            ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
            pairedListView.setAdapter(mArrayAdapter);
            pairedListView.setOnItemClickListener(mDeviceClickListener);

            //새로운 장치 조회
            ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
            newDevicesListView.setAdapter(new_ArrayAdapter);
            newDevicesListView.setOnItemClickListener(mDeviceClickListener);

            //장치검색시 방송수신 등록
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            this.registerReceiver(mReceiver, filter);

            //장치검색 종료시 방송 수신 등록
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            this.registerReceiver(mReceiver, filter);

            mbtAdapter = BluetoothAdapter.getDefaultAdapter();

            Set<BluetoothDevice> pairedDevices = mbtAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
                for (BluetoothDevice device : pairedDevices) {
                    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else {
                String noDevices = getResources().getText(R.string.no_device).toString();
                mArrayAdapter.add(noDevices);
            }
        }

    protected void onDestroy() {
        super.onDestroy();

        //검색중지
        if (mbtAdapter != null) {
            mbtAdapter.cancelDiscovery();
        }

        this.unregisterReceiver(mReceiver);
    }

    private void doDiscovery() {
       if(D) Log.d(TAG, "doDiscovery()");

        //검색중 표시
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.device_found);

        // 새로운 장치 검색중 표시
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // 이미 검색중이라면 중지
        if (mbtAdapter.isDiscovering()) {
            mbtAdapter.cancelDiscovery();
        }

        // 검색 시작
        mbtAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // 검색 중지
            mbtAdapter.cancelDiscovery();

            // 장치의 맥주소 추출
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // 맥주소를 넣은 인텐트 생성
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);


            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

           //장치 발견시
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 인텐트에서 BluetoothDevice 객체 얻기
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 페어링 된게 아닐경우
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    new_ArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // 검색이 종료되면 액티비티 타이틀 바꾸기
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (new_ArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    new_ArrayAdapter.add(noDevices);
                }
            }
        }
    };
}