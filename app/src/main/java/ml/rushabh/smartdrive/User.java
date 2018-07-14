package ml.rushabh.smartdrive;

public class User {
    private String displayName;
    private String email;
    private String uid;
    private String photoUrl;
    private String instanceId;

    public User(){

    }

    public User(String displayName, String email, String uid, String photoUrl, String instanceId){

        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.instanceId = instanceId;

    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
