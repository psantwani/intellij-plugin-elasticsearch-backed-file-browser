package com.piyush.bigbrowsky;

import com.intellij.openapi.project.Project;
import org.elasticsearch.action.search.SearchResponse;

import java.util.List;

public interface DataSource {
    public void connect();
    public void disconnect();
    public List<Object> search(String term);
    public DataSourceSearchResponseModel transform(Object matchedObj);
}