package local.andregg.lab_2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<NewsItem> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    RecyclerViewAdapter(Context context, List<NewsItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NewsItem item = mData.get(position);
        holder.newsItemHeader.setText(item.returnHeader());
        holder.newsItemDescription.setText(item.returnDescription());

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void clear(MainActivity ref){
        mData.clear();
        ref.updateRecyclerView();
    }

    public void setData(MainActivity ref, List<NewsItem> data) {
        this.mData = data;
        ref.updateRecyclerView();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView newsItemHeader;
        TextView newsItemDescription;

        ViewHolder(View itemView) {
            super(itemView);
            newsItemHeader = itemView.findViewById(R.id.newsItemHeader);
            newsItemDescription = itemView.findViewById(R.id.newsItemDescription);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    NewsItem getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }
}
