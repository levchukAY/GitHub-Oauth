package com.artiomlevchuk.githuboauth.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.artiomlevchuk.githuboauth.api.entity.Repository;

import java.util.ArrayList;
import java.util.List;

public class ReposAdapter extends RecyclerView.Adapter<ReposAdapter.SimpleViewHolder>  {

    private List<Repository> repos;

    public ReposAdapter() {
        repos = new ArrayList<>();
    }

    public void setReposList(List<Repository> repos) {
        this.repos = repos;
        notifyDataSetChanged();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new TextView(parent.getContext());
        return new SimpleViewHolder(view);
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private SimpleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.textView.setText(repos.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return repos.size();
    }
}
