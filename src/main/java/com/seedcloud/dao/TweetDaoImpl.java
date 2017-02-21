package com.seedcloud.dao;

import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seedcloud.domain.SeedCloudTweet;

public class TweetDaoImpl implements TweetDao {

  @Autowired
  private SolrTemplate solrTemplate;

  private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  @Override
  public UpdateResponse saveOrUpdate(SeedCloudTweet tweet) {
    UpdateResponse response = solrTemplate.saveBean(tweet);
    solrTemplate.commit();
    return response;
  }
}
