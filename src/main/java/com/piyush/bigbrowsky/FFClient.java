package com.piyush.bigbrowsky;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FFClient implements DataSource {

    private static Logger logger = Logger.getInstance(FFClient.class.getName());

    private static Project project = null;
    private static String basePath = null;
    private static File basePathDir = null;

    public FFClient(Project project){
        this.project = project;
        this.basePath = project.getBasePath();
        this.basePathDir = new File(basePath);
    }

    @Override
    public void connect() {
        if(!System.getProperty("os.name").toLowerCase().equals("mac os x")){
            Utils.createPopupWithMessage(project,"Non Elasticsearch mode is supported on MAC OSX only.", MessageType.ERROR);
        }
    }

    @Override
    public void disconnect() {
        // Do nothing.
    }

    @Override
    public List<Object> search(String term) {
        List<Object> response = new ArrayList<>();

        try {
            // String[] commands = {"/bin/sh", "-c", "ff -G -D " + term};
            String[] commands = {"ls"};
            ProcessBuilder pb = new ProcessBuilder()
                    .command(commands)
                    .directory(basePathDir);

            Process p = null;
            p = pb.start();

            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
            Thread thread = new Thread(outputGobbler);
            thread.start();

            int exit = p.waitFor();
            thread.join();
            response = outputGobbler.getValue();

            if (exit != 0) {
                logger.error(String.format("runCommand returned %d", exit));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return response;
    }

    @Override
    public DataSourceSearchResponseModel transform(Object matchedObj) {
        String line = (String)matchedObj;
        String realPath = basePath + line.substring(1);
        String virtualPath = Utils.ellipsize(line.substring(2), 40);
        String fileName = Paths.get(realPath).getFileName().toString();
        return new DataSourceSearchResponseModel(fileName, virtualPath, realPath);
    }

    public static class StreamGobbler extends Thread {

        private final InputStream is;
        private volatile List<Object> lines = new ArrayList<>();

        public StreamGobbler(InputStream is, String type) {
            this.is = is;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public List<Object> getValue(){
            return lines;
        }
    }
}