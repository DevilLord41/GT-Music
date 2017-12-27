package org.redeagle.gtmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.Toast;

import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcPlayerView;
import com.example.jean.jcplayer.JcStatus;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements JcPlayerView.OnInvalidPathListener, JcPlayerView.JcPlayerViewStatusListener {
    private JcPlayerView player;
    private RecyclerView recyclerView;
    private AudioAdapter audioAdapter;
    private AdView adView;
    private InterstitialAd interAds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this,"");
        adView = findViewById(R.id.ads);
        interAds = new InterstitialAd(this);
        interAds.setAdUnitId("");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        player = (JcPlayerView) findViewById(R.id.jcplayer);
        ArrayList<JcAudio> musicList = new ArrayList<JcAudio>();
        musicList.add(JcAudio.createFromURL("Another One Bities Of Dust","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1zqcwflJjFT.mp3"));
        musicList.add(JcAudio.createFromURL("Be Starters","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1KMVtNBwtGW.mp3"));
        musicList.add(JcAudio.createFromURL("Crazy Frogs","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1IN5m15oEcu.mp3"));
        musicList.add(JcAudio.createFromURL("Dance Of The Knights","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1UtUmVB1Tl2.mp3"));
        musicList.add(JcAudio.createFromURL("Dumags","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1CcypsYOiS7.mp3"));
        musicList.add(JcAudio.createFromURL("Feel This Moment","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1AfxoRr8dk3.mp3"));
        musicList.add(JcAudio.createFromURL("Flight Of The Bumblebee","https://s1.vocaroo.com/media/download_temp/Vocaroo_s15cjibddJl2.mp3"));
        musicList.add(JcAudio.createFromURL("Game of Thrones","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1srPoxtfSeA.mp3"));
        musicList.add(JcAudio.createFromURL("Gee","https://s1.vocaroo.com/media/download_temp/Vocaroo_s14Lx19MsFxa.mp3"));
        musicList.add(JcAudio.createFromURL("Growtopia Menu","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1RU8xv4uosK.mp3"));
        musicList.add(JcAudio.createFromURL("Here With You","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1R52lycmdAp.mp3"));
        musicList.add(JcAudio.createFromURL("Hey There Darlin","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1qZzHc11Ybl.mp3"));
        musicList.add(JcAudio.createFromURL("I Need Your Love","https://s1.vocaroo.com/media/download_temp/Vocaroo_s1AJ93YXmOnN.mp3"));
        player.initPlaylist(musicList);
        player.playAudio(player.getMyPlaylist().get(1));

        player.registerInvalidPathListener(this);
        player.registerStatusListener(this);
        adapterSetup();

        interAds.loadAd(adRequest);

    }

    protected void adapterSetup() {
        audioAdapter = new AudioAdapter(player.getMyPlaylist());
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                    player.playAudio(player.getMyPlaylist().get(position));
            }

            @Override
            public void onSongItemDeleteClicked(int position) {
                Toast.makeText(MainActivity.this, "Delete song at position " + position,
                        Toast.LENGTH_SHORT).show();
//                if(player.getCurrentPlayedAudio() != null) {
//                    Toast.makeText(MainActivity.this, "Current audio = " + player.getCurrentPlayedAudio().getPath(),
//                            Toast.LENGTH_SHORT).show();
//                }
                removeItem(position);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(audioAdapter);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

    }
    @Override
    public void onPause(){
        super.onPause();
        if(interAds.isLoaded()) {
            interAds.show();
            interAds.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    player.createNotification();
                }
            });        
            
        } else {
            player.createNotification();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.kill();
    }

    @Override
    public void onPathError(JcAudio jcAudio) {
        Toast.makeText(this, jcAudio.getPath() + " with problems", Toast.LENGTH_LONG).show();
//        player.removeAudio(jcAudio);
//        player.next();
    }

    private void removeItem(int position) {
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        player.removeAudio(player.getMyPlaylist().get(position));
        audioAdapter.notifyItemRemoved(position);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override public void onPausedStatus(JcStatus jcStatus) {

    }

    @Override public void onContinueAudioStatus(JcStatus jcStatus) {

    }

    @Override public void onPlayingStatus(JcStatus jcStatus) {

    }

    @Override public void onTimeChangedStatus(JcStatus jcStatus) {
        updateProgress(jcStatus);
    }

    @Override public void onCompletedAudioStatus(JcStatus jcStatus) {
        updateProgress(jcStatus);
    }

    @Override public void onPreparedAudioStatus(JcStatus jcStatus) {
    }

    private void updateProgress(final JcStatus jcStatus) {

        runOnUiThread(new Runnable() {
            @Override public void run() {
                // calculate progress
                float progress = (float) (jcStatus.getDuration() - jcStatus.getCurrentPosition())
                    / (float) jcStatus.getDuration();
                progress = 1.0f - progress;
                audioAdapter.updateProgress(jcStatus.getJcAudio(), progress);
            }
        });
    }
}
