package com.zabojcampula.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class GooDrive implements IRemoteDrive {

    private static FileDataStoreFactory dataStoreFactory;
    private static JacksonFactory JSON_FACTORY = new JacksonFactory();
    private Drive drive;

    public GooDrive(String appName) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        dataStoreFactory = new FileDataStoreFactory(new java.io.File("C:/tmp"));
        Credential credential = authorize(httpTransport);
        drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(appName).build();
    }

    private Credential authorize(HttpTransport httpTransport) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(new FileInputStream(new java.io.File("client_id.json"))));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    @Override
    public String uploadFile(String fileName) throws IOException {

        String id = null;
        boolean isNew = false;
        try {
            id = getFileId(fileName);
        } catch (NoSuchElementException e) {
            isNew = true;
        }

        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        java.io.File filePath = new java.io.File(fileName);
        FileContent mediaContent = new FileContent("text/plain", filePath);

        if (isNew) {
            File file = drive.files().create(fileMetadata, mediaContent).setFields("id").execute();
            id = file.getId();
        } else {
            drive.files().update(id, fileMetadata, mediaContent).execute();
        }
        return id;
    }

    @Override
    public void downloadFile(String remoteFileName, String localFileName) throws IOException, NoSuchElementException {
        String id = getFileId(remoteFileName);
        try (OutputStream os = new FileOutputStream(new java.io.File(localFileName))) {
            drive.files().get(id).executeMediaAndDownloadTo(os);
        }
    }

    private String getFileId(String fileName) throws IOException {
        System.out.println("gettubg file id for " +fileName);
        List<File> allFiles = new ArrayList<>();
        Drive.Files.List request = drive.files().list();
        do {
            FileList files = request.execute();
            allFiles.addAll(files.getFiles());
            request.setPageToken(files.getNextPageToken());
        } while (request.getPageToken() != null && request.getPageToken().length() > 0);

        return allFiles.stream().filter(f -> f.getName().equals(fileName)).findAny().map(File::getId).orElseThrow();
    }
}