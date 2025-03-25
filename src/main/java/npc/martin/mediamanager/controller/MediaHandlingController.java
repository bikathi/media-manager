package npc.martin.mediamanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

@RestController
@RequestMapping(value = "/media")
public class MediaHandlingController {
    @Value("${app.base-url}")
    private String appBaseUrl;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
        @RequestParam String filePath,
        @RequestParam String basePath,
        @RequestPart MultipartFile file
    ) throws IOException {
        String[] pathTokens = filePath.split("/");

        // Construct the absolute path of the user's downloads folder
        // Create directories if they don't exist
        Path currentPath = Paths.get("/app/data", basePath); // /app/data is the mapping in the Docker container
        for (String token : pathTokens) {
            currentPath = currentPath.resolve(token);
            if (!Files.exists(currentPath)) {
                Files.createDirectory(currentPath);
            }
        }

        // Place the file in the created folder
        Path fp = currentPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), fp, StandardCopyOption.REPLACE_EXISTING);

        // Return the URL of the file
        String fileUrl = appBaseUrl + "/media/download" + fp.toString().replace("/app/data", "");
        return ResponseEntity.ok().body(fileUrl);
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.DELETE }, value = "/download/{*filePath}")
    public ResponseEntity<?> downloadFile(@PathVariable String filePath, HttpServletRequest request) {
        Path fp = Paths.get("/app/data", filePath);

        // check the method if it is DELETE call the method to delete the file
        if (request.getMethod().equals(RequestMethod.DELETE.name())) {
            try {
                this.deleteFile(fp);
                return ResponseEntity.status(HttpStatus.OK).body("File deleted successfully.");
            } catch (NoSuchFileException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting the file.");
            }

        }

        if (Files.exists(fp) && request.getMethod().equals(RequestMethod.GET.name())) {
            try {
                byte[] fileBytes = Files.readAllBytes(fp);
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fp.getFileName() + "\"")
                .body(fileBytes);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading file.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
        }
    }

    private void deleteFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }
}
