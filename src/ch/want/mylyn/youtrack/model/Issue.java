package ch.want.mylyn.youtrack.model;

import java.util.List;

public class Issue {

    private String id;
    private String idReadable;
    private String summary;
    private String description;
    private Long created;
    private Long updated;
    private Long resolved;
    private Project project;
    private User reporter;
    private List<IssueAttachment> attachments;
    private List<IssueComment> comments;
    private Integer commentsCount;
    private List<IssueCustomField﻿> customFields;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getIdReadable() {
        return idReadable;
    }

    public void setIdReadable(final String idReadable) {
        this.idReadable = idReadable;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(final Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(final Long updated) {
        this.updated = updated;
    }

    public Long getResolved() {
        return resolved;
    }

    public void setResolved(final Long resolved) {
        this.resolved = resolved;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(final User reporter) {
        this.reporter = reporter;
    }

    public List<IssueAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<IssueAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<IssueComment> getComments() {
        return comments;
    }

    public void setComments(final List<IssueComment> comments) {
        this.comments = comments;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(final Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<IssueCustomField﻿> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(final List<IssueCustomField﻿> customFields) {
        this.customFields = customFields;
    }
}