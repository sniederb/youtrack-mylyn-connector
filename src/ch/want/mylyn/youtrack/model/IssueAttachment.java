package ch.want.mylyn.youtrack.model;

public record IssueAttachment(
        String id,
        String name,
        User author,
        Long created,
        Long updated,
        Long size,
        String extension,
        String charset,
        String mimeType,
        String metaData,
        Boolean draft,
        Boolean removed,
        String base64Content,
        String url,
        String thumbnailURL) {
}
