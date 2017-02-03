package com.example.jovan.guessthewonder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    ArrayList<String> wonderURLs = new ArrayList<String>();
    ArrayList<String> gameNames = new ArrayList<String>();
    int chosenWonder = 0;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];


    public class WonderName extends AsyncTask < String,Void,String >{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    total.append(line).append('\n');
                }
                result = total.toString();
                return result;




            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    public class ImageDownload extends AsyncTask < String, Void, Bitmap > {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);
        button0 = (Button)findViewById(R.id.button0);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);

        WonderName wonderName = new WonderName();
        String result = null;

        try {
            result = wonderName.execute("http://www.listchallenges.com/100-wonders-of-the-world").get();
            String[] splitResult1 = result.split("<div class=\"col-list-items\">\n");

            String[] splitResult2 = splitResult1[1].split("<div id=\"MainContent_MainContent_panelPageText\" class=\"text-center h3 mm\">");
            String[] splitResult3 = splitResult1[1].split("data-item-id=\"1963539\">");


            Pattern p = Pattern.compile("data-src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult2[0]);



            while (m.find()) {

                wonderURLs.add("http://www.listchallenges.com" + m.group(1));

            }

            p = Pattern.compile("<div class=\"item-name\">(.*?)</div>");
            m = p.matcher(splitResult3[1]);

            while (m.find()) {

                gameNames.add(m.group(1));

            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), gameNames.get(4), Toast.LENGTH_LONG).show();
        createQuestion();


    }

    public void guessIt(View view){


        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(getApplicationContext(), "Wrong! It was " + gameNames.get(chosenWonder), Toast.LENGTH_LONG).show();

        }

        createQuestion();

    }
    public void createQuestion() {

        Random random = new Random();
        chosenWonder = random.nextInt(wonderURLs.size());

        

        ImageDownload imageTask = new ImageDownload();

        Bitmap celebImage;

        try {

            celebImage = imageTask.execute(wonderURLs.get(chosenWonder)).get();

            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);
            //answers.clear();

            int incorrectAnswerLocation;

            for (int i=0; i<4; i++) {

                if (i == locationOfCorrectAnswer) {

                    answers[i] = gameNames.get(chosenWonder);

                } else {

                    incorrectAnswerLocation = random.nextInt(wonderURLs.size());

                    while (incorrectAnswerLocation == chosenWonder) {

                        incorrectAnswerLocation = random.nextInt(wonderURLs.size());

                    }

                    answers[i] = gameNames.get(incorrectAnswerLocation);


                }


            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
