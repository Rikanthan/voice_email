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

public class SentboxHolder extends RecyclerView.Adapter<SentboxHolder.ImageViewHolder> {
    private final Context mContext;
    private final List<Sentbox> mSentbox;
    private static OnItemClickListener mListener;
    public SentboxHolder(Context context, List<Sentbox> Carts) {
        mContext = context;
        mSentbox = Carts;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sentbox_recycle, parent, false);
        return new ImageViewHolder(v);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Sentbox Sentbox = mSentbox.get(position);
        holder.Sentboxmsg.setText("   Message :  "+ Sentbox.getMsg());
        holder.receivedDate.setText("   Date :  "+ Sentbox.getDate()+"   "+Sentbox.getTime());
        holder.receiver.setText("  < Receiver >   "+Sentbox.getReceiver());

    }
    @Override
    public int getItemCount() {
        return mSentbox.size();
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView Sentboxmsg,receivedDate,receiver;

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
            Sentboxmsg = itemView.findViewById(R.id.sentboxmsg);
            receivedDate=itemView.findViewById(R.id.sentdate);
            receiver=itemView.findViewById(R.id.receiver);
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
