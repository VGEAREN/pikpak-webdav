package com.vgearen.pikpakwebdav.model.filelist;

public class FileListRequest {
    private String parent_id;

    private String thumbnail_size = "SIZE_LARGE";

    private Boolean with_audit = true;

    private String page_token;

    private Integer limit = 100;

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getThumbnail_size() {
        return thumbnail_size;
    }

    public void setThumbnail_size(String thumbnail_size) {
        this.thumbnail_size = thumbnail_size;
    }

    public Boolean getWith_audit() {
        return with_audit;
    }

    public void setWith_audit(Boolean with_audit) {
        this.with_audit = with_audit;
    }

    public String getPage_token() {
        return page_token;
    }

    public void setPage_token(String page_token) {
        this.page_token = page_token;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
