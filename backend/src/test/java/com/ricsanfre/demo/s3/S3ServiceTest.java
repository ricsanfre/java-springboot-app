package com.ricsanfre.demo.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;
    private S3Service underTest;
    @BeforeEach
    void setUp() {
        underTest = new S3Service(s3Client);
    }

    @Test
    void canPutObject() throws IOException {
        // Given
        String bucket = "customer";
        String key = "foo";
        byte[] file = "Hello World!".getBytes();

        // When
        underTest.putObject(bucket, key, file);

        // Then
        ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor =
                ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> putObjectRequestBodyCaptor =
                ArgumentCaptor.forClass(RequestBody.class);
        Mockito.verify(s3Client).putObject(
                putObjectRequestArgumentCaptor.capture(),
                putObjectRequestBodyCaptor.capture());

        PutObjectRequest putObjectRequestArgumentValue = putObjectRequestArgumentCaptor.getValue();
        RequestBody putObjectRequestBodyValue = putObjectRequestBodyCaptor.getValue();
        assertThat(putObjectRequestArgumentValue.bucket()).isEqualTo(bucket);
        assertThat(putObjectRequestArgumentValue.key()).isEqualTo(key);
        assertThat(putObjectRequestBodyValue.contentStreamProvider().newStream().readAllBytes())
                .isEqualTo(RequestBody.fromBytes(file).contentStreamProvider().newStream().readAllBytes());



    }

    @Test
    void canGetObject() throws IOException {
        // Given
        String bucket = "customer";
        String key = "foo";
        byte[] file = "Hello World!".getBytes();

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> res = Mockito.mock(ResponseBytes.class);
        Mockito.when(res.asByteArray()).thenReturn(file);

        Mockito.when(s3Client.getObjectAsBytes(objectRequest)).thenReturn(res);

        // When
        byte[] actual = underTest.getObject(bucket, key);

        // Then
        assertThat(actual).isEqualTo(file);

    }

}