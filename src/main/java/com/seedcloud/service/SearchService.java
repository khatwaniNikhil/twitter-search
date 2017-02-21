package com.seedcloud.service;

import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.seedcloud.domain.SeedCloudTweet;

@Component
public class SearchService {
  @Autowired
  private SolrTemplate solrTemplate;

  public void postTweetsToSolr(SeedCloudTweet tweet) {
    UpdateResponse response = solrTemplate.saveBean(tweet);
  }

  private String filterQueryForField(String searchTerm, String fieldName) {
    switch (fieldName) {
      case "user":
        return fieldName + ":*" + searchTerm + "*";
      case "tweetText":
        return fieldName + ":*" + searchTerm + "*";
      case "hashTags":
        return fieldName + ":(*" + searchTerm + "*)";

      default:
        return null;
    }
  }

  public Pair<SolrDocumentList, Map<String, Integer>> filterSearchResultsByField(String searchTerm,
      String fieldName) throws SolrServerException {
    return searchTermInTweets(searchTerm, filterQueryForField(searchTerm, fieldName));
  }

  public Pair<SolrDocumentList, Map<String, Integer>> searchTermInTweets(String searchTerm,
      String filterQuery) throws SolrServerException {
    SolrQuery queryWithFacets = new SolrQuery();
    if (filterQuery != null) {
      queryWithFacets.set("q", filterQuery);
    } else {
      queryWithFacets.set("q", searchTerm);
    }
    queryWithFacets.setFacet(true);
    queryWithFacets.addFacetQuery(filterQueryForField(searchTerm, "user"));
    queryWithFacets.addFacetQuery(filterQueryForField(searchTerm, "tweetText"));
    queryWithFacets.addFacetQuery(filterQueryForField(searchTerm, "hashTags"));
    
    QueryResponse response = solrTemplate.getSolrServer().query(queryWithFacets);
    SolrDocumentList docList = response.getResults();
    
    Map<String, Integer> facetQuery = response.getFacetQuery();
    
    return new ImmutablePair<>(docList, facetQuery);
  }
}
