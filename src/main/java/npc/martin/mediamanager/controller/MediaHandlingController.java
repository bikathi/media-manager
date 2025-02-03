package npc.martin.mediamanager.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/media")
public class MediaHandlingController {
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam String filePath, @RequestPart MultipartFile file) {
        String[] pathTokens = filePath.split("/");
        return null;
    }

    @GetMapping(value = "/download")
    public ResponseEntity<?> downloadFile() {
        return null;
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<?> deleteFile() {
        return null;
    }
}
