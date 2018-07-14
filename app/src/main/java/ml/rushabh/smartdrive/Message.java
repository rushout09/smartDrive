package ml.rushabh.smartdrive;

import java.util.Date;

public class Message {
    private String body;
    private String senderUid;
    private String senderName;
    private long timeStamp;
    private String nodeKey;

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

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
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
