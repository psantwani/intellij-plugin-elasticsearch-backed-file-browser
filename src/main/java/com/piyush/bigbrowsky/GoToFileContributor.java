package com.piyush.bigbrowsky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GoToFileContributor implements ChooseByNameContributor {

    private static final Logger logger = Logger.getInstance(GoToFileContributor.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();
    private static DataSource dataSource = null;

    public GoToFileContributor(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        return new String[]{"*"};
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<Object> matches = dataSource.search(pattern);
        if(matches == null){
            return new NavigationItem[0];
        }

        List<NavigationItem> fileItems = new ArrayList<>();
        for (Object match : matches) {
            DataSourceSearchResponseModel dataSourceSearchResponseModel = dataSource.transform(match);
            FileItem fileItem = new FileItem(project,null,
                    dataSourceSearchResponseModel.getFileName(),
                    dataSourceSearchResponseModel.getVirtualPath(),
                    dataSourceSearchResponseModel.getRealPath());
            fileItems.add(fileItem);
        }

        return fileItems.toArray(NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY);
    }
}