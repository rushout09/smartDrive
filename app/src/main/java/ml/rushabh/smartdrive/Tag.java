package ml.rushabh.smartdrive;

public class Tag {
    private String tagName;
    private Message message;

    public Tag(){

    }
    public Tag(String tagName,Message message){
        this.tagName = tagName;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
