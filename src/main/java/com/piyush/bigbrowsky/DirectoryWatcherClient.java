package com.piyush.bigbrowsky;

import com.intellij.openapi.project.Project;
import com.piyush.bigbrowsky.DirectoryWatcher.DirectoryWatcherClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DirectoryWatcherClient implements DataSource {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private static DirectoryWatcherClass directoryWatcherClient = null;
    private static Project project = null;
    private static String projectName = null, basePath = null;

    public DirectoryWatcherClient(){}

    public DirectoryWatcherClient(Project project){
        this.project = project;
        this.projectName = project.getName();
        this.basePath = project.getBasePath();
    }

    public void connect(){
        directoryWatcherClient = new DirectoryWatcherClass.Builder()
                .addDirectories(basePath)
                .setPreExistingAsCreated(checkIfPreExistingCaptured())
                .build((event, path) -> {
                    switch (event) {
                        case ENTRY_CREATE:
                            System.out.println(path + " created.");
                            break;

                        case ENTRY_MODIFY:
                            System.out.println(path + " modify.");
                            break;

                        case ENTRY_DELETE:
                            System.out.println(path + " deleted.");
                            break;
                    }
                });

        try {
            directoryWatcherClient.start();
            TimeUnit.SECONDS.sleep(300);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static boolean checkIfPreExistingCaptured(){
        if(basePath == null){
            return false;
        }

        return true;
    }

    public void disconnect(){
        directoryWatcherClient.stop();
    }

    @Override
    public List<Object> search(String term) {
        return null;
    }

    @Override
    public DataSourceSearchResponseModel transform(Object matchedObj) {
        return null;
    }
}
