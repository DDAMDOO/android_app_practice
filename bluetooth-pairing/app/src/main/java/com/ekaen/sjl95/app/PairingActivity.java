package com.ekaen.sjl95.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PairingActivity extends AppCompatActivity {
    final String TAG = "SubActivity";
    ArrayAdapter<String> pairingAdapter, scanAdapter;
    ListView listView_pairing, listView_scan;
    ArrayList<String> pairingList, scanList;
    Button bt_cancel, bt_scan;
    BluetoothAdapter myBluetoothAdapter;
    protected static UUID MY_UUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_scan = (Button) findViewById(R.id.bt_scan);
        listView_pairing = (ListView) findViewById(R.id.listview_pairing);
        listView_scan = (ListView) findViewById(R.id.listview_scan);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pairingList = new ArrayList<>();
        //기기스캔 목록
        scanList = new ArrayList<>();
        //list data
        MY_UUID = UUID.randomUUID();
        Log.d(TAG, MY_UUID.toString());

        //이건 이미 페어링되어 있는 기기 목록을 가져 오는것이다. 새 기기를 페어링 하는것은 생략 하자. 복잡하다.
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // 루프를 돌면서 리스트뷰에 출력할 array에 계속 추가한다.
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                pairingList.add(device.getName() + "\n" + device.getAddress());//기기 이름과 맥어드레스를 추가한다.
            }
        }

        pairingAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, pairingList);//두번째 인자는 미리 정의된 레이아웃을 사용하는 것
        listView_pairing.setAdapter(pairingAdapter);

        //액티비티 닫기 버튼
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //액티비티 닫기
                finish();
            }
        });

        //리스트 항목 클릭시
        listView_pairing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                Toast.makeText(getApplicationContext(), "연결기기: " + selectedItem, Toast.LENGTH_SHORT).show();
                // 선택한 기기와 연결을 하자.

            }
        });

        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter.isDiscovering()) {
                    myBluetoothAdapter.cancelDiscovery();
                }
                //scanList.clear();//기존 목록을 크리어함
                myBluetoothAdapter.startDiscovery();
            }
        });

        //브로드캐스트 리시버
        BroadcastReceiver myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //ListView 갱신함
                    //먼저 기존 데이터를 비워주고 시작해야 할듯 중복 추가되는 문제 해결 위해서...
                    scanList.add(device.getName());
                    scanAdapter.notifyDataSetChanged();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(myReceiver, intentFilter);//onDestory()에서 언레지스터하는 것을 추가해 줄것.

        scanAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_pairing, scanList);
        listView_scan.setAdapter(scanAdapter);


        listView_scan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                Toast.makeText(getApplicationContext(), "연결기기: " + selectedItem, Toast.LENGTH_SHORT).show();
                // 선택한 기기를 페어링 목록에 추가한다.
            }
        });

    }
}