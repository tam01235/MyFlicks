package com.example.nhoxb.myflicks.ui.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhoxb.myflicks.R;
import com.example.nhoxb.myflicks.data.remote.model.Movie;
import com.example.nhoxb.myflicks.databinding.ActivityDetailBinding;
import com.example.nhoxb.myflicks.ui.main.MainActivity;
import com.example.nhoxb.myflicks.utils.Constant;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class DetailActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private ActivityDetailBinding binding;

    private TextView tvTitle, tvOverview, tvReleaseDate, tvAverageRate;
    private RatingBar ratingBar;
    private YouTubePlayerView playerView;

    private Movie movie;
    private String videoId;

    //Binding


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        // Store the field now without any need for casting or findViewById
        playerView = binding.playerView;
        tvTitle = binding.filmTitle;
        tvReleaseDate = binding.filmReleaseDate;
        tvOverview = binding.filmOverview;
        tvAverageRate = binding.averageRate;
        ratingBar = binding.ratingBar;

        playerView.initialize(Constant.YOUTUBE_ANDROID_KEY, this);

        Intent intent = getIntent();
        videoId = intent.getStringExtra(MainActivity.VIDEO_ID_KEY);
        movie = (Movie) intent.getExtras().get(MainActivity.SELECTED_ITEM_KEY);

        //Set
        if (movie != null) {
            tvTitle.setText(movie.getTitle());
            tvReleaseDate.setText("Release date: " + movie.getReleasePath());
            tvOverview.setText(movie.getDescription());
            tvAverageRate.setText(movie.getAverageVote() + " (" + movie.getVoteCount() + ")");
            ratingBar.setMax(10);
            ratingBar.setNumStars(5);
            ratingBar.setRating(movie.getAverageVote());
        }

        //Material design style
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorRatingBar, null), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorRatingBarStroke, null), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorRatingBarStroke, null), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(DetailActivity.this, "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Constant.YOUTUBE_ANDROID_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.player_view);
    }
}
