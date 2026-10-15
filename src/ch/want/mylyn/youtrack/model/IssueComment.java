package ch.want.mylyn.youtrack.model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public record IssueComment(String id,
        List<JsonNode> attachments,
        User author,
        Long created,
        Boolean deleted,
        Issue issue,
        Boolean pinned,
        List<JsonNode> reactions,
        String text,
        String textPreview,
        Long updated,
        JsonNode visibility) {
}
