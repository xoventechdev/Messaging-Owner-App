package xyz.xoventechdev.appforown;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.SmsViewHolder> {

    private List<SmsMessageModel> smsList;
    private List<String> smsKeys;
    private OnItemLongClickListener longClickListener;

    public SmsAdapter(List<SmsMessageModel> smsList, List<String> smsKeys, OnItemLongClickListener longClickListener) {
        this.smsList = smsList;
        this.smsKeys = smsKeys;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public SmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sms, parent, false);
        return new SmsViewHolder(view, longClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SmsViewHolder holder, int position) {
        SmsMessageModel sms = smsList.get(position);
        holder.tvPhoneNumber.setText(sms.phoneNumber);
        holder.tvMessageBody.setText(sms.messageBody);
        holder.tvTimestamp.setText(sms.timestamp);
        holder.tvMobileModel.setText(sms.mobileModel);
        holder.itemView.setTag(smsKeys.get(position)); // Set the key as the tag
        holder.tvMessageBody.setTextIsSelectable(true);

    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public static class SmsViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView tvPhoneNumber, tvMessageBody, tvTimestamp, tvMobileModel;
        OnItemLongClickListener longClickListener;

        public SmsViewHolder(@NonNull View itemView, OnItemLongClickListener longClickListener) {
            super(itemView);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvMessageBody = itemView.findViewById(R.id.tvMessageBody);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvMobileModel = itemView.findViewById(R.id.tvMobileModel);
            this.longClickListener = longClickListener;

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            longClickListener.onItemLongClick(getAdapterPosition(), (String) v.getTag());
            return true;
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position, String key);
    }
}