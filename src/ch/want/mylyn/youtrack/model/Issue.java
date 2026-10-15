package ch.want.mylyn.youtrack.model;

import java.util.List;

public record Issue(
        String id,
        String idReadable,
        String summary,
        String description,
        Long created,
        Long updated,
        Long resolved,
        Project project,
        User reporter,
        List<IssueAttachment> attachments,
        List<IssueComment> comments,
        Integer commentsCount,
        List<CustomField> customFields) {
}