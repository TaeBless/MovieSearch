package kr.ac.yc.moviesearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<MovieInfo> movieInfoArrayList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgMovie;
        TextView txttitle;
        RatingBar ratingBar;
        TextView txtYear;
        TextView txtDirector;
        TextView txtActor;

        public MyViewHolder(View view) {
            super(view);
            imgMovie = view.findViewById(R.id.img_movie);
            txttitle = view.findViewById(R.id.txt_title);
            ratingBar = view.findViewById(R.id.rat_bar);
            txtYear = view.findViewById(R.id.txt_year);
            txtDirector = view.findViewById(R.id.txt_director);
            txtActor = view.findViewById(R.id.txt_actor);
        }
    }

    public MyAdapter(ArrayList<MovieInfo> movieInfoArrayList, Context context) {
        this.movieInfoArrayList = movieInfoArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_row, viewGroup,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        MyViewHolder myViewHolder1 = (MyViewHolder) myViewHolder;

        //이미지뷰 스레드 처리
        new DownLoadImageTask(myViewHolder1.imgMovie).execute(movieInfoArrayList.get(i).getImgurl());
        String title = movieInfoArrayList.get(i).getTitle();

        //title 에러 처리
        if(title.contains("<b>")||title.contains("</b>")){
            title = title.replace("<b>","");
            title = title.replace("</b>","");
        }

        myViewHolder1.txttitle.setText(title);
        //레이팅 바 실수표현 처리
        myViewHolder1.ratingBar.setRating(Float.parseFloat(movieInfoArrayList.get(i).getRate()) / 2);
        myViewHolder1.txtYear.setText(movieInfoArrayList.get(i).getYear());
        myViewHolder1.txtDirector.setText(movieInfoArrayList.get(i).getDirector());
        myViewHolder1.txtActor.setText(movieInfoArrayList.get(i).getActor());

        myViewHolder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieInfoArrayList.get(i).getLink()));
                    context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieInfoArrayList.size();
    }

    //이미지 uri이용해서 비트맵형식으로 가져오는 스레드
    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlOfImage = strings[0];
            Bitmap bitmap = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                bitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
