package com.piyush.bigbrowsky;

public class DataSourceSearchResponseModel {

    public DataSourceSearchResponseModel(String fileName, String virtualPath, String realPath){
        this.fileName = fileName;
        this.virtualPath = virtualPath;
        this.realPath = realPath;
    }

    private String fileName;

    private String virtualPath;

    private String realPath;

    public String getFileName() {
        return fileName;
    }

    public String getVirtualPath() {
        return virtualPath;
    }

    public String getRealPath() {
        return realPath;
    }
}