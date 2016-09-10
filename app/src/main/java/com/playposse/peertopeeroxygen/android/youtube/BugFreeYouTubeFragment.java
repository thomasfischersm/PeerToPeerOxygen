package com.playposse.peertopeeroxygen.android.youtube;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

/**
 * A version of {@link YouTubePlayerSupportFragment} that doesn't throw an too large transaction
 * exception on rotating the screen and re-initializes itself after rotating.
 */
public class BugFreeYouTubeFragment extends YouTubePlayerSupportFragment {

    private static final String LOG_CAT = BugFreeYouTubeFragment.class.getSimpleName();

    private static final String API_KEY = "AIzaSyDDguf1286_xmMqA8BmPzeBV3vEX8ojXIU";

    private static final String VIDEO_ID_ARG = "videoId";
    private static final String VIDEO_POSITION_ARG = "position";
    private static final String VIDEO_IS_PLAYING_ARG = "isPlaying";

    private YouTubePlayer player;
    private String videoId;
    private Integer position;
    private boolean isPlaying;

    @Override
    public void onActivityCreated(@Nullable Bundle args) {
        super.onActivityCreated(args);

        Log.i(LOG_CAT, "Got args in onActivityCreated " + args);
    }

    @Override
    public void onCreate(Bundle args) {
        super.onCreate(args);

        Log.i(LOG_CAT, "Got args in onCreate " + args);

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        Log.i(LOG_CAT, "setArguments was called " + args);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle args) {
        View rootView = super.onCreateView(layoutInflater, viewGroup, args);

        Log.i(LOG_CAT, "Got args in onCreateView " + args);
        Log.i(LOG_CAT, "Got arguments from getArguments " + getArguments());
        if (getArguments() != null) {
            videoId = getArguments().getString(VIDEO_ID_ARG);
        }
        if (args != null) {
            Log.i(LOG_CAT, "Inside if");
            if (videoId == null) {
                videoId = args.getString(VIDEO_ID_ARG);
            }
            position = args.getInt(VIDEO_POSITION_ARG, -1);
            position = (position == -1) ? null : position;
            isPlaying = args.getBoolean(VIDEO_IS_PLAYING_ARG, false);
        }

        initialize(API_KEY, new YouTubeInitializedListener());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle args) {
        // The super method is bad. Don't call it!

        Log.i(LOG_CAT, "onSaveInstanceState is called ");
        args.putString(VIDEO_ID_ARG, videoId);

        if (player != null) {
            args.putInt(VIDEO_POSITION_ARG, player.getCurrentTimeMillis());
            args.putBoolean(VIDEO_IS_PLAYING_ARG, player.isPlaying());
        }
    }

    public static YouTubePlayerSupportFragment newInstance(String videoId) {
        BugFreeYouTubeFragment fragment = new BugFreeYouTubeFragment();
        Bundle args = new Bundle();
        args.putString(VIDEO_ID_ARG, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    private class YouTubeInitializedListener implements YouTubePlayer.OnInitializedListener {

        @Override
        public void onInitializationSuccess(
                YouTubePlayer.Provider provider,
                YouTubePlayer player,
                boolean wasRestored) {

            BugFreeYouTubeFragment.this.player = player;

            if (videoId != null) {
                if (isPlaying) {
                    player.loadVideo(videoId, (position != null)? position : 0);
                } else {
                    player.cueVideo(videoId, (position != null)? position : 0);
                }
            }

            // The YouTube player has a bug where it doesn't release the horizontal orientation
            // after exiting full screen mode.
            player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean isFullScreen) {
                    if (!isFullScreen) {
                        getActivity().setRequestedOrientation(
                                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                }
            });
        }

        @Override
        public void onInitializationFailure(
                YouTubePlayer.Provider provider,
                YouTubeInitializationResult result) {

            Log.e(LOG_CAT, "Failed to initialize YouTube fragment " + result.name());
        }
    }
}
