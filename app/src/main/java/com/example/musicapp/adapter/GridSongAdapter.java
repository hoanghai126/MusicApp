package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.databinding.ItemSongGridBinding;
import com.example.musicapp.listener.IOnClickSongItemListener;
import com.example.musicapp.model.Song;
import com.example.musicapp.utils.GlideUtils;

import java.util.List;

public class GridSongAdapter extends RecyclerView.Adapter<GridSongAdapter.GridSongViewHolder>{

    private final List<Song> songList;

    public final IOnClickSongItemListener iOnClickSongItemListener;

    public GridSongAdapter(List<Song> mListSongs, IOnClickSongItemListener iOnClickSongItemListener) {
        this.songList = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public GridSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongGridBinding itemSongGridBinding = ItemSongGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GridSongViewHolder(itemSongGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GridSongViewHolder holder, int position) {
        Song song = songList.get(position);
        if (song == null) {
            return;
        }
        GlideUtils.loadUrl(song.getImage(), holder.itemSongGridBinding.imgSong);
        holder.itemSongGridBinding.tvSongName.setText(song.getName());
        holder.itemSongGridBinding.tvArtist.setText(song.getSinger());

        holder.itemSongGridBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
    }

    @Override
    public int getItemCount() {
        if(songList == null){
            return 0;
        } else {
            return songList.size();
        }
    }

    public static class GridSongViewHolder extends RecyclerView.ViewHolder{

        private final ItemSongGridBinding itemSongGridBinding;

        public GridSongViewHolder(ItemSongGridBinding itemSongGridBinding) {
            super(itemSongGridBinding.getRoot());
            this.itemSongGridBinding = itemSongGridBinding;
        }
    }
}
