package ml.rushabh.smartdrive;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> implements Filterable{
    private List<Message> messageList;
    private List<Message> filteredMessageList;

    private Context context;
    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageHolder(view);
    }

    public MessageAdapter(Context context, List<Message> messageList){
        this.context = context;
        this.messageList = messageList;
        this.filteredMessageList = messageList;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder messageHolder, int position) {
        Message message = filteredMessageList.get(position);
        messageHolder.setValues(message);

    }

    @Override
    public int getItemCount() {
        return filteredMessageList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()){
                    filteredMessageList = messageList;
                }
                else {
                    List<Message> filteredList = new ArrayList<>();
                    for (Message row : messageList){
                        if (row.getBody().toLowerCase().contains(charString.toLowerCase()) || row.getSenderName().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    filteredMessageList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredMessageList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredMessageList = (ArrayList<Message>) results.values;
            notifyDataSetChanged();
            }
        };
    }
}
