package com.example.musically;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   static ArrayList<MusicFiles>  musicFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission();
    }

    private void permission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        }
        else
        {
            Toast.makeText(this,"Permission Granted !",Toast.LENGTH_SHORT).show();
            musicFiles = getAllaudio(this);
            intitViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
        if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"permission Granted !",Toast.LENGTH_SHORT).show();
            musicFiles=getAllaudio(this);
            intitViewPager();
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
    }

    private void intitViewPager() {
        ViewPager viewPager =findViewById(R.id.viewpager);
        TabLayout tabLayout=findViewById(R.id.tab_layout);
        viewpagerAdapter viewpagerAdapter= new viewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.addFragments(new SongsFragment(),"title");
        viewpagerAdapter.addFragments(new AlbumFragment(),"Albums");
        viewPager.setAdapter(viewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }
    public  static  class viewpagerAdapter extends FragmentPagerAdapter{
    private ArrayList<Fragment>  fragments;
    private ArrayList<String> titles;
        public viewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles=new ArrayList<>();
        }
   void addFragments (Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);

   }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position) ;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    public  static  ArrayList<MusicFiles> getAllaudio(Context context){
        ArrayList<MusicFiles>  list =new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST

               };

       Cursor cursor = context.getContentResolver().query(
               uri,projection,null,null,null
       );
       if (cursor!=null){
           while (cursor.moveToNext()){
             ;
               String path = cursor.getString(0);
               String title = cursor.getString(1);
               String duration = cursor.getString(2);
               String artist = cursor.getString(3);

               MusicFiles musicFiles = new MusicFiles(path,title,duration,artist);
            Log.e("path"+path,"title"+title);
               list.add(musicFiles);


           }
           cursor.close();
       }
       return list;
    }


}