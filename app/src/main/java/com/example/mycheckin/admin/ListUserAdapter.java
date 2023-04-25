package com.example.mycheckin.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycheckin.R;
import com.example.mycheckin.databinding.LineuserBinding;
import com.example.mycheckin.model.User;

import java.util.List;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ListUserViewHolder> {
    List<User> list;
    private iClick iClick;

    public ListUserAdapter(List<User> list, iClick iClick) {
        this.list = list;
        this.iClick = iClick;
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
            iClick.clickEmployee(list.get(position), position);
        });
        holder.binding.txtName.setText(list.get(position).getName());
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
    }
}
