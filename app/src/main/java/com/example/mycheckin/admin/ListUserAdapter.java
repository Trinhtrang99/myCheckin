package com.example.mycheckin.admin;

import static com.example.mycheckin.model.Common.EMAIL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycheckin.R;
import com.example.mycheckin.databinding.LineuserBinding;
import com.example.mycheckin.model.User;
import com.example.mycheckin.update_user;
import com.example.mycheckin.utils.SharedUtils;

import java.util.List;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ListUserViewHolder> {
    List<User> list;
    private iClick iClick;
    private Context context;

    public ListUserAdapter(List<User> list, iClick iClick,Context context) {
        this.list = list;
        this.iClick = iClick;
        this.context = context;
    }

    @NonNull
    @Override
    public ListUserAdapter.ListUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LineuserBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.lineuser, parent, false);
        ListUserViewHolder viewHolder = new ListUserViewHolder(binding);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ListUserAdapter.ListUserViewHolder holder, int position) {
        holder.binding.getRoot().setOnClickListener(v -> {
           if (SharedUtils.getString(context, EMAIL, "").equals("admin@gmail.com")){
               iClick.clickEmployee(list.get(position), position);
           }else {
               iClick.clickEmployee2(list.get(position), position);
           }

        });
        holder.binding.txtName.setText(list.get(position).getName());
        if (list.get(position).getUrl()!=null){
            Glide.with(context)
                    .load(list.get(position).getUrl())
                    .centerCrop()
                    .into( holder.binding.avatar);
        }

    }

    public void updateList(List<User> userList) {
   //     this.list.clear();
        this.list = userList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListUserViewHolder extends RecyclerView.ViewHolder {
        LineuserBinding binding;

        public ListUserViewHolder(@NonNull LineuserBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface iClick {
        void clickEmployee(User user, int pos);
        void clickEmployee2(User user, int pos);
    }
}
