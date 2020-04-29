package com.piyush.bigbrowsky;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import org.buildobjects.process.ProcBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class FFClient implements DataSource {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private static Project project = null;
    private static String basePath = null;

    public FFClient(Project project){
        this.project = project;
        this.basePath = project.getBasePath();
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
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(basePath));

        String[] commands = {"/bin/sh", "-c", "ff -G -D " + term + " | head -n 15"};
        builder.command(commands);
        List<Object> response = new ArrayList<>();
        try {
            Process process = builder.start();
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
        return null;
    }
}