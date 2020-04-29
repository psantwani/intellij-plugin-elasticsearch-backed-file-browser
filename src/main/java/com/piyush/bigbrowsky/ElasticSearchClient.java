package com.piyush.bigbrowsky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.piyush.bigbrowsky.Utils.ellipsize;
import static org.elasticsearch.index.query.QueryBuilders.*;

public class ElasticSearchClient implements DataSource {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private static final ObjectMapper mapper = new ObjectMapper();
    private static RestHighLevelClient client = null;
    private static final String ELASTICSEARCH_ENDPOINT_URL = "http://localhost:9200";
    private static final String[] FILTER_EXTENSIONS = new String[]{"jar", "trace"};
    private static Project project = null;
    private static String projectName = null;

    public ElasticSearchClient(Project project){
        this.project = project;
        this.projectName = project.getName();
    }

    public void connect(){
        logger.info("Setting ElasticSearch client.");
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create(ELASTICSEARCH_ENDPOINT_URL))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                .setConnectTimeout(5000)
                .setSocketTimeout(60000)));
    }

    public void disconnect(){
        logger.info("Disconnecting from ElasticSearch.");
        try {
            client.close();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public List<Object> search(String term){
        logger.debug("Querying ElasticSearch for: " + term);
        List<Object> response = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest(projectName, projectName + "_folder");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        QueryBuilder searchQuery = queryStringQuery("*" + term + "*").field("file.filename");
        boolQueryBuilder.should().add(searchQuery);

        QueryBuilder extensionFilter = termsQuery("file.extension", FILTER_EXTENSIONS);
        boolQueryBuilder.mustNot(extensionFilter);

        String[] includeFields = new String[] {"file.filename", "path.virtual", "path.real"};
        String[] excludeFields = new String[] {};

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.sort(new FieldSortBuilder("file.last_accessed").order(SortOrder.DESC));
        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchSourceBuilder.fetchSource(includeFields, excludeFields);
        searchSourceBuilder.size(10);

        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            if(searchResponse != null){
                response.addAll(Arrays.asList(searchResponse.getHits().getHits()));
            }
        } catch (ConnectException connectException) {
            logger.error(connectException.getMessage(), connectException);
            Utils.createPopupWithMessage(project,"Failed to connect to ElasticSearch.", MessageType.ERROR);
        } catch (Throwable throwable){
            logger.error(throwable.getMessage(), throwable);
        }

        return response;
    }

    @Override
    public DataSourceSearchResponseModel transform(Object matchedObj) {
        try{
            SearchHit hit = ((SearchHit)matchedObj);
            Map<String, Object> source = hit.getSourceAsMap();
            Object file = source.get("file");
            Map fileMap = mapper.convertValue(file, Map.class);
            String fileName = fileMap.get("filename").toString();

            Object path = source.get("path");
            Map pathMap = mapper.convertValue(path, Map.class);
            String virtualPath = ellipsize(pathMap.get("virtual").toString(), 40);
            String realPath = pathMap.get("real").toString();
            return new DataSourceSearchResponseModel(fileName, virtualPath, realPath);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
