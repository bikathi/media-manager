package npc.martin.mediamanager.dto;

public class DuplicateValuationRequest {
    private String folderPath;
    private String newFolderName;

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getNewFolderName() {
        return newFolderName;
    }

    public void setNewFolderName(String newFolderName) {
        this.newFolderName = newFolderName;
    }

    public DuplicateValuationRequest(String folderPath, String newFolderName) {
        this.folderPath = folderPath;
        this.newFolderName = newFolderName;
    }

    public DuplicateValuationRequest() {
    }
}
