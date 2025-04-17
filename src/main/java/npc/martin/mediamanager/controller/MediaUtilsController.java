package npc.martin.mediamanager.controller;

import npc.martin.mediamanager.service.MediaUtilsPythonCaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "/utils")
public class MediaUtilsController {
    @Autowired
    private MediaUtilsPythonCaller mediaUtilsPythonCaller;

    @PostMapping(value = "/dup-media")
    public ResponseEntity<?> duplicateMedia(@RequestParam String folderPath, @RequestParam String newFolderName) {
        try {
            Path pathToFolder = Paths.get("/app/data/valuation-media", folderPath);
            mediaUtilsPythonCaller.callPythonScript(pathToFolder.toString(), newFolderName);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
