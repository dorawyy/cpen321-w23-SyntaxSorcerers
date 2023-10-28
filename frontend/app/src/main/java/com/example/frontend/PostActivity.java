package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.frontend.apiWrappers.ServerRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");
        String writtenBy = intent.getStringExtra("writtenBy");
        String content = intent.getStringExtra("content");
        String likesCount = intent.getStringExtra("likesCount");
        String commentCount = intent.getStringExtra("commentCount");
        boolean userLiked = intent.getBooleanExtra("userLiked", false);
        String date = intent.getStringExtra("date");


        ((TextView) findViewById(R.id.post_user)).setText(writtenBy);
        ((TextView) findViewById(R.id.post_content)).setText(content);
        ((TextView) findViewById(R.id.number_of_likes)).setText(likesCount);
        ((TextView) findViewById(R.id.number_of_comments)).setText(commentCount);
        ((TextView) findViewById(R.id.post_date)).setText(date);

        if (userLiked) {
            ((ImageButton) findViewById(R.id.like_button)).setImageResource(R.drawable.baseline_thumb_up_alt_24);
        }

        findViewById(R.id.like_button).setOnClickListener(v -> {
            // TODO: add call to remove and add like
            PostActivity.this.recreate();
        });

        findViewById(R.id.create_button).setOnClickListener(v -> {
            addComment(postId);
        });

        getComments(postId);
    }

    private void addComment(String postId) {
        String comment = (((EditText) findViewById(R.id.commentMessage)).getText()).toString();
        SharedPreferences sharedPreferences = getSharedPreferences("GoogleAccountInfo", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        ServerRequest serverRequest = new ServerRequest(userId);

        ServerRequest.ApiRequestListener apiRequestListener = new ServerRequest.ApiRequestListener() {
            @Override
            public void onApiRequestComplete(JsonElement response) throws ParseException {
                Log.d(ServerRequest.RequestTag, "Success");
                findViewById(R.id.commentMessage).clearFocus();
                ((EditText) findViewById(R.id.commentMessage)).setText("");
                PostActivity.this.recreate();
            }

            @Override
            public void onApiRequestError(String error) {
                Log.d(ServerRequest.RequestTag, "Failure");
                Log.d(ServerRequest.RequestTag, error);
            }
        };

        JsonObject body = new JsonObject();
        body.addProperty("content", comment);
        body.addProperty("writtenBy", userId);
        body.addProperty("postId", postId);
        try {
            serverRequest.makePostRequest("/comments", body, apiRequestListener);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void getComments(String postId) {
        SharedPreferences sharedPreferences = getSharedPreferences("GoogleAccountInfo", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        ServerRequest serverRequest = new ServerRequest(userId);
        ServerRequest.ApiRequestListener apiRequestListener = new ServerRequest.ApiRequestListener() {
            @Override
            public void onApiRequestComplete(JsonElement response) throws ParseException {
                Log.d("Comments", response.toString());
                for(int i = 0;  i < response.getAsJsonArray().size(); i++) {
                    JsonObject comment = response.getAsJsonArray().get(i).getAsJsonObject();
                    Log.d("ForumViewActivity", comment.toString());
                    View commentView = getLayoutInflater().inflate(R.layout.comment_card, null);
                    commentView.setTag(comment.get("commentId").getAsString());
                    ((TextView) commentView.findViewById(R.id.comment_user)).setText(comment.get("writtenBy").getAsString());
                    ((TextView) commentView.findViewById(R.id.comment_content)).setText(comment.get("content").getAsString());

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.US);
                    Date date = inputFormat.parse(comment.get("dateWritten").getAsString());
                    assert date != null;
                    String formattedDate = outputFormat.format(date);
                    Log.d("ForumViewActivity", formattedDate);
                    ((TextView) commentView.findViewById(R.id.comment_date)).setText(formattedDate);

                    ((LinearLayout) findViewById(R.id.commentLayout)).addView(commentView);
                }
            }

            @Override
            public void onApiRequestError(String error) {
                Log.d(ServerRequest.RequestTag, "Failure");
                Log.d(ServerRequest.RequestTag, error);
            }
        };

        try {
            serverRequest.makeGetRequest("/comments/" + postId, apiRequestListener);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}