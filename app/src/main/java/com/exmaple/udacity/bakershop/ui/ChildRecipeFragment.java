package com.exmaple.udacity.bakershop.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exmaple.udacity.bakershop.RecipeViewModel;
import com.exmaple.udacity.bakershop.pojo.Procedure;
import com.exmaple.udacity.bakershop.R;
import com.exmaple.udacity.bakershop.pojo.Recipe;
import com.exmaple.udacity.bakershop.service.UtilIntentService;
import com.exmaple.udacity.bakershop.util.RecipeUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChildRecipeFragment extends Fragment implements ExoPlayer.EventListener {

    @BindView(R.id.exo_buffering)
    ProgressBar mProgressBar;

    @BindView(R.id.exo_reload)
    ImageView mRefreshImageView;

    @BindView(R.id.exo_artwork)
    ImageView exoArtWorkView;

    @BindView(R.id.childRecipeVideo)
    SimpleExoPlayerView exoPlayerView;

    @BindView(R.id.textViewRecipeDetailInstruction)
    TextView recipeDetailStepView;

    private OnFragmentInteractionListener mListener;

    private SimpleExoPlayer exoPlayer;
    private static MediaSessionCompat mediaSession;

    private int stepId;
    private int recipeId;

    private final static String ARG_STEP_ID="arg_step_id";
    private final static String ARG_RECIPE_ID="arg_recipe_id";
    private final static String ARG_PLAYER_POSITION="arg_player_position";
    private PlaybackStateCompat.Builder playbackStateCompat;

    RecipeViewModel recipeViewModel;


    private ArrayList<Procedure> procedureArrayList;

    public ChildRecipeFragment() {
        // Required empty public constructor
    }

    public static ChildRecipeFragment newInstance(int recipeId,int stepID) {
        ChildRecipeFragment fragment=new ChildRecipeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RECIPE_ID, recipeId);
        args.putInt(ARG_STEP_ID,stepID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments=getArguments();
        if(arguments!=null) {
            recipeId=getArguments().getInt(ARG_RECIPE_ID);
            stepId=getArguments().getInt(ARG_STEP_ID);
        }
        else {
            if(savedInstanceState!=null) {
                if(savedInstanceState.containsKey(ARG_RECIPE_ID) && savedInstanceState.containsKey(ARG_STEP_ID)) {
                    recipeId=savedInstanceState.getInt(ARG_RECIPE_ID);
                    stepId=savedInstanceState.getInt(ARG_STEP_ID);
                }
            }
        }
        recipeViewModel= ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        procedureArrayList=recipeViewModel.getProcedures(recipeId);
        stepId=stepId%procedureArrayList.size();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(ARG_STEP_ID,stepId);
        outState.putInt(ARG_RECIPE_ID,recipeId);
        if(exoPlayer!=null) {
            outState.putLong(ARG_PLAYER_POSITION,exoPlayer.getCurrentPosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_child_recipe, container, false);
        ButterKnife.bind(this,view);
        recipeDetailStepView.setText(procedureArrayList.get(stepId).getDescription());
        if(stepId==procedureArrayList.size()-1) {
            Recipe recipe=recipeViewModel.getRecipe(recipeId);
            String serving="\nServings: "+recipe.getServings();
            recipeDetailStepView.append(serving);
        }
        createMediaSession();
        initialisePlayer();
        if(savedInstanceState!=null) {
            if(savedInstanceState.containsKey(ARG_PLAYER_POSITION)) {
                if(exoPlayer!=null) {
                    exoPlayer.seekTo(savedInstanceState.getLong(ARG_PLAYER_POSITION));
                }
            }
        }
        if(getContext().getResources().getBoolean(R.bool.isRotated)) {
            exoPlayerView.setMinimumHeight(RecipeUtils.getScreenHeight(getContext()));
        }
        return view;
    }



    private void initialisePlayer() {
        if(mListener.onIsNetworkConnected()) {
            if(exoPlayer==null) {
                String videoUri=procedureArrayList.get(stepId).getVideoURL();
                if(videoUri.length()==0) {
                   exoArtWorkView.setImageResource(R.drawable.video_not_avaialble);
                    mProgressBar.setVisibility(View.GONE);
                    return;
                }
                exoPlayer= ExoPlayerFactory.newSimpleInstance(getContext(),
                        new DefaultTrackSelector(),new DefaultLoadControl());
                exoPlayerView.setPlayer(exoPlayer);
                String userAgent= Util.getUserAgent(getContext(),"bakershop");
                MediaSource mediaSource=new ExtractorMediaSource(Uri.parse(videoUri),
                        new DefaultDataSourceFactory(getContext(),userAgent),new DefaultExtractorsFactory(),
                        null,null);
                exoPlayer.addListener(this);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
            }
        }
        else {
            exoArtWorkView.setImageResource(R.drawable.connection_failure);
            mProgressBar.setVisibility(View.GONE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initialisePlayer();
                if(exoPlayer!=null) {
                    exoPlayer.seekTo(0);
                }
            }
        });
    }

    private void createMediaSession() {
        mediaSession=new MediaSessionCompat(getContext(),RecipeDetailStepActivity.class.getSimpleName());
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        playbackStateCompat=new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_REWIND|
                        PlaybackStateCompat.ACTION_FAST_FORWARD
        );
        mediaSession.setMediaButtonReceiver(null);
        mediaSession.setPlaybackState(playbackStateCompat.build());
        mediaSession.setCallback(new MediaSessionCallBack());
        mediaSession.setActive(true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        boolean onIsNetworkConnected();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        int mediaPLayBackState;
        if(playbackState==ExoPlayer.STATE_READY) {
            if(playWhenReady) {
                playbackStateCompat.setState(PlaybackStateCompat.STATE_PLAYING,
                                exoPlayer.getCurrentPosition(),1f);
                mediaPLayBackState=PlaybackStateCompat.STATE_PLAYING;
            }
            else {
                playbackStateCompat
                        .setState(PlaybackStateCompat.STATE_PAUSED,
                        exoPlayer.getCurrentPosition(),1f);
                mediaPLayBackState=PlaybackStateCompat.STATE_PAUSED;
            }
            mProgressBar.setVisibility(View.GONE);
            mRefreshImageView.setVisibility(View.GONE);
            mediaSession.setPlaybackState(playbackStateCompat.build());
        }
        if(playbackState==ExoPlayer.STATE_BUFFERING) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.GONE);
        }
        if(playbackState==ExoPlayer.STATE_ENDED) {
            mProgressBar.setVisibility(View.GONE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(getContext(),"Error Playing Media",Toast.LENGTH_LONG).show();
        mProgressBar.setVisibility(View.GONE);
        mRefreshImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onDestroy() {
        mediaSession.setActive(false);
        releasePLayer();
        super.onDestroy();
    }

    private void releasePLayer() {
        if(exoPlayer!=null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer=null;
        }
    }
    class MediaSessionCallBack extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onFastForward() {
            long time=exoPlayer.getCurrentPosition();
            long fwdDur= TimeUnit.SECONDS.toMillis(10);
            exoPlayer.seekTo(time+fwdDur);
        }

        @Override
        public void onRewind() {
            exoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver () {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mediaSession,intent);
        }
    }

    @Override
    public void onStop() {
        if(exoPlayer!=null) {
            exoPlayer.setPlayWhenReady(false);
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        if(exoPlayer!=null) {
            exoPlayer.setPlayWhenReady(true);
        }
        super.onResume();
    }
}
