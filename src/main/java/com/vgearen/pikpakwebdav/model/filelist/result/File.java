
package com.vgearen.pikpakwebdav.model.filelist.result;

import java.util.Date;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class File {

    @Expose
    private List<Object> apps;
    @Expose
    private Object audit;
    @SerializedName("created_time")
    private Date created_time;
    @SerializedName("delete_time")
    private Date delete_time;
    @SerializedName("file_extension")
    private String file_extension;
    @SerializedName("folder_type")
    private String folder_type;
    @Expose
    private String hash;
    @SerializedName("icon_link")
    private String icon_link;
    @Expose
    private String id;
    @Expose
    private String kind;
    @Expose
    private Links links;
    @SerializedName("md5_checksum")
    private String md5_checksum;
    @Expose
    private List<Object> medias;
    @SerializedName("mime_type")
    private String mime_type;
    @SerializedName("modified_time")
    private Date modified_time;
    @Expose
    private String name;
    @SerializedName("original_file_index")
    private Long original_file_index;
    @SerializedName("original_url")
    private String original_url;
    @Expose
    private Params params;
    @SerializedName("parent_id")
    private String parent_id;
    @Expose
    private String phase;
    @Expose
    private String revision;
    @Expose
    private Long size;
    @Expose
    private String space;
    @Expose
    private Boolean starred;
    @SerializedName("thumbnail_link")
    private String thumbnail_link;
    @Expose
    private Boolean trashed;
    @SerializedName("user_id")
    private String user_id;
    @SerializedName("web_content_link")
    private String web_content_link;
    @Expose
    private Boolean writable;

    public List<Object> getApps() {
        return apps;
    }

    public void setApps(List<Object> apps) {
        this.apps = apps;
    }

    public Object getAudit() {
        return audit;
    }

    public void setAudit(Object audit) {
        this.audit = audit;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public Date getDelete_time() {
        return delete_time;
    }

    public void setDelete_time(Date delete_time) {
        this.delete_time = delete_time;
    }

    public String getFile_extension() {
        return file_extension;
    }

    public void setFile_extension(String file_extension) {
        this.file_extension = file_extension;
    }

    public String getFolder_type() {
        return folder_type;
    }

    public void setFolder_type(String folder_type) {
        this.folder_type = folder_type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getIcon_link() {
        return icon_link;
    }

    public void setIcon_link(String icon_link) {
        this.icon_link = icon_link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getMd5_checksum() {
        return md5_checksum;
    }

    public void setMd5_checksum(String md5_checksum) {
        this.md5_checksum = md5_checksum;
    }

    public List<Object> getMedias() {
        return medias;
    }

    public void setMedias(List<Object> medias) {
        this.medias = medias;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public Date getModified_time() {
        return modified_time;
    }

    public void setModified_time(Date modified_time) {
        this.modified_time = modified_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOriginal_file_index() {
        return original_file_index;
    }

    public void setOriginal_file_index(Long original_file_index) {
        this.original_file_index = original_file_index;
    }

    public String getOriginal_url() {
        return original_url;
    }

    public void setOriginal_url(String original_url) {
        this.original_url = original_url;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public Boolean getStarred() {
        return starred;
    }

    public void setStarred(Boolean starred) {
        this.starred = starred;
    }

    public String getThumbnail_link() {
        return thumbnail_link;
    }

    public void setThumbnail_link(String thumbnail_link) {
        this.thumbnail_link = thumbnail_link;
    }

    public Boolean getTrashed() {
        return trashed;
    }

    public void setTrashed(Boolean trashed) {
        this.trashed = trashed;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWeb_content_link() {
        return web_content_link;
    }

    public void setWeb_content_link(String web_content_link) {
        this.web_content_link = web_content_link;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }
}
