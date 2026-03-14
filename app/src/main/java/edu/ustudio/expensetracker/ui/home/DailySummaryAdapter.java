package edu.ustudio.expensetracker.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.ustudio.expensetracker.R;
import edu.ustudio.expensetracker.data.model.DailySummary;

public class DailySummaryAdapter extends RecyclerView.Adapter<DailySummaryAdapter.VH> {

    private final List<DailySummary> items = new ArrayList<>();
    private final SimpleDateFormat df =
            new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    public void setItems(List<DailySummary> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_summary_card, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {

        DailySummary s = items.get(position);

        h.txtTotalAmount.setText("$" + s.totalAmount);

        String dateStr = df.format(new Date(s.expenseDate));
        h.txtDate.setText(dateStr);

        h.txtCount.setText(s.expenseCount + " 筆");

        h.itemView.setOnClickListener(v -> {

            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(80)
                    .withEndAction(() -> {

                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(80)
                                .start();

                        if (listener != null) {
                            listener.onDayClick(s.expenseDate);
                        }

                    })
                    .start();

        });

        h.btnDeleteDay.setOnClickListener(v -> {

            if (deleteListener != null) {
                deleteListener.onDeleteDay(s.expenseDate);
            }

        });

        h.shimmerCover.startShimmer();
        h.shimmerCover.setVisibility(View.VISIBLE);

        if (s.coverImageUri != null && !s.coverImageUri.isEmpty()) {

            Glide.with(h.imgCover.getContext())
                    .load(new File(s.coverImageUri))
                    .centerCrop()
                    .into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.drawable.Drawable>() {
                        @Override
                        public void onResourceReady(@androidx.annotation.NonNull android.graphics.drawable.Drawable resource,
                                                    com.bumptech.glide.request.transition.Transition<? super android.graphics.drawable.Drawable> transition) {
                            h.imgCover.setImageDrawable(resource);
                            h.shimmerCover.stopShimmer();
                            h.shimmerCover.hideShimmer();
                            h.shimmerCover.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {
                            h.imgCover.setImageDrawable(placeholder);
                        }

                        @Override
                        public void onLoadFailed(android.graphics.drawable.Drawable errorDrawable) {
                            h.imgCover.setImageResource(android.R.color.darker_gray);
                            h.shimmerCover.stopShimmer();
                            h.shimmerCover.hideShimmer();
                        }
                    });

        } else {
            h.imgCover.setImageResource(android.R.color.darker_gray);
            h.shimmerCover.stopShimmer();
            h.shimmerCover.hideShimmer();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView imgCover;
        TextView txtDate;
        TextView txtTotalAmount;
        TextView txtCount;
        ImageButton btnDeleteDay;
        ShimmerFrameLayout shimmerCover;

        VH(@NonNull View itemView) {
            super(itemView);

            imgCover = itemView.findViewById(R.id.imgCover);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmount);
            txtCount = itemView.findViewById(R.id.txtCount);
            btnDeleteDay = itemView.findViewById(R.id.btnDeleteDay);
            shimmerCover = itemView.findViewById(R.id.shimmerCover);
        }
    }

    private OnDayClickListener listener;
    public interface OnDayClickListener {
        void onDayClick(long expenseDate);
    }
    public void setOnDayClickListener(OnDayClickListener l) {
        listener = l;
    }

    private OnDeleteDayClickListener deleteListener;
    public interface OnDeleteDayClickListener {
        void onDeleteDay(long date);
    }
    public void setOnDeleteDayClickListener(OnDeleteDayClickListener l) {
        deleteListener = l;
    }
}