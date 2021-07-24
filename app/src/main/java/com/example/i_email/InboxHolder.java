package com.example.i_email;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class InboxHolder extends RecyclerView.Adapter<InboxHolder.ImageViewHolder> {
    private final Context mContext;
    private final List<Inbox> mInbox;
    private static OnItemClickListener mListener;
    public InboxHolder(Context context, List<Inbox> Carts) {
        mContext = context;
        mInbox = Carts;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.inbox_recycle, parent, false);
        return new ImageViewHolder(v);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Inbox inbox = mInbox.get(position);
        holder.inboxmsg.setText("   Message :  "+ inbox.getMsg());
        holder.receivedDate.setText("   Date :  "+ inbox.getDate()+"   "+inbox.getTime());
        holder.sender.setText("     < Sender >   "+inbox.getSender());

    }
    @Override
    public int getItemCount() {
        return mInbox.size();
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView inboxmsg,receivedDate,sender;

        @Override
        public void onClick(View v) {
            if(mListener !=null)
            {
                int position=getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION)
                {
                    mListener.onItemClick(position);
                }
            }
        }

        public ImageViewHolder(View itemView) {
            super(itemView);
            inboxmsg = itemView.findViewById(R.id.inboxmsg);
            receivedDate=itemView.findViewById(R.id.receivedate);
            sender=itemView.findViewById(R.id.sender);
            itemView.setOnClickListener(this);
        }

    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener= listener;
    }

}
