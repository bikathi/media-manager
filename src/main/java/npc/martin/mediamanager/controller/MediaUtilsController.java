package npc.martin.mediamanager.controller;

import npc.martin.mediamanager.dto.DuplicateValuationRequest;
import npc.martin.mediamanager.service.MediaUtilsPythonCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "/utils")
public class MediaUtilsController {
    @Autowired
    private MediaUtilsPythonCaller mediaUtilsPythonCaller;

    @PostMapping(value = "/dup-media")
    public ResponseEntity<?> duplicateMedia(@RequestBody DuplicateValuationRequest duplicateValuationRequest) {
        try {
            Path pathToFolder = Paths.get("/app/data/valuation-media", duplicateValuationRequest.getFolderPath());
            mediaUtilsPythonCaller.callPythonScript(pathToFolder.toString(), duplicateValuationRequest.getNewFolderName());
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
