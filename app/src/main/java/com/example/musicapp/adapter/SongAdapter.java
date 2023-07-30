package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.databinding.ItemSongBinding;
import com.example.musicapp.listener.IOnClickSongItemListener;
import com.example.musicapp.model.Song;
import com.example.musicapp.utils.GlideUtils;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder>{

    private final List<Song> songList;

    public final IOnClickSongItemListener iOnClickSongItemListener;

    public SongAdapter(List<Song> mListSongs, IOnClickSongItemListener iOnClickSongItemListener) {
        this.songList = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongBinding itemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongViewHolder(itemSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        if (song == null) {
            return;
        }
        GlideUtils.loadUrl(song.getImage(), holder.itemSongBinding.imgSong);
        holder.itemSongBinding.tvSongName.setText(song.getName());
        holder.itemSongBinding.tvArtist.setText(song.getSinger());
        holder.itemSongBinding.tvCountView.setText(String.valueOf(song.getCount()));

        holder.itemSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
    }

    @Override
    public int getItemCount() {
        if(songList == null){
            return 0;
        } else {
            return songList.size();
        }
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder{

        private final ItemSongBinding itemSongBinding;

        public SongViewHolder(ItemSongBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.itemSongBinding = itemSongBinding;
        }
    }
}
