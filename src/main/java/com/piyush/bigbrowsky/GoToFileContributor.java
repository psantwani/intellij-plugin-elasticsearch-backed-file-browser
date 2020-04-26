package com.piyush.bigbrowsky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.*;

public class GoToFileContributor implements ChooseByNameContributor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private final ObjectMapper mapper = new ObjectMapper();

    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        return new String[]{"*"};
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        Map<String, Object> source;
        Map fileMap, pathMap;
        String fileName, virtualPath, realPath;

        SearchResponse searchResponse = ElasticSearchClient.search(project, pattern);
        if(searchResponse == null){
            return new NavigationItem[0];
        }

        SearchHit[] hits = searchResponse.getHits().getHits();
        List<NavigationItem> fileItems = new ArrayList<>();

        for (SearchHit hit : hits) {
            source = hit.getSourceAsMap();
            try {
                Object file = source.get("file");
                fileMap = mapper.convertValue(file, Map.class);
                fileName = fileMap.get("filename").toString();

                Object path = source.get("path");
                pathMap = mapper.convertValue(path, Map.class);
                virtualPath = ellipsize(pathMap.get("virtual").toString(), 40);
                realPath = pathMap.get("real").toString();

                /* Moved to FileItem.
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(realPath);
                if(virtualFile != null){
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                    FileItem fileItem = new FileItem(psiFile, fileName, virtualPath, realPath);
                    fileItems.add(fileItem);
                }
                 */

                FileItem fileItem = new FileItem(project,null, fileName, virtualPath, realPath);
                fileItems.add(fileItem);

            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }

        return fileItems.toArray(NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY);
    }

    public static String ellipsize(String input, int maxLength) {
        if (input == null || input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength-3) + "...";
    }
}