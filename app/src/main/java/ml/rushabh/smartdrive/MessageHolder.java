package ml.rushabh.smartdrive;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

public class MessageHolder extends RecyclerView.ViewHolder {
    TextView messageTextView;
    TextView timeTextView;

    public MessageHolder(View itemView) {
        super(itemView);

        messageTextView = (TextView) itemView.findViewById(R.id.message_tv);
        timeTextView = (TextView) itemView.findViewById(R.id.time_tv);
    }

    public void setValues(Message message) {
        messageTextView.setText(message.getBody());
        timeTextView.setText(DateFormat.format("HH:mm",
                message.getTimeStamp()));
    }
}
