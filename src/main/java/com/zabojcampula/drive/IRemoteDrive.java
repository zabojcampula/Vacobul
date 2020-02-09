package com.zabojcampula.drive;

import java.io.IOException;

public interface IRemoteDrive {

    String uploadFile(String fileName) throws IOException;
    void downloadFile(String id, String localFileName) throws IOException;

}
