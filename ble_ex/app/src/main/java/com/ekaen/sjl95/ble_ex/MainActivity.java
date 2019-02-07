package com.ekaen.sjl95.ble_ex;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 버튼 객체 생성 및 xml id 연결
        Button button_connect_bluetooth = (Button) findViewById(R.id.button_connect_bluetooth);
        Button button_how_to = (Button) findViewById(R.id.button_how_to);
        Button button_inqure = (Button) findViewById(R.id.button_inquire);
        Button button_exit = (Button) findViewById(R.id.button_exit);

        // "블루투스 연결" 버튼 클릭 이벤트
        button_connect_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새 창에서 ConnectBluetoothActivity 실행
                Intent intent = new Intent(MainActivity.this, ConnectBluetoothActivity.class);
                startActivity(intent);
            }
        });

        // "사용 방법" 버튼 클릭 이벤트
        button_how_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새 창에서 HowToActivity 실행
                //Intent intent = new Intent(MainActivity.this, HowToActivity.class);
                //startActivity(intent);
            }
        });

        // "문의하기" 버튼 클릭 이벤트
        button_inqure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새 창에서 InquireActivity 실행
                //Intent intent = new Intent(MainActivity.this, InquireActivity.class);
                //startActivity(intent);
            }
        });

        // "앱 종료" 버튼 클릭 이벤트
        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 종료 확인 다이얼로그(대화창) 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                builder.setMessage("정말로 앱을 종료하시겠습니까?").setTitle("앱 종료")
                        .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                            // 아니오 버튼을 눌렀을 때
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 아무 작동없이 함수를 종료
                                return;
                            }
                        })
                        .setNeutralButton("예", new DialogInterface.OnClickListener() {
                            // 예 버튼을 눌렀을 때
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 앱을 종료
                                finish();
                            }
                        })
                        .setCancelable(false).show();
            }
        });
    }
}