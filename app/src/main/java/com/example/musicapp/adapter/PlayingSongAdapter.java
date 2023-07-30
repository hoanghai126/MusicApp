package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.databinding.ItemSongPlayingBinding;
import com.example.musicapp.listener.IOnClickSongItemListener;
import com.example.musicapp.listener.IOnClickSongPlayingItemListener;
import com.example.musicapp.model.Song;
import com.example.musicapp.utils.GlideUtils;

import java.util.List;

public class PlayingSongAdapter extends RecyclerView.Adapter<PlayingSongAdapter.PlayingSongViewHolder>{

    private List<Song> songList;

    private IOnClickSongPlayingItemListener iOnClickSongPlayingItemListener;

    public PlayingSongAdapter(List<Song> mListSongs, IOnClickSongPlayingItemListener iOnClickSongPlayingItemListener) {
        this.songList = mListSongs;
        this.iOnClickSongPlayingItemListener = iOnClickSongPlayingItemListener;
    }

    @NonNull
    @Override
    public PlayingSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongPlayingBinding itemSongPlayingBinding = ItemSongPlayingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlayingSongViewHolder(itemSongPlayingBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayingSongViewHolder holder, int position) {
        Song song = songList.get(position);
        if (song == null) {
            return;
        }
        if (song.isPlaying()) {
            holder.itemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.background_bottom);
            holder.itemSongPlayingBinding.imgPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.itemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.white);
            holder.itemSongPlayingBinding.imgPlaying.setVisibility(View.GONE);
        }
        GlideUtils.loadUrl(song.getImage(), holder.itemSongPlayingBinding.imgSong);
        holder.itemSongPlayingBinding.tvSongName.setText(song.getName());
        holder.itemSongPlayingBinding.tvArtist.setText(song.getSinger());

        holder.itemSongPlayingBinding.layoutItem.setOnClickListener(v
                -> iOnClickSongPlayingItemListener.onClickItemSongPlaying(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        if(songList == null){
            return 0;
        } else {
            return songList.size();
        }
    }

    public static class PlayingSongViewHolder extends RecyclerView.ViewHolder{

        private final ItemSongPlayingBinding itemSongPlayingBinding;

        public PlayingSongViewHolder(ItemSongPlayingBinding itemSongPlayingBinding) {
            super(itemSongPlayingBinding.getRoot());
            this.itemSongPlayingBinding = itemSongPlayingBinding;
        }
    }
}
