package com.piyush.bigbrowsky;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.ConnectException;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class ElasticSearchClient {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private static RestHighLevelClient client = null;
    private static final String ELASTICSEARCH_ENDPOINT_URL = "http://localhost:9200";
    private static final String[] FILTER_EXTENSIONS = new String[]{"class", "jar"};

    public static void connect(){
        logger.info("Setting ElasticSearch client.");
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create(ELASTICSEARCH_ENDPOINT_URL)).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(
                    RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder
                        .setConnectTimeout(5000)
                        .setSocketTimeout(60000);
            }
        }));
    }

    public static void disconnect(){
        logger.info("Disconnecting from ElasticSearch.");
        try {
            client.close();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static SearchResponse search(Project project, String term){
        logger.debug("Querying ElasticSearch for: " + term);

        SearchRequest searchRequest = new SearchRequest("fievel", "fievel_folder");
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
            return client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ConnectException connectException) {
            logger.error(connectException.getMessage(), connectException);
            Utils.createPopupWithMessage(project,"Failed to connect to ElasticSearch.", MessageType.ERROR);
        } catch (Throwable throwable){
            logger.error(throwable.getMessage(), throwable);
        }

        return null;
    }
}
