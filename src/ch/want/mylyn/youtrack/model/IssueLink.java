package ch.want.mylyn.youtrack.model;

import java.util.List;

public class IssueLink {

    private String id;
    private String direction;
    private IssueLinkType﻿ linkType;
    private List<Issue> issues;
    private List<Issue> trimmedIssues;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(final String direction) {
        this.direction = direction;
    }

    public IssueLinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(final IssueLinkType linkType) {
        this.linkType = linkType;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(final List<Issue> issues) {
        this.issues = issues;
    }

    public List<Issue> getTrimmedIssues() {
        return trimmedIssues;
    }

    public void setTrimmedIssues(final List<Issue> trimmedIssues) {
        this.trimmedIssues = trimmedIssues;
    }
}
