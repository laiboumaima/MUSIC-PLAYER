package com.example.musically;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.musically.Appclass.ACTION_NEXT;
import static com.example.musically.Appclass.ACTION_PLAY;
import static com.example.musically.Appclass.ACTION_PREVIOUS;
import static com.example.musically.Appclass.CHANNEL_ID_2;
import static com.example.musically.MainActivity.musicFiles;

public class PlayerActivity extends AppCompatActivity implements Actionplaying , ServiceConnection , SensorEventListener {
  TextView songname, durationplay,durationtotal;
  ImageView nextbtn,prevbtn,reaptbtn,play;
  SeekBar seekBar;
  int position = -1;
  MediaSessionCompat mediaSessionCompat;
 static ArrayList<MusicFiles>   listSongs=new ArrayList<>();
    static Uri uri;
   // static MediaPlayer mediaPlayer;
     private Handler handler =new Handler();
     private Thread playThread,prevThread,nextThread;
  MusicService musicService;
  private SensorManager sensorManager;
  private Sensor accelremoterSendor;
     private  Boolean isAcceleremotedAvailabled, Notfirst = false ;
     private  float currentx,currenty,currentz,lastx,lasty,lastz;
    private  float xDiffrent,yDiffrent,zDiffrent;
    private  float ShakeThreashold = 4f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mediaSessionCompat = new MediaSessionCompat(this,"myaudio");
        initViews();
        getIntentMethod();
        songname.setText(listSongs.get(position).getTitle());

      vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!= null){
             accelremoterSendor =  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
             isAcceleremotedAvailabled = true;
         }else{
             isAcceleremotedAvailabled = false;
         }




     seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
         @Override
         public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
             if (musicService !=null && fromUser ){
                 musicService.seekTo(progress*1000);
             }
         }

         @Override
         public void onStartTrackingTouch(SeekBar seekBar) {

         }

         @Override
         public void onStopTrackingTouch(SeekBar seekBar) {

         }
     });
     PlayerActivity.this.runOnUiThread(new Runnable() {
         @Override
         public void run() {
                if (musicService != null){
                    int currentPosition = musicService.getCurrentPosition()/1000;
                    seekBar.setProgress(currentPosition);
                    durationplay.setText(frommattedTime(currentPosition));
                }
                handler.postDelayed(this,1000);

         }
     });

    }

    @Override
    protected void onResume() {
        Intent intent= new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        playThreadbtn();
        nextThreadbtn();
        prevThreadbtn();
        
        super.onResume();
        if (isAcceleremotedAvailabled){
            sensorManager.registerListener(this,accelremoterSendor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
        if (isAcceleremotedAvailabled){
            sensorManager.unregisterListener(this);

        }
    }

    private void prevThreadbtn() {
        prevThread =new Thread(){
            @Override
            public void run() {
                super.run();
                prevbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevbtnClicked();

                    }});

            }
        };
        prevThread.start();

    }

    public void prevbtnClicked() {
        if (musicService.isPlaying()){

            musicService.stop();
            musicService.release();
            position =((position+1) % listSongs.size());
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metData(uri);
            songname.setText(listSongs.get(position).getTitle());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int currentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);
                        durationplay.setText(frommattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);

                }
            });
            musicService.Oncomplete();
            showNotification(R.drawable.ic_baseline_pause);
            play.setImageResource(R.drawable.ic_baseline_pause);
            musicService.start();

        }
        else {
            musicService.stop();
            musicService.release();
            position =((position-1) <0 ? (listSongs.size()-1) :(position-1));
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metData(uri);
            songname.setText(listSongs.get(position).getTitle());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int currentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);
                        durationplay.setText(frommattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);

                }
            });
            musicService.Oncomplete();
            showNotification(R.drawable.ic_baseline_play);
            play.setImageResource(R.drawable.ic_baseline_play);

        }

    }

    private void nextThreadbtn() {
        nextThread =new Thread(){
            @Override
            public void run() {
                super.run();
                nextbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextbtnClicked();

                    }});

            }
        };
        nextThread.start();
    }

    public void nextbtnClicked() {
        if (musicService.isPlaying()){

            musicService.stop();
            musicService.release();
            position =((position+1) % listSongs.size());
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metData(uri);
            songname.setText(listSongs.get(position).getTitle());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int currentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);
                        durationplay.setText(frommattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);

                }
            });
            musicService.Oncomplete();
            showNotification(R.drawable.ic_baseline_pause);
            play.setImageResource(R.drawable.ic_baseline_pause);
            musicService.start();

        }
        else {
            musicService.stop();
            musicService.release();
            position =((position+1)% listSongs.size());
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metData(uri);
            songname.setText(listSongs.get(position).getTitle());
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int currentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);
                        durationplay.setText(frommattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);

                }
            });
            musicService.Oncomplete();
            showNotification(R.drawable.ic_baseline_play);
            play.setImageResource(R.drawable.ic_baseline_play);
        }
    }

    private void playThreadbtn() {
        playThread =new Thread(){
            @Override
            public void run() {
                super.run();
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playbtnClicked();

                    }});

            }
        };
        playThread.start();
    }

    public void playbtnClicked() {
        if (musicService.isPlaying()){
            play.setImageResource(R.drawable.ic_baseline_play);
            showNotification(R.drawable.ic_baseline_play);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int currentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);
                        durationplay.setText(frommattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);

                }
            });
        } else{
            showNotification(R.drawable.ic_baseline_pause);
            play.setImageResource(R.drawable.ic_baseline_pause);
             musicService.start();
            musicService.Oncomplete();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int currentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(currentPosition);
                        durationplay.setText(frommattedTime(currentPosition));
                    }
                    handler.postDelayed(this,1000);

                }
            });
            musicService.Oncomplete();


        }
    }

    private String frommattedTime(int currentPosition) {
        String totalout="";
        String totalNew ="";
        String seconds = String.valueOf(currentPosition % 60);
         String minutes = String.valueOf(currentPosition/60);
           totalout = minutes+":"+seconds;
           totalNew =minutes + ":" +"0"+seconds;
           if (seconds.length() == 1) {
               return totalNew;

           } else
           {
               return  totalout;
           }



    }

    private void getIntentMethod() {
         position=getIntent().getIntExtra("position",-1);
         listSongs =musicFiles;
         if (listSongs != null ){
             play.setImageResource(R.drawable.ic_baseline_pause);
             uri = Uri.parse(listSongs.get(position).getPath());
         }
        showNotification(R.drawable.ic_baseline_pause);
        Intent intent = new Intent(this,MusicService.class);
         intent.putExtra("serviceposition",position);
         startService(intent);



    }

    private void initViews() {
        songname=findViewById(R.id.songname);
        durationplay=findViewById(R.id.durationplayed);
        durationtotal=findViewById(R.id.durationtotal);
        nextbtn=findViewById(R.id.id_next);
        prevbtn=findViewById(R.id.id_prev);
        reaptbtn=findViewById(R.id.id_shuffle_on);
        play=findViewById(R.id.play_pause);
        seekBar=findViewById(R.id.seekbar);

    }
    private  void metData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int duration_total = Integer.parseInt(listSongs.get(position).getDuration())/1000;

        durationtotal.setText(frommattedTime(duration_total) );
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
   MusicService.Mybinder mybinder = (MusicService.Mybinder) service;
   musicService = mybinder.getService();
   musicService.setcallback(this);
        Toast.makeText(this, "connected" + musicService,Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration()/1000);
        metData(uri);
        musicService.Oncomplete();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
   musicService = null;
    }
    void showNotification(int playPauseBtn){
        Intent intent = new Intent(this,PlayerActivity.class);
        PendingIntent contentIntet = PendingIntent.getActivity(this,
                0,intent,0);

        Intent previntent = new Intent(this,NotificationRecevier.class).setAction(ACTION_PREVIOUS);
        PendingIntent prevpending  = PendingIntent.getBroadcast(this,0,previntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseintent = new Intent(this,
                NotificationRecevier.class).setAction(ACTION_PLAY);
        PendingIntent pausepending = PendingIntent.getBroadcast(this,
                0,pauseintent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextintent = new Intent(this,
                NotificationRecevier.class).setAction(ACTION_NEXT);
        PendingIntent nextpending = PendingIntent.getBroadcast(this,
                0,nextintent,PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.image);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(bitmap)
                .setContentTitle(listSongs.get(position).getTitle())
                 .addAction(R.drawable.ic_skip_previous,"previous",prevpending)
                .addAction(playPauseBtn,"playpause",pausepending)
                .addAction(R.drawable.ic_skip_next,"next",nextpending)

                 .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                 .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntet).setOnlyAlertOnce(true)
                .build()
                ;
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
         notificationManager.notify(0,notification);



    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        currentx = event.values[0];
        currenty = event.values[1];
        currentz = event.values[2];
        if (Notfirst){
            xDiffrent = Math.abs(lastx - currentx);
            yDiffrent = Math.abs(lasty - currenty);
            zDiffrent = Math.abs(lastz - currentz);
            if ((xDiffrent > ShakeThreashold && yDiffrent > ShakeThreashold ) ||
                    (xDiffrent > ShakeThreashold && zDiffrent > ShakeThreashold ) ||
                    (yDiffrent > ShakeThreashold && zDiffrent > ShakeThreashold )
            ){
                nextbtnClicked();

            }else
            {



            }
        }

        lastx = currentx;
        lasty = currenty;
        lastz = currentz;
        Notfirst=true;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
}