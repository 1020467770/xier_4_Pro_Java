package cn.sqh.Server.domain;
import java.sql.Timestamp;
import java.util.Date;

public class BasicFile {
    static public int FILETYPE_FOLDER = 0;
    static public int FILETYPE_PICTURE = 1;

    private int idFile;
    private String fileName;
    private int creatorId;
    private Timestamp createTime;
    private String fileRealPath;
    private long capacity;
    private int parentId;
    private int fileType;



    public BasicFile(String fileName, String fileRealPath, long capacity, int parentId, int fileType) {
        this.fileName = fileName;
        this.fileRealPath = fileRealPath;
        this.capacity = capacity;
        this.parentId = parentId;
        this.fileType = fileType;
    }

    public BasicFile() {
    }

    public int getIdFile() {
        return idFile;
    }

    public void setIdFile(int idFile) {
        this.idFile = idFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getFileRealPath() {
        return fileRealPath;
    }

    public void setFileRealPath(String fileRealPath) {
        this.fileRealPath = fileRealPath;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
}
