package com.c13.devin.moodfeedandroid.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.c13.devin.moodfeedandroid.MoodFeedModels.MoodFeedService;
import com.c13.devin.moodfeedandroid.R;

import org.json.JSONObject;

import retrofit.RestAdapter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StreamActivity extends Activity {
    public static final String MESSAGE_ID = "messageId";

    private String messageId;
    private Subscription subscription;
    private MoodFeedService moodFeedService;
    private TextView textView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        messageId = getIntent().getStringExtra(MESSAGE_ID);

        textView = (TextView) findViewById(R.id.textView);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://moodfeedapi.herokuapp.com")
                .build();

        moodFeedService = restAdapter.create(MoodFeedService.class);

        getStreamData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stream, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            getStreamData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        super.onDestroy();
    }

    public void getStreamData() {
        progressDialog = ProgressDialog.show(this, null, getString(R.string.getting_stream_data));

        if (subscription == null || subscription.isUnsubscribed()) {
            subscription = moodFeedService.getStreamData(messageId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<JSONObject>() {
                        @Override
                        public void onCompleted() {
                            Log.d("MOODFEED - Completed: ", "?");
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("MOODFEED - Error!", e.getCause().toString() + " - " + e.toString());
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onNext(JSONObject response) {
                            textView.setText(response.toString());
                            Log.d("MOODFEED - Response: ", response.toString());
                        }
                    });
        }
    }
}
