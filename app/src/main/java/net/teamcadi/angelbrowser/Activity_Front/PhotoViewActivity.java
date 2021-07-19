package net.teamcadi.angelbrowser.Activity_Front;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import net.teamcadi.angelbrowser.R;

public class PhotoViewActivity extends AppCompatActivity {

    private static final String TAG = PhotoViewActivity.class.getSimpleName();
    private PhotoView pvImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        pvImage = (PhotoView) findViewById(R.id.photoView_Image);

        Log.i(TAG, getIntent().getStringExtra("elevPic"));


        Glide.with(this).asGif()
                .load(getIntent().getStringExtra("elevPic"))
                .into(pvImage); // 3중 연결 >> ElevatorActivity에서 꼽는게 일로 들어오는 것임

    }
}
