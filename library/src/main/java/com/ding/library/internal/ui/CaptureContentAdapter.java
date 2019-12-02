package com.ding.library.internal.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ding.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author:DingDeGao
 * time:2019-11-01-10:37
 * function: CaptureContentAdapter
 */
public class CaptureContentAdapter extends RecyclerView.Adapter<CaptureContentAdapter.VH> {

    private List<Entity> list = new ArrayList<>();
    private Context context;

    public CaptureContentAdapter(Context context) {
        this.context = context;
    }

    void clear(){
        list.clear();
        notifyDataSetChanged();
    }

    public void setData(List<Entity> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();

        Toast.makeText(context,"数据刷新完成",Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public CaptureContentAdapter.VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_content,
                viewGroup, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CaptureContentAdapter.VH viewHolder, int position) {
        final Entity entity = list.get(position);
        viewHolder.tvTitle.setText(entity.title);
        viewHolder.tvValue.setText(entity.value);
        viewHolder.tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = viewHolder.tvCopy.getContext();
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("copy", entity.value);
                if(cm != null) {
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvCopy;
        TextView tvValue;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCopy = itemView.findViewById(R.id.tvCopy);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }

    public static class Entity{
        String title;
        String value;

        public Entity(String title, String value) {
            this.title = title;
            this.value = value;
        }
    }
}
