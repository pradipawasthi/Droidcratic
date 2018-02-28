package org.socratic.android.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaedongchicken.ytplayer.YoutubePlayerView;

import org.socratic.android.R;
import org.socratic.android.contract.NativeCardVideoContract;
import org.socratic.android.api.model.video.Video;
import org.socratic.android.api.model.video.VideoCard;
import org.socratic.android.databinding.FragmentNativeCardVideoBinding;
import org.socratic.android.globals.BaseSearchManager;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.TextSearchManager;
import org.socratic.android.util.DimenUtil;
import org.socratic.android.util.MultiLog;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by byfieldj on 9/21/17.
 */

public class NativeCardVideoFragment extends BaseFragment<FragmentNativeCardVideoBinding, NativeCardVideoContract.ViewModel> implements NativeCardVideoContract.View {
    
    private static final String EXTRA_IS_OCR = "isOCR";
    private static final String EXTRA_INDEX = "index";

    private String videoSource;

    @Inject
    TextSearchManager textSearchManager;

    @Inject
    OcrSearchManager ocrSearchManager;

    BaseSearchManager searchManager;

    private ArrayList<Video> relatedVideos;

    YoutubePlayerView youtubePlayerView;

    public NativeCardVideoFragment() {

    }

    public static NativeCardVideoFragment newInstance(int index, boolean isOCR) {

        Bundle args = new Bundle();

        NativeCardVideoFragment fragment = new NativeCardVideoFragment();
        args.putInt(EXTRA_INDEX, index);
        args.putBoolean(EXTRA_IS_OCR, isOCR);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_native_card_video);

        boolean isOCR = getArguments().getBoolean(EXTRA_IS_OCR);
        int cardIndex = getArguments().getInt(EXTRA_INDEX);

        if (isOCR) {
            searchManager = ocrSearchManager;
        } else {
            searchManager = textSearchManager;
        }

        VideoCard videoCard = searchManager.getResponseCard(cardIndex).getData().getVideoCard();
        videoSource = searchManager.getResponseCard(cardIndex).getSource();
        relatedVideos = searchManager.getResponseCard(cardIndex).getData().getVideoCard().getRelatedVideos();
        setupUI(videoCard);
        return rootView;

    }

    private void setupUI(VideoCard videoCard) {

        //Set the video title
        String videoTitle = videoCard.getPrimaryVideo().getTitle();
        binding.tvVideoTitle.setText(videoTitle);
        binding.tvVideoTitle.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-bold.otf"));


        // Set the channel name
        String channelName = videoCard.getPrimaryVideo().getChannel();
        MultiLog.d("NativeCardVideoFragment", "Channel name -> " + channelName);


        // Set the source
        videoSource = videoSource.substring(0,1).toUpperCase() + videoSource.substring(1, videoSource.length());
        binding.tvVideoSource.setText(videoSource + "-" + channelName);
        MultiLog.d("NativeCardVideoFragment", "Video Source -> " + videoSource);


        // Set the video description
        String description = videoCard.getPrimaryVideo().getDescription();
        binding.tvVideoDesc.setText(description);
        MultiLog.d("NativeCardVideoFragment", "Description -> " + description);


        // Load the video info
        String videoId = videoCard.getPrimaryVideo().getVideoId();


        loadVideo(videoId);
        setupRelatedVideos(relatedVideos);

    }

    private void loadVideo(String videoId){

        youtubePlayerView = binding.ytVideo;

        youtubePlayerView.initialize(videoId, new YoutubePlayerView.YouTubeListener() {

            @Override
            public void onReady() {
                // when player is ready.
            }

            @Override
            public void onStateChange(YoutubePlayerView.STATE state) {
                /**
                 * YoutubePlayerView.STATE
                 *
                 * UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, CUED, NONE
                 *
                 */
            }

            @Override
            public void onPlaybackQualityChange(String arg) {
            }

            @Override
            public void onPlaybackRateChange(String arg) {
            }

            @Override
            public void onError(String error) {
            }

            @Override
            public void onApiChange(String arg) {
            }

            @Override
            public void onCurrentSecond(double second) {
                // currentTime callback
            }

            @Override
            public void onDuration(double duration) {
                // total duration
            }

            @Override
            public void logs(String log) {
                // javascript debug log. you don't need to use it.
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        if(youtubePlayerView != null) {
            youtubePlayerView.pause();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(youtubePlayerView != null) {
            youtubePlayerView.destroy();
        }
    }

    private void setupRelatedVideos(final ArrayList<Video> videos) {

        if (videos != null && !videos.isEmpty()) {

            for (int i = 0; i < relatedVideos.size(); i++) {

                final Video video = relatedVideos.get(i);

                RelativeLayout relativeLayoutRow = new RelativeLayout(getContext());
                RelativeLayout.LayoutParams rowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


                String thumbUrl = video.getThumbnailMediumUrl();

                // Create and place the thumbnail imageview
                ImageView imageView = new ImageView(getContext());
                imageView.setId(i+10);
                RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                imageParams.setMargins(20, 20, 12, 12);
                imageParams.height = DimenUtil.convertDpToPx(getContext(), 63.7f);
                imageParams.width = DimenUtil.convertDpToPx(getContext(), 110f);
                imageView.setLayoutParams(imageParams);
                imageView.setOnClickListener(new LinkClickListener(video.getUrl()));
                relativeLayoutRow.addView(imageView);
                Glide.with(getContext()).load(thumbUrl).into(imageView);


                // Create and place the title
                TextView titleText = new TextView(getContext());
                RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                titleParams.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
                titleParams.setMargins(40, 20, 20, 0);
                titleText.setId(i+11);
                titleText.setText(video.getTitle());
                titleText.setMaxLines(3);
                titleText.setLayoutParams(titleParams);
                titleText.setOnClickListener(new LinkClickListener(video.getUrl()));
                relativeLayoutRow.addView(titleText);


                // Create and place the channel name
                TextView channelText = new TextView(getContext());
                RelativeLayout.LayoutParams channelParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                channelParams.addRule(RelativeLayout.BELOW, titleText.getId());
                channelParams.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
                channelParams.setMargins(40, 10, 20, 20);
                channelText.setId(i+12);
                channelText.setLayoutParams(channelParams);
                channelText.setText(video.getChannel());
                channelText.setTextColor(Color.parseColor("#B3FFFFFF"));
                channelText.setOnClickListener(new LinkClickListener(video.getUrl()));

                relativeLayoutRow.addView(channelText);


                relativeLayoutRow.setLayoutParams(rowParams);
                binding.llRelatedVideosContainer.addView(relativeLayoutRow);


            }
        }
    }

    class LinkClickListener implements  View.OnClickListener{

        private String videoUrl;

        public LinkClickListener(String videoUrl){
            this.videoUrl = videoUrl;
        }

        @Override
        public void onClick(View view) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(videoUrl));
            startActivity(browserIntent);
        }
    }
}
