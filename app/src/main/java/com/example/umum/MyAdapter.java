package com.example.umum;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {
    private List<UploadPDF> listData;
    private OnItemClickListener listener;
    private OnItemLongClickListener lOngClickListener;
    private final Context context;
    private final List<UploadPDF> uploadPDFList;

    public MyAdapter(Context context, List<UploadPDF> listData) {
        this.listData = listData;
        this.uploadPDFList = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout_iten, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        UploadPDF ld = listData.get(position);
        holder.txtid.setText(ld.getAutor());
        holder.txtname.setText(ld.getTitulo());
        holder.id.setText(ld.getUploadPDFKey());
        holder.path.setText(ld.getPath());
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {

        return new Filter() {
            @NonNull
            @Override
            protected FilterResults performFiltering(@NonNull CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    listData = uploadPDFList;
                } else {

                    ArrayList<UploadPDF> PDFliest = new ArrayList<>();

                    for (UploadPDF uploadPDF : uploadPDFList) {

                        if (uploadPDF.getTitulo().contains(charString) || uploadPDF.getAutor().contains(charString)) {
                            PDFliest.add(uploadPDF);
                        }
                    }

                    listData = PDFliest;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, @NonNull FilterResults filterResults) {
                listData = (List<UploadPDF>) filterResults.values;
                notifyDataSetChanged();
            }
        };


    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtid;
        private final TextView txtname;
        private final TextView id;
        private final TextView path;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtid = itemView.findViewById(R.id.nautor);
            txtname = itemView.findViewById(R.id.ntitulo);
            id = itemView.findViewById(R.id.docID);
            path = itemView.findViewById(R.id.path);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && lOngClickListener != null) {
                    lOngClickListener.onItemLongClick(position);
                }
                return true;
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemLongClickListener {
        void onItemLongClick(int position);

    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.lOngClickListener = listener;
    }

}











