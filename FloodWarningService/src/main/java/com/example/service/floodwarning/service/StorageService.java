package com.example.service.floodwarning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

@Service
public class StorageService {

    private final CloudStorageAccount cloudStorageAccount;
    private final CloudBlobContainer files;

    @Autowired
    public StorageService (
            CloudStorageAccount csa) throws URISyntaxException, StorageException {
        this.cloudStorageAccount = csa;
        this.files = this.cloudStorageAccount
                .createCloudBlobClient()
                .getContainerReference("files");
    }

    public String readBlobFile(Resource blobFile) throws IOException {
        return StreamUtils.copyToString(
                blobFile.getInputStream(),
                Charset.defaultCharset()) + "\n";
    }

    public String writeBlobFile(String data, Resource blobFile) throws IOException {
        try (OutputStream os = ((WritableResource) blobFile).getOutputStream()) {
            os.write(data.getBytes());
        }
        return "File was updated.\n";
    }
}
