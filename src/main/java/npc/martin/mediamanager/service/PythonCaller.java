package npc.martin.mediamanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class PythonCaller {

    private static final Logger logger = LoggerFactory.getLogger(PythonCaller.class);

    public final void callPythonScript(String inputFilePath, String outputFilePath) {
        try {
            // Command to execute the Python script
            String command = String.format("python3 %s/src/main/resources/compressor.py %s %s", System.getProperty("user.dir"), inputFilePath, outputFilePath);

            // Execute the Python script
            Process process = Runtime.getRuntime().exec(command);

            // Reading the output from the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }

            // Wait for process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Python script executed successfully.");
            } else {
                logger.error("Python script execution failed.");
            }
        } catch (Exception e) {
            logger.error("An error occurred while executing the Python script.", e);
        }
    }
}
