package com.example.ictko.SimpleNote;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MemoRecyclerAdapter extends RecyclerView.Adapter<MemoRecyclerAdapter.ViewHolder> {

    public void swap(List<Memo> newMemoList) {
        mData = newMemoList;
        notifyDataSetChanged();
    }
    public void insert(List<Memo> memoList) {
        mData = memoList;
        //notifyItemInserted(0);
        notifyDataSetChanged();

    }
    public void update(List<Memo> memoList, int position) {
        mData = memoList;
        notifyItemChanged(position);
    }



    //Event Bus 클래스
    public static class ItemClickEvent {
        public ItemClickEvent(View view, int position, long id) {
            this.view = view;
            this.position = position;
            this.id = id;
        }
        public View view;
        public int position;
        public long id;

    }
    //Event Bus 클래스
    public static class ItemDelClickEvent {
        public ItemDelClickEvent(int position, long id) {
            this.position = position;
            this.id = id;
        }
        public int position;
        public long id;

    }
    //Event Bus 클래스
    public static class ItemLongClickEvent {
        public ItemLongClickEvent(int position, long id) {
            this.position = position;
            this.id = id;
        }

        public int position;
        public long id;

    }

    private List<Memo> mData;
    private final Context mContext;
    public MemoRecyclerAdapter(Context context, List<Memo> memoList) {

        mData = memoList;
        mContext = context;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //뷰를 새로 만들 때
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo,parent,false);

        return new ViewHolder(convertView);
    }




    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // 데이터
        final Memo memo = mData.get(position);



        // 화면에 뿌리기
        holder.titleTextView.setText(memo.getTitle());
        holder.contentTextView.setText(memo.getContents());
        holder.dateTextView.setText(memo.getDate());

        /*if(memo.getImg_uri() !=null){
            //사진
            Glide.with(mContext)
                    .load(memo.getImg_uri())
                    .thumbnail(0.3f)
                    .into(holder.imageView);
        }*/

       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {
               EventBus.getDefault().post(new ItemLongClickEvent(position,memo.getId()));
               return true;
           }
       });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity에 onItemClick이 받음
                EventBus.getDefault().post(new ItemClickEvent(holder.imageView,position,memo.getId()));
                Log.d("TAG","onBindViewHolder memo.getId 값 : " + memo.getId());
            }
        });
        /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new ItemLongClickEvent(position,memo.getId()));
                return true;
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView dateTextView;
        TextView contentTextView;
        ImageView imageView;
        //ImageButton delBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            // 레이아웃 들고 오기
            TextView titleTextView = (TextView) itemView.findViewById(R.id.titletxt);
            titleTextView.setSelected(true);// 텍스트가 물흐르게 하는효과
            TextView contentTextView = (TextView) itemView.findViewById(R.id.contentTxt);
            contentTextView.setEllipsize(TextUtils.TruncateAt.END);

            TextView dateTextView = (TextView) itemView.findViewById(R.id.datetxt);

            ImageView imgView = (ImageView) itemView.findViewById(R.id.imgView);
            //ImageButton delBtn = (ImageButton) itemView.findViewById(R.id.delBtn);


            this.titleTextView = titleTextView;
            this.contentTextView = contentTextView;
            this.dateTextView = dateTextView;
            this.imageView = imgView;
            //this.delBtn = delBtn;



        }

    }

}



