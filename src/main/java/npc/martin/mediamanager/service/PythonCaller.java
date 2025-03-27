package npc.martin.mediamanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@Service
public class PythonCaller {

    private static final Logger logger = LoggerFactory.getLogger(PythonCaller.class);

    public final void callPythonScript(String inputFilePath, String outputFilePath, String watermarkText) {
        logger.info("Calling python script");
        try {
//            URL scriptUrl = getClass().getClassLoader().getResource("compressor.py");

            // Execute the Python script
            ProcessBuilder processBuilder = new ProcessBuilder("python3", "/app/compressor.py", inputFilePath, outputFilePath, watermarkText);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
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
}
