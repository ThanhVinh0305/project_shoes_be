package vn.shoestore.infrastructure.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

  @Value("${vn.shoe-store.minio.url}")
  private String url;

  @Value("${vn.shoe-store.minio.access-key}")
  private String accessKey;

  @Value("${vn.shoe-store.minio.secret}")
  private String secret;

  @Value("${vn.shoe-store.minio.region}")
  private String region;

  @Bean
  public AmazonS3 s3client() {
    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secret);
    AwsClientBuilder.EndpointConfiguration endpointConfiguration =
        new AwsClientBuilder.EndpointConfiguration(url, region);
    return AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withEndpointConfiguration(endpointConfiguration)
        .withPathStyleAccessEnabled(true)
        .build();
  }
}
