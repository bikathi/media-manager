package npc.martin.mediamanager.service;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

@Service
public class MediaHandlingPythonCaller {
    private static final Logger logger = LoggerFactory.getLogger(MediaHandlingPythonCaller.class);

    public final void callPythonScript(String inputFilePath, String outputFilePath, String watermarkText, Boolean skipCompression) {
        try {
            // Execute the Python script
            Process process = getProcess(inputFilePath, outputFilePath, watermarkText, skipCompression);
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
        }
    }

    private Process getProcess(String inputFilePath, String outputFilePath, @Nullable String watermarkText, @Nullable Boolean skipCompression) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "python3",
            "/app/compressor.py",
            inputFilePath,
            outputFilePath
        );
        if (!Objects.isNull(watermarkText)) {
            processBuilder.command().add("--watermark_text");
            processBuilder.command().add(watermarkText);
        }

        if (Boolean.TRUE.equals(skipCompression)) {
            processBuilder.command().add("--skip-compression");
        }

        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }
}
