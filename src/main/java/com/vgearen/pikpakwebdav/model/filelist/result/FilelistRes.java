
package com.vgearen.pikpakwebdav.model.filelist.result;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class FilelistRes {

    @Expose
    private List<File> files;
    @Expose
    private String kind;
    @SerializedName("next_page_token")
    private String next_page_token;
    @Expose
    private String version;
    @SerializedName("version_outdated")
    private Boolean version_outdated;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getVersion_outdated() {
        return version_outdated;
    }

    public void setVersion_outdated(Boolean version_outdated) {
        this.version_outdated = version_outdated;
    }
}
