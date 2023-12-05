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

		final String cosInstanceId = System.getenv("CLOUD_OBJECT_STORAGE_RESOURCE_INSTANCE_ID");
		final String cosBucketLocation = System.getenv("CLOUD_OBJECT_STORAGE_BUCKET_LOCATION");
		final String cosBucketName = System.getenv("CLOUD_OBJECT_STORAGE_BUCKET_NAME");
		final String cosEndpoint;
		if (System.getenv("CE_DOMAIN") != null) {
			// we run inside Code Engine and can use the direct endpoint
			cosEndpoint = "s3.direct." + cosBucketLocation + ".cloud-object-storage.appdomain.cloud";
		} else {
			// we run locally and must use the public endpoint
			cosEndpoint = "s3." + cosBucketLocation + ".cloud-object-storage.appdomain.cloud";
		}

        final AWSCredentials credentials = new BasicIBMOAuthCredentials(System.getenv("CLOUD_OBJECT_STORAGE_APIKEY"), cosInstanceId);
		final ClientConfiguration clientConfig = new ClientConfiguration().withRequestTimeout(-1).withTcpKeepAlive(true);

		final AmazonS3 client = AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(cosEndpoint, cosBucketLocation))
			.withPathStyleAccessEnabled(true)
			.withClientConfiguration(clientConfig)
			.build();

        final BlobStoreFileSystem cloudFileSystem = BlobStoreFileSystem.New(
			// use caching connector
			CosConnector.Caching(client)
		);

		LOGGER.info("Connecting to bucket {} in COS instance {} using endpoint {}", cosBucketName, cosInstanceId, cosEndpoint);

        embeddedStorageFoundation.setConfiguration(StorageConfiguration.Builder().setStorageFileProvider(Storage.FileProvider(cloudFileSystem.ensureDirectoryPath(cosBucketName))).createConfiguration());
    }
}
