package com.ekaen.sjl95.ble_ex;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BluetoothCommunicationActivity extends AppCompatActivity {

    // xml 객체 선언
    private LinearLayout linearLayout_setcolor;
    private TextView textView_connection_status;
    private TextView textView_connection_explaination;
    private ListView listView_alarm_log;
    private Button button_pairing;
    private TextView textView_alarm_log;

    // 일반 변수 객체 선언
    private List<String> list;
    private ArrayAdapter arrayAdapter;
    private int log_num = 1;

    // 쓰레드 사용 객체 선언
    private int readBufferPosition;
    private byte[] readBuffer;
    private Thread thread;

    // WakeLock 사용 객체
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_communacation);

        // xml 객체 id 연결
        linearLayout_setcolor = (LinearLayout)findViewById(R.id.linearlayout_setcolor);
        textView_connection_status = (TextView)findViewById(R.id.textview_connection_status);
        textView_connection_explaination = (TextView)findViewById(R.id.textview_connection_explaination);
        listView_alarm_log = (ListView)findViewById(R.id.listview_alarm_log);
        button_pairing = (Button)findViewById(R.id.button_pairing);
        textView_alarm_log = (TextView)findViewById(R.id.textview_alarm_log);

        // 리스트 뷰 어댑터 생성
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(BluetoothCommunicationActivity.this, android.R.layout.simple_list_item_1, list);
        // 항상 최하단 아이템으로 유지
        listView_alarm_log.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView_alarm_log.setAdapter(arrayAdapter);

        // 페어링 하기 버튼 클릭 이벤트
        button_pairing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(BluetoothCommunicationActivity.this, ConnectBluetoothActivity.class));
            }
        });

        // WakeLock 객체 생성 및 설정
//        powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
//        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
//                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");

        // 동적 버튼 객체 생성
        final Button newbtn = new Button(BluetoothCommunicationActivity.this);

        // UI 변경은 핸들러에서 처리
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                // 연결 되지 않았을 때
                linearLayout_setcolor.setBackgroundColor(Color.rgb(179,179,179));
                textView_connection_status.setTextColor(Color.RED);
                textView_connection_status.setText("블루투스 연결 상태 : 불량");
                textView_connection_explaination.setText("블루투스 디바이스와 연결 상태가 좋지 않습니다.\n" +
                        "계속해서 연결 상태가 좋지 않을 경우 아래 버튼을 눌러 다시 페어링을 시도하세요.");

                // 버튼 보이기
                button_pairing.setVisibility(View.VISIBLE);
                listView_alarm_log.setVisibility(View.INVISIBLE);
                textView_alarm_log.setVisibility(View.INVISIBLE);
            }
        };

        // 수신 버퍼 저장 위치
        readBufferPosition = 0;
        readBuffer = new byte[10];

        // 문자 수신 쓰레드
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 인터럽트 호출 전까지 반복
                while(!Thread.currentThread().isInterrupted()) {
                    // 수신 데이터 확인 변수
                    int byteAvailabe = 0;

                    // 문자열 개수를 받아옴
                    try {
                        byteAvailabe = ConnectBluetoothActivity.inputStream.available();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("Thread", "From Bluetooth Data : " + byteAvailabe);

                    // 데이터가 수신된 경우
                    if(byteAvailabe > 0) {
                        // 데이터 크기만큼 바이트 배열 생성
                        byte[] packetByte = new byte[byteAvailabe];
                        // 바이트 배열 크기만큼 읽어옴
                        try {
                            ConnectBluetoothActivity.inputStream.read(packetByte);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        for(int i = 0; i < byteAvailabe; i++) {
                            final byte readData = packetByte[i];
                            if(readData != '\n') {
                                // 읽어온 바이트 배열을 인코딩 된 배열로 복사
                                byte[] encodedByte = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedByte, 0, encodedByte.length);

                                try {
                                    String data = new String(encodedByte, "US-ASCII");
                                }
                                catch (UnsupportedEncodingException e) {
                                    e.getStackTrace();
                                }

                                readBufferPosition = 0;

                                final PendingIntent pendingIntent = PendingIntent.getActivity(BluetoothCommunicationActivity.this, 0,
                                        new Intent(getApplicationContext(), BluetoothCommunicationActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                                handler.post(new Runnable() {
                                    // 알림 객체 선언
                                    NotificationManager notificationManager;
                                    Notification.Builder builder;

                                    @Override
                                    public void run() {
                                        // 버튼 숨기기
                                        button_pairing.setVisibility(View.INVISIBLE);
                                        listView_alarm_log.setVisibility(View.VISIBLE);
                                        textView_alarm_log.setVisibility(View.VISIBLE);

                                        // 0 입력 받을 때
                                        if(readData == 48) {
                                            linearLayout_setcolor.setBackgroundColor(Color.rgb(185,255,198));
                                            textView_connection_status.setTextColor(Color.BLACK);
                                            textView_connection_status.setText("블루투스 연결 상태 : 정상");
                                            textView_connection_explaination.setText("블루투스 디바이스와 성공적으로 페어링했습니다.");
                                        }
                                        // 1 입력 받을 때
                                        else {
                                            // 현재 시간을 받아옴
                                            long now = System.currentTimeMillis();
                                            Date date = new Date(now);
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 - HH시 mm분 ss초");
                                            String nowDate = simpleDateFormat.format(date);

                                            linearLayout_setcolor.setBackgroundColor(Color.rgb(243,197,197));
                                            textView_connection_explaination.setText("센서가 감지되었습니다.");

                                            // 알림 객체 설정
                                            builder = new Notification.Builder(BluetoothCommunicationActivity.this)
                                                    .setSmallIcon(R.drawable.ic_launcher_background) // 아이콘 설정
                                                    .setContentTitle(nowDate) // 제목 설정
                                                    .setContentText("센서가 감지되었습니다.") // 내용 설정
                                                    .setAutoCancel(true)
                                                    .setTicker("센서가 감지되었습니다.") // 한줄 내용 설명
                                                    .setContentIntent(pendingIntent);
                                            notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                            // 젤리빈 버전 이상 알림
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                notificationManager.notify(0, builder.build());
                                            }

                                            // 로그 리스트에 추가
                                            list.add(log_num + ". " + nowDate);
                                            arrayAdapter.notifyDataSetChanged();
                                            log_num++;

                                            // 진동 객체 설정
//                                            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
//                                            vibrator.vibrate(1000);

                                            // 알림 소리 설정
                                            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                                            ringtone.play();

                                            // WakeLock 깨우기 및 해제
//                                            wakeLock.acquire();
//                                            wakeLock.release();
                                        }
                                    }
                                });
                            }
                        }
                    }
                    // 데이터가 수신되지 않은 경우
                    else {
                        // 메세지 핸들러 호출
                        Message msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    }

                    try {
                        // 2초 간격으로 반복
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 쓰레드 시작
        thread.start();
    }
}