package verisoft.homeTask.yael;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class SeleniumProxyController {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumProxyController.class);

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private RestTemplate restTemplate;

    @PostMapping("/seleniumProxySender")
    public ResponseEntity<String> seleniumProxySender(@RequestBody MultipartFile jsonFile) throws IOException {

        Boolean ifJsonIsValid = isValid(new String(jsonFile.getBytes()));
        if (ifJsonIsValid) {
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/api/seleniumProxyReceiver", jsonFile.getBytes(), String.class);
            return response;
        }
        else
            return new ResponseEntity<>("jsonFile is not valid", HttpStatusCode.valueOf(400));
    }

    @PostMapping("/seleniumProxyReceiver")
    public ResponseEntity<String> seleniumProxyReceiver(@RequestBody String jsonData) {
        try (FileWriter fileWriter = new FileWriter("logFile.txt")) {
            fileWriter.write(jsonData);
            logger.info("Data successfully written to log file.");
        } catch (IOException e) {
            logger.error("Error writing data to log file: " + e.getMessage());
        }
        return ResponseEntity.ok("Data received and logged successfully.");
    }

    private boolean isValid(String json) {
        try {
            objectMapper.readTree(json);
        } catch (JacksonException e) {
            return false;
        }
        return true;
    }

}
