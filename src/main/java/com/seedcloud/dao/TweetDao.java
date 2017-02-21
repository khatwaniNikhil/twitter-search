package com.seedcloud.dao;

import org.apache.solr.client.solrj.response.UpdateResponse;

import com.seedcloud.domain.SeedCloudTweet;

public interface TweetDao {
  UpdateResponse saveOrUpdate(SeedCloudTweet tweet);
}
