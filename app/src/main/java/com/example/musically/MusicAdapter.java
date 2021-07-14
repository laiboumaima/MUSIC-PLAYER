package com.example.musically;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicAdapter  extends RecyclerView.Adapter<MusicAdapter.Myview> {

   private Context context;
   private ArrayList<MusicFiles> files;

   MusicAdapter( Context context,ArrayList<MusicFiles> files){
       this.context=context;
       this.files=files;
   }
 Myview  holder;
    @NonNull
    @Override
    public Myview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);
        return new Myview(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Myview holder, int position) {
      holder.filename.setText(files.get(position).getTitle());
      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent =new Intent(context,PlayerActivity.class);
              intent.putExtra("position",position);
              context.startActivity(intent);
          }
      });

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class  Myview extends RecyclerView.ViewHolder{
   TextView filename ;
        public Myview(@NonNull View itemView) {
            super(itemView);
            filename=itemView.findViewById(R.id.title);

        }
    }

}
