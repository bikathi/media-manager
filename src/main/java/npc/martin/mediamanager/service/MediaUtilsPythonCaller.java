package npc.martin.mediamanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class MediaUtilsPythonCaller {
    private static final Logger logger = LoggerFactory.getLogger(MediaUtilsPythonCaller.class);

    public final void callPythonScript(String existingFolderPath, String newFolderName) throws Exception {
        try {
            // Execute the Python script
            Process process = getProcess(existingFolderPath, newFolderName);
            try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = processOutputReader.readLine()) != null) {
                    logger.info(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Python script executed successfully");
            } else {
                logger.error("Python script execution failed with exit code {}", exitCode);
            }
        } catch (Exception e) {
            logger.error("An error occurred while executing the Python script.", e);
            throw new RuntimeException("Failed to duplicate folders");
        }
    }

    private Process getProcess(String existingFolderPath, String newFolderName) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "python3",
            "/app/media-duplicator.py",
            "--folder-path",
            existingFolderPath,
            "--folder-name",
            newFolderName
        );

        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }
}
