package kr.ac.yc.moviesearch;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText edt_movie;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MovieInfo> movieInfoArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_movie = findViewById(R.id.edt_movie);

        mRecyclerView = findViewById(R.id.rv_movie);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(), 1));

        myAdapter = new MyAdapter(movieInfoArrayList, this);
        mRecyclerView.setAdapter(myAdapter);
    }

    public void btnclick(View v) {
        new SearchTask().execute();
    }

    //네이버 검색 api이용해서 영화 검색하는 스레드
    private class SearchTask extends AsyncTask {
        private StringBuilder sb;
        private String info;

        @Override
        protected Object doInBackground(Object[] objects) {
            String clientId = "D350H9AMAYsLrlMUGSfw";
            String clientSecret = "6ogwhjeGSS";
            try {
                String text = URLEncoder.encode(edt_movie.getText().toString(), "utf-8");
                String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text + "&display=100";

                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                info = sb.toString();

                int length = movieinfojsonParser(info);
                if(length==0){
                    //유효하지 않은 검색어, 검색결과 처리
                    publishProgress(edt_movie.getText().toString());
                }
                con.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return info;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            Toast.makeText(getApplicationContext(), String.valueOf(values[0]) + " 검색결과가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public int movieinfojsonParser(final String jsoninfo) {
        String title = null;
        String link = null;
        String imgurl = null;
        String year = null;
        String director = null;
        String actor = null;
        String rate = null;
        JSONArray jarray = null;

        try {
            movieInfoArrayList.clear();
            jarray = new JSONObject(jsoninfo).getJSONArray("items");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jsonObject = jarray.getJSONObject(i);

                title = jsonObject.optString("title");
                link = jsonObject.optString("link");
                imgurl = jsonObject.optString("image");
                year = jsonObject.optString("pubDate");
                director = jsonObject.optString("director");
                actor = jsonObject.optString("actor");
                rate = jsonObject.optString("userRating");

                MovieInfo movieInfo = new MovieInfo(title, link, imgurl, year, director, actor, rate);
                movieInfoArrayList.add(movieInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myAdapter.notifyDataSetChanged();
            }
        });

        return jarray.length();
    }
}
