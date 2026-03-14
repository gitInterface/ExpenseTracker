package edu.ustudio.expensetracker.ui.home;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.ustudio.expensetracker.R;
import edu.ustudio.expensetracker.data.local.entity.ExpenseEntity;

public class ExpenseCardAdapter extends RecyclerView.Adapter<ExpenseCardAdapter.VH> {

    private final List<ExpenseEntity> items = new ArrayList<>();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());

    public void setItems(List<ExpenseEntity> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ExpenseEntity e = items.get(position);

        h.txtAmountBadge.setText("$" + e.amount);

        String dateStr = df.format(new Date(e.createdAt));
        h.txtDate.setText(dateStr);

        String status = (e.status == null || e.status.isEmpty()) ? "UNCLASSIFIED" : e.status;
        h.txtStatus.setText(status);

        h.btnDelete.setOnClickListener(v -> {

            if (deleteListener != null) {
                deleteListener.onDelete(e);
            }

        });

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

                        if (cardClickListener != null) {
                            cardClickListener.onCardClick(e);
                        }

                    })
                    .start();

        });

        // 圖片：先用 URI 顯示（之後我們會改成 copy 到 app private storage）
        if (e.imageUri != null && !e.imageUri.isEmpty()) {
            try {
                Glide.with(h.imgExpense.getContext())
                        .load(new java.io.File(e.imageUri))
                        .centerCrop()
                        .into(h.imgExpense);
            } catch (Exception ex) {
                h.imgExpense.setImageResource(android.R.color.darker_gray);
            }
        } else {
            h.imgExpense.setImageResource(android.R.color.darker_gray);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgExpense;
        TextView txtAmountBadge;
        TextView txtDate;
        TextView txtStatus;
        ImageButton btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            imgExpense = itemView.findViewById(R.id.imgExpense);
            txtAmountBadge = itemView.findViewById(R.id.txtAmountBadge);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private OnDeleteClickListener deleteListener;
    public interface OnDeleteClickListener {
        void onDelete(ExpenseEntity expense);
    }
    public void setOnDeleteClickListener(OnDeleteClickListener l) {
        deleteListener = l;
    }

    private OnCardClickListener cardClickListener;
    public interface OnCardClickListener {
        void onCardClick(ExpenseEntity expense);
    }
    public void setOnCardClickListener(OnCardClickListener l) {
        cardClickListener = l;
    }
}