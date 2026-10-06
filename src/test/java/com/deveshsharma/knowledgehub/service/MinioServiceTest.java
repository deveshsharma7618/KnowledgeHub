package com.deveshsharma.knowledgehub.service;

import com.deveshsharma.knowledgehub.service.impl.MinioService;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MinioServiceTest {

    @Test
    void uploadsFileAndCreatesMissingBucket() throws Exception {
        MinioClient client = mock(MinioClient.class);
        when(client.bucketExists(any())).thenReturn(false);
        MinioService service = new MinioService(client, "documents");
        MockMultipartFile file = new MockMultipartFile("file", "notes.txt", "text/plain", "content".getBytes());

        service.uploadFile(file);

        verify(client).makeBucket(any());
        verify(client).putObject(any());
    }
}
