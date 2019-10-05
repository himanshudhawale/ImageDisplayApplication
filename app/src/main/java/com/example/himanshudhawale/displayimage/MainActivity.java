package com.example.himanshudhawale.displayimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ImageView myImageView;
    private Handler handler;
    public static String KEYFORIMAGE="key";
    private Button buttonThread, buttonAsync;
    ProgressBar myProgress;
    ExecutorService imgservice= Executors.newFixedThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myImageView=findViewById(R.id.imageView);

        myProgress=findViewById(R.id.progressBar2);
        myProgress.setVisibility(View.INVISIBLE);


        buttonThread=findViewById(R.id.button);
        buttonThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myProgress.setVisibility(View.VISIBLE);




                handler = new Handler(new Handler.Callback() {
                   @Override
                   public boolean handleMessage(Message msg) {
                       if(msg.getData().containsKey("key")) {
                           Bitmap myimage1 = msg.getData().getParcelable("key");
                           myImageView.setImageBitmap(myimage1);
                       }
                       myProgress.setVisibility(View.INVISIBLE);

                       return false;

                   }
               });

              imgservice.execute(new getImage());


            }
        });



        buttonAsync=findViewById(R.id.button2);
        buttonAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myProgress.setVisibility(View.VISIBLE);

                new GetImageAsync().execute("https://cdn.pixabay.com/photo/2014/12/16/22/25/youth-570881_960_720.jpg");

            }
        });



    }


    public class getImage implements Runnable{

        Bitmap getImageBitmap(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void run() {



            String imageURL = null;
            imageURL = "https://cdn.pixabay.com/photo/2017/12/31/06/16/boats-3051610_960_720.jpg";
            Bitmap myBitmap = getImageBitmap(imageURL);
           // myImageView =(ImageView) findViewById(R.id.imageView);
           // myImageView.setImageBitmap(myBitmap);




            Message message=new Message();
            Bundle bundle=new Bundle();
            bundle.putParcelable(KEYFORIMAGE,myBitmap);
            message.setData(bundle);
            handler.sendMessage(message);


        }
    }


    public class GetImageAsync extends AsyncTask<String, Integer, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            HttpURLConnection connection=null;
            Bitmap bitmap=null;
            URL url=null;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                //Handle the exceptions
            } catch(IOException e) {
                e.printStackTrace();
                //Close open connection
            }
            return bitmap;
        }



        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null)
            {
                myImageView.setImageBitmap(bitmap);
            }
            myProgress.setVisibility(View.INVISIBLE);

        }
    }


}
