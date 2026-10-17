package ch.want.mylyn.youtrack.model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class IssueComment {

    private String id;
    private List<JsonNode> attachments;
    private User author;
    private Long created;
    private Boolean deleted;
    private Issue issue;
    private Boolean pinned;
    private List<JsonNode> reactions;
    private String text;
    private String textPreview;
    private Long updated;
    private JsonNode visibility;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public List<JsonNode> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<JsonNode> attachments) {
        this.attachments = attachments;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(final User author) {
        this.author = author;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(final Long created) {
        this.created = created;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(final Boolean deleted) {
        this.deleted = deleted;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(final Issue issue) {
        this.issue = issue;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(final Boolean pinned) {
        this.pinned = pinned;
    }

    public List<JsonNode> getReactions() {
        return reactions;
    }

    public void setReactions(final List<JsonNode> reactions) {
        this.reactions = reactions;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getTextPreview() {
        return textPreview;
    }

    public void setTextPreview(final String textPreview) {
        this.textPreview = textPreview;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(final Long updated) {
        this.updated = updated;
    }

    public JsonNode getVisibility() {
        return visibility;
    }

    public void setVisibility(final JsonNode visibility) {
        this.visibility = visibility;
    }
}
