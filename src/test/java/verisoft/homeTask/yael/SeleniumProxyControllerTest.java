package verisoft.homeTask.yael;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SeleniumProxyControllerTest {

    private static class CustomJsonProcessingException extends JsonProcessingException {
        public CustomJsonProcessingException(String msg) {
            super(msg);
        }
    }

    @InjectMocks
    private SeleniumProxyController seleniumProxyController;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSeleniumProxySenderValidJson() throws IOException {
        MultipartFile jsonFile = mock(MultipartFile.class);
        when(jsonFile.getBytes()).thenReturn("{\"key\": \"value\"}".getBytes()); // valid JSON

        when(objectMapper.readTree(anyString())).thenReturn(mock(JsonNode.class));
        ResponseEntity<String> mockResponse = new ResponseEntity<>("Data received and logged successfully", HttpStatus.OK);
        when(restTemplate.postForEntity("http://localhost:8080/api/seleniumProxyReceiver", jsonFile.getBytes(), String.class)).thenReturn(mockResponse);
        ResponseEntity<String> response = seleniumProxyController.seleniumProxySender(jsonFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data received and logged successfully", response.getBody());
    }

    @Test
    public void testSeleniumProxySenderInValidJson() throws IOException {
        MultipartFile jsonFile = mock(MultipartFile.class);
        when(jsonFile.getBytes()).thenReturn("{\"key\": \"value\"".getBytes()); // InValid JSON

        doThrow(new CustomJsonProcessingException("Invalid JSON")).when(objectMapper).readTree(anyString());
        ResponseEntity<String> mockResponse = new ResponseEntity<>("jsonFile is not valid", HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity("http://localhost:8080/api/seleniumProxyReceiver", jsonFile.getBytes(), String.class)).thenReturn(mockResponse);
        ResponseEntity<String> response = seleniumProxyController.seleniumProxySender(jsonFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("jsonFile is not valid", response.getBody());
    }

}