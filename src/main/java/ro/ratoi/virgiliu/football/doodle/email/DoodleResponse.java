package ro.ratoi.virgiliu.football.doodle.email;

/**
 * Created by vigi on 8/30/2014.
 */
class DoodleResponse {

    private String id;

    private String state;

    private String adminKey;

    private String title;

    private Boolean isByInvitationOnly;

    DoodleResponse() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsByInvitationOnly() {
        return isByInvitationOnly;
    }

    public void setIsByInvitationOnly(Boolean isByInvitationOnly) {
        this.isByInvitationOnly = isByInvitationOnly;
    }
}
