package de.saschaschwarze.quarkuseclipsestore.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import one.microstream.afs.blobstore.types.BlobStoreFileSystem;
import one.microstream.integrations.quarkus.types.config.EmbeddedStorageFoundationCustomizer;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.Storage;
import one.microstream.storage.types.StorageConfiguration;
import software.xdev.microstream.afs.ibm.cos.types.CosConnector;

@ApplicationScoped
public class FoundationCustomizer implements EmbeddedStorageFoundationCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoundationCustomizer.class);

    @Override
    public void customize(EmbeddedStorageFoundation<?> embeddedStorageFoundation) {
        LOGGER.info("customize({})", embeddedStorageFoundation);

        final AWSCredentials credentials = new BasicIBMOAuthCredentials(System.getenv("COS_API_KEY_ID"), System.getenv("COS_SERVICE_CRN"));
		final ClientConfiguration clientConfig = new ClientConfiguration().withRequestTimeout(-1).withTcpKeepAlive(true);
		
		final AmazonS3 client = AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(System.getenv("COS_ENDPOINT"), System.getenv("COS_BUCKET_LOCATION")))
			.withPathStyleAccessEnabled(true)
			.withClientConfiguration(clientConfig)
			.build();

        final BlobStoreFileSystem cloudFileSystem = BlobStoreFileSystem.New(
			// use caching connector
			CosConnector.Caching(client)
		);

        embeddedStorageFoundation.setConfiguration(StorageConfiguration.Builder().setStorageFileProvider(Storage.FileProvider(cloudFileSystem.ensureDirectoryPath(System.getenv("COS_BUCKET_NAME")))).createConfiguration());
    }
}
