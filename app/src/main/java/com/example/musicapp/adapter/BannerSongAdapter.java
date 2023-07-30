package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.databinding.ItemBannerSongBinding;
import com.example.musicapp.listener.IOnClickSongItemListener;
import com.example.musicapp.model.Song;
import com.example.musicapp.utils.GlideUtils;

import java.util.List;

public class BannerSongAdapter extends RecyclerView.Adapter<BannerSongAdapter.BannerSongViewHolder> {

    private final List<Song> songList;
    public final IOnClickSongItemListener iOnClickSongItemListener;

    public BannerSongAdapter(List<Song> songList, IOnClickSongItemListener iOnClickSongItemListener) {
        this.songList = songList;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public BannerSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerSongBinding itemBannerSongBinding = ItemBannerSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BannerSongViewHolder(itemBannerSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerSongViewHolder holder, int position) {
        Song song = songList.get(position);
        if (song == null) {
            return;
        }
        GlideUtils.loadUrlBanner(song.getImage(), holder.itemBannerSongBinding.imageBanner);
        holder.itemBannerSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
    }

    @Override
    public int getItemCount() {
        if (songList != null) {
            return songList.size();
        }
        return 0;
    }

    public static class BannerSongViewHolder extends RecyclerView.ViewHolder {

        private final ItemBannerSongBinding itemBannerSongBinding;

        public BannerSongViewHolder(@NonNull ItemBannerSongBinding itemBannerSongBinding) {
            super(itemBannerSongBinding.getRoot());
            this.itemBannerSongBinding = itemBannerSongBinding;
        }
    }
}