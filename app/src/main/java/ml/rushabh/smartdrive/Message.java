package ml.rushabh.smartdrive;

import java.util.Date;

public class Message {
    private String body;
    private String senderUid;
    private String senderName;
    private long timeStamp;

    public Message(){

    }

    public Message(String body,String senderUid, String senderName){
        this.body = body;
        this.senderUid = senderUid;
        this.senderName = senderName;
        this.timeStamp = new Date().getTime();
    }

    public String getBody() {
        return body;
    }


    public String getSenderUid() {
        return senderUid;
    }

    public String getSenderName() {
        return senderName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
}
