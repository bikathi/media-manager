package npc.martin.mediamanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

@Service
public class PythonCaller {
    private static final Logger logger = LoggerFactory.getLogger(PythonCaller.class);

    public final void callPythonScript(String inputFilePath, String outputFilePath, String watermarkText) {
        try {
            // Execute the Python script
            Process process = getProcess(inputFilePath, outputFilePath, watermarkText);
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

    private Process getProcess(String inputFilePath, String outputFilePath, String watermarkText) throws IOException {
        ProcessBuilder processBuilder = Objects.isNull(watermarkText) || !StringUtils.hasLength(watermarkText) ? new ProcessBuilder(
            "python3",
            "/app/compressor.py",
            inputFilePath,
            outputFilePath
        ) : new ProcessBuilder(
            "python3",
            "/app/compressor.py",
            inputFilePath,
            outputFilePath,
            "--watermark_text",
            watermarkText
        );

        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }
}
