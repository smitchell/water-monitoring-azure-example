package com.example.client.monitor.service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class StorageService {

    private final String containerName = "images";
    private final CloudStorageAccount cloudStorageAccount;
    private final CloudBlobContainer files;

    @Autowired
    public StorageService(CloudStorageAccount csa) {
        this.cloudStorageAccount = csa;
        files = getOrCreateContainerReference(containerName);
    }
    @PostConstruct
    public void postConstruct() {
        Assert.notNull(cloudStorageAccount, "cloudStorageAccount must not be null");
        Assert.notNull(files, "files must not be null");
    }

    private CloudBlobContainer getOrCreateContainerReference(String containerName) {
        CloudBlobContainer container = null;
        try {
            container = this.cloudStorageAccount
                    .createCloudBlobClient()
                    .getContainerReference(containerName);
            BlobRequestOptions blobRequestOptions = new BlobRequestOptions();
            container.createIfNotExists(BlobContainerPublicAccessType.BLOB, new BlobRequestOptions(), new OperationContext());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return container;
    }

    public String writeBlobFile(String stationId, byte[] bytes, String extension) throws Exception {
        CloudBlockBlob blockBlobReference = files.getBlockBlobReference(generateBlobName(stationId, new Date(), extension));
        try (InputStream in = new ByteArrayInputStream(bytes)) {
            blockBlobReference.upload(in, bytes.length);
            return blockBlobReference.getStorageUri().getPrimaryUri().toString();
        }
    }

    private String generateBlobName(String stationId, Date date, String extension) {
        return stationId
                .concat("/")
                .concat(UUID.randomUUID().toString())
                .concat(".").concat(extension);
    }

}
