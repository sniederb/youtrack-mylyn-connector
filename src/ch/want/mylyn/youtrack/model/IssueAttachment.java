package ch.want.mylyn.youtrack.model;

public class IssueAttachment {

    private String id;
    private String name;
    private User author;
    private Long created;
    private Long updated;
    private Long size;
    private String extension;
    private String charset;
    private String mimeType;
    private String metaData;
    private Boolean draft;
    private Boolean removed;
    private String base64Content;
    private String url;
    private String thumbnailURL;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(final Long updated) {
        this.updated = updated;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(final Long size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(final String extension) {
        this.extension = extension;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(final String metaData) {
        this.metaData = metaData;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(final Boolean draft) {
        this.draft = draft;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public void setRemoved(final Boolean removed) {
        this.removed = removed;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(final String base64Content) {
        this.base64Content = base64Content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(final String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
