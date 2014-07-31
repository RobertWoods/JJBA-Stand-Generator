package com.robert.standgenerator;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jreddit.submissions.Submission;
import com.github.jreddit.user.User;
import com.robert.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyActivity extends ActionBarActivity implements AsyncResponse {

    private static final Pattern TITLE_TAG =
            Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    String name;
    String newName;
    String power;
    String newPower;
    String password;
    String user;
    String url;
    String redditStand;
    TextView powerText;
    TextView nameText;
    TextView stats;
    EditText field;
    TitleFetcher fetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        field = (EditText) findViewById(R.id.editText1);
        nameText = (TextView) findViewById(R.id.textView1);
        powerText = (TextView) findViewById(R.id.textView2);
        stats = (TextView) findViewById(R.id.textView3);
        setEditText();
        generate();
    }

    public void setEditText() {
        field.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    generate();
                    return true;
                }
                return false;
            }
        });
    }

    public void getRedditFormatStand(){
        String form = "Stand Name: " + name + "\n \n" +"Stand Ability: " + "[" + power + "]" +"(http://powerlisting.wikia.com/wiki/"+power.replace(" ","_") + ")" + "\n\n" + stats.getText().toString().replace("\n","\n\n");
        redditStand = form;
    }

    public void onRedditClick(View view) {
            RedditSignInDialogFragment signIn = new RedditSignInDialogFragment();
            signIn.show(getFragmentManager(), "signin");
    }

    public void onPostClick(View view){
        if(name == null || power == null || stats ==null) {
            Toast.makeText(this, "Generate a Stand first", Toast.LENGTH_SHORT).show();
        } else if(user==null || password == null || user == "" || password == ""){
            Toast.makeText(this,"Sign in", Toast.LENGTH_SHORT).show();
        } else {
            getRedditFormatStand();
            RedditPost post = new RedditPost();
            post.delegate=this;
            post.execute(user,password,redditStand);
        }
    }

    public void generate() {
        fetcher = new TitleFetcher();
        fetcher.delegate = this;
        fetcher.execute(field.getText().toString());
    }

    public void onNameClick(View view) {
        power=newPower;
        name=newName;
        powerText.setText("Stand Ability: " + power);
        nameText.setText("Stand Name: " + name);
        stats.setText(getStats());
        if(dio()){
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageResource(R.drawable.dio_portrait);
            Button button = (Button) findViewById(R.id.postButton);
            button.setText(R.string.wryy);
        }
        generate();
    }

    public boolean dio(){
        if(field.getText().toString().toLowerCase().equals("dio")){
            return true;
        }
        return false;
    }

    public String getStats() {
        String stats = "";
        Random rand = new Random();
        String[] attributes = {"Power - ", "Speed - ", "Range - ", "Durability - ", "Precision - ", "Potential - "};
        for (int i = 0; i < 6; i++) {
            stats += attributes[i];
            int num = rand.nextInt(5);
            switch (num) {
                case 0:
                    stats += "A";
                    break;
                case 1:
                    stats += "B";
                    break;
                case 2:
                    stats += "C";
                    break;
                case 3:
                    stats += "D";
                    break;
                case 4:
                    stats += "E";
                    break;
            }
            stats += "\n";
        }
        return stats;
    }

    @Override
    public void processFinish(String[] output) {
        newPower = output[0].replace("/Gallery","");
        newName = output[1];
    }

    @Override
    public void setUrl(String url) {
        TextView urlText = (TextView) findViewById(R.id.urlText);
        urlText.setClickable(true);
        urlText.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='" + url + "'> Stand Saturday! </a>";
        urlText.setText(Html.fromHtml(text));
    }


    class TitleFetcher extends AsyncTask<String, Void, String[]> {

        public AsyncResponse delegate;


        @Override
        protected String[] doInBackground(String... params) {
            String[] result = new String[2];
            try {
                result[0] = getPower();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                result[1] = getName(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(String[] result) {
            delegate.processFinish(result);
        }

        public String getPower() throws IOException {
            String title;
            URL url = new URL("http://www.powerlisting.wikia.com/wiki/Special:Random");
            URLConnection connection = url.openConnection();

            ContentType contentType = getContentTypeHeader(connection);
            if (!contentType.contentType.equals("text/html"))
                return null;
            else {
                Charset charset = getCharset(contentType);
                if (charset == null) {
                    charset = Charset.defaultCharset();
                }
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
                int n = 0, totalRead = 0;
                char buf[] = new char[1024];
                StringBuilder content = new StringBuilder();

                while (totalRead < 8192 && (n = reader.read(buf, 0, buf.length)) != -1) {
                    content.append(buf, 0, n);
                    totalRead += n;
                }
                reader.close();

                Matcher matcher = TITLE_TAG.matcher(content);
                if (matcher.find()) {
                    title = matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim();
                    title = title.split(" -")[0];
                    return title;
                }
            }
            return null;
        }

        public String getName(String albumName) throws IOException, JSONException {
            String partialUrl = "https://itunes.apple.com/search?term=";
            albumName.replaceAll("[\\s\\<>]+", " ");
            albumName = albumName.replace(' ', '+');
            URL url = new URL(partialUrl + albumName);
            Scanner scan = new Scanner(url.openStream());
            String str = new String();
            while (scan.hasNext()) {
                str += scan.nextLine();
            }
            scan.close();
            JSONObject obj = new JSONObject(str);
            JSONArray arr = obj.getJSONArray("results");
            ArrayList<String> tracks = new ArrayList<String>();
            for (int i = 0; i < arr.length(); i++) {
                tracks.add(arr.getJSONObject(i).getString("trackName"));
            }
            if (tracks.size() == 0)
                return "[Album Not Found]";
            Random rand = new Random();
            String name = tracks.get(rand.nextInt(tracks.size()));
            return name;
        }


    }


    private static ContentType getContentTypeHeader(URLConnection conn) {
        int i = 0;
        boolean moreHeaders = true;
        do {
            String headerName = conn.getHeaderFieldKey(i);
            String headerValue = conn.getHeaderField(i);
            if (headerName != null && headerName.equals("Content-Type"))
                return new ContentType(headerValue);

            i++;
            moreHeaders = headerName != null || headerValue != null;
        }
        while (moreHeaders);

        return null;
    }


    private static Charset getCharset(ContentType contentType) {
        if (contentType != null && contentType.charsetName != null && Charset.isSupported(contentType.charsetName))
            return Charset.forName(contentType.charsetName);
        else
            return null;
    }

    private static final class ContentType {
        private static final Pattern CHARSET_HEADER = Pattern.compile("charset=([-_a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        private String contentType;
        private String charsetName;

        private ContentType(String headerValue) {
            if (headerValue == null)
                throw new IllegalArgumentException("ContentType must be constructed with a not-null headerValue");
            int n = headerValue.indexOf(";");
            if (n != -1) {
                contentType = headerValue.substring(0, n);
                Matcher matcher = CHARSET_HEADER.matcher(headerValue);
                if (matcher.find())
                    charsetName = matcher.group(1);
            } else
                contentType = headerValue;
        }
    }

    class RedditPost extends AsyncTask<String, Void, String>{

        AsyncResponse delegate;

        @Override
        protected String doInBackground(String... strings) {
            String[] arr;
            User user = new User(strings[0],strings[1]);
            Submission thread = new Submission();
            try {
                user.connect();
                thread.setUser(user);
                arr = getStandSaturday();
                thread.setFullName(arr[0]);
                thread.comment(strings[2]);
                return arr[1];
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            delegate.setUrl(url);
        }

        public String[] getStandSaturday() throws IOException, JSONException {
            String arr[] = new String[2];
            String str = "";
            URL url = new URL("http://www.reddit.com/r/StardustCrusaders/search.json?q=Stand+Saturday&sort=new&restrict_sr=on");
            Scanner scan = new Scanner(url.openStream());
            while(scan.hasNext()){
                str+=scan.next();
            }
            scan.close();
            JSONObject obj = new JSONObject(str);
            arr[0]=(obj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("name"));
            arr[1]=(obj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("url"));
            return arr;
        }
    }

}
