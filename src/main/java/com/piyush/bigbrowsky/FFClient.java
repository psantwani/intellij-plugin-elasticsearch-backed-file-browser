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
        logger.info("FF mode: Searching " + term);

        List<Object> response = new ArrayList<>();
        try {
            String[] commands = {"/bin/sh", "-c", "ff -G -D " + term + " | head -n 15"};
            Process process=Runtime.getRuntime().exec(commands, null, basePathDir);
            process.waitFor();

            BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int counter = 0;

            while ((line=buf.readLine())!=null && counter <= 15) {
                response.add(line);
                counter++;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
}