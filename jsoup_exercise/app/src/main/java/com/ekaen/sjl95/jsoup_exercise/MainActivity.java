package com.ekaen.sjl95.jsoup_exercise;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView textView; //결과를 띄어줄 TextView
    TextView reload; //reload버튼
    Elements contents;
    Document doc = null;
    String Temperature;//결과를 저장할 문자열변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textBox);
        reload = (TextView) findViewById(R.id.reload);

        reload.setOnClickListener(new View.OnClickListener() {//onclicklistener를 연결하여 터치시 실행됨
            @Override
            public void onClick(View v) {
                new AsyncTask() {//AsyncTask객체 생성
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            doc = Jsoup.connect("http://www.weather.go.kr/weather/forecast/timeseries.jsp").get(); //naver페이지를 불러옴
                            contents = doc.select("dd.now_weather1_center.temp1.MB10");//셀렉터로 span태그중 class값이 ah_k인 내용을 가져옴
//                            doc = Jsoup.connect("https://search.naver.com/search.naver?sm=top_hty&fbm=1&ie=utf8&query=%EB%82%A0%EC%94%A8").get(); //naver페이지를 불러옴
//                            contents = doc.select("dd.weather_item._dotWrapper");//셀렉터로 span태그중 class값이 ah_k인 내용을 가져옴
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Temperature = "현재날씨 = " +contents.text()+"\n";

                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        Log.i("TAG",""+ Temperature);
                        textView.setText(Temperature);
                    }
                }.execute();
            }
        });
    }
}