package com.example.musically;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.example.musically.PlayerActivity.listSongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mybinder = new Mybinder ();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles =new ArrayList<>();
    Uri uri;
    Actionplaying actionplaying;

  int position;
    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind","method");
        return mybinder;
    }




    public class  Mybinder extends Binder {

        MusicService getService(){
            return  MusicService.this;
        }
   }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionname=intent.getStringExtra("Action");
        int mposition=intent.getIntExtra("serviceposition",-1);
        if (mposition!=-1){ playMedia(mposition);}
        if (actionname!= null ){
            switch (actionname){
                case "playpause":
                   // Toast.makeText(this,"playpause",Toast.LENGTH_SHORT).show();
                    if (actionplaying != null) {
                        Log.e("inside","action");
                        actionplaying.playbtnClicked();
                    }

                     break;
                case "next":
                   // Toast.makeText(this,"next",Toast.LENGTH_SHORT).show();
                    if (actionplaying != null) {
                        Log.e("inside","action");
                        actionplaying.nextbtnClicked();
                    }
                    break;
                case "previous":

                    if (actionplaying != null) {
                        Log.e("inside","action");
                        actionplaying.prevbtnClicked();
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int Sposition) {
        musicFiles = listSongs;
        position=Sposition;
        if (mediaPlayer !=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (musicFiles !=null){
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }else{
        createMediaPlayer(position);
        mediaPlayer.start();
        }
    }

    void start (){
        mediaPlayer.start();
    }
    void stop (){
        mediaPlayer.stop();
    }
    void pause (){
        mediaPlayer.pause();
    }
    boolean isPlaying(){
   return  mediaPlayer.isPlaying();
    }
    void release(){
        mediaPlayer.release();
    }
    int getDuration(){
      return   mediaPlayer.getDuration();
    }
    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }
    void createMediaPlayer(int positionint){
        position =  positionint;
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer =MediaPlayer.create(getBaseContext(),uri);
    }
    int getCurrentPosition(){
    return     mediaPlayer.getCurrentPosition();

    }
    void Oncomplete(){
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionplaying!=null){
            actionplaying.nextbtnClicked();
            if (mediaPlayer != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
                Oncomplete();
            }
        }

    }
    void setcallback(Actionplaying actionplaying){
        this.actionplaying =actionplaying;
    }
}
