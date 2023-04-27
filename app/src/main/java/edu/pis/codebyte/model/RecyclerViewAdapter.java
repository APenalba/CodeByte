package edu.pis.codebyte.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.pis.codebyte.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<MyData> data;

    public RecyclerViewAdapter(List<MyData> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.icon_home);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageResource(data.get(position).getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyData {
        private int imageResourceId;

        public MyData(int imageResourceId) {
            this.imageResourceId  = imageResourceId ;
        }

        public int getImageResourceId() {
            return imageResourceId;
        }
    }
}


