package com.seedcloud.service;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.seedcloud.converter.TweetConverter;
import com.seedcloud.dao.TweetDao;


public class TwitterDataService {

  private Query query;

  @Autowired
  @Qualifier("twitter")
  private Twitter twitter;

  @Autowired
  private TweetDao searchResultDao;

  @Autowired
  private TweetConverter twitterConverter;

  public TwitterDataService() {}

  private void prepareQuery() throws TwitterException {
    // TODO read from config
    query = new Query("test1");
    query.setLang("en");
    query.setCount(100);
    System.out.println("token type: " + twitter.getOAuth2Token().getTokenType());
  }

  @Scheduled(initialDelay = 3000, fixedDelay = 3000)
  public void fetchDataAndStoreHourly() throws TwitterException, SolrServerException {
    QueryResult result;
    Query nextQuery = query;
    List<Status> tweets = null;
    int lastFetchResultTweetsCount = 0;
    try {
      do {
        System.out.println("Fetching tweets on twitter.com: ");
        System.out.println("query sinceId:" + query.getSinceId());
        System.out.println("query maxId:" + query.getMaxId());
        result = twitter.search(nextQuery);
        tweets = result.getTweets();
        lastFetchResultTweetsCount = tweets.size();
        System.out.println("Found tweets: " + tweets.size());
        for (Status tweet : tweets) {
          System.out.println("tweet id: " + tweet.getId());
          System.out.println("tweet createTime: " + tweet.getCreatedAt());
        }
        processTweets(tweets);
        System.out.println("result sinceId: " + result.getSinceId());
        System.out.println("result maxId: " + result.getMaxId());
      } while ((nextQuery = result.nextQuery()) != null);
      if (lastFetchResultTweetsCount > 0) {
        query.setSinceId(result.getMaxId());
      }
    } catch (TwitterException te) {
      te.printStackTrace();
      System.out.println("Failed to search tweets: " + te.getMessage());
    }
  }

  private void processTweets(List<Status> tweets) {
    for (Status tweet : tweets)
      searchResultDao.saveOrUpdate(twitterConverter.convert(tweet));
  }

  public TweetDao getDao() {
    return searchResultDao;
  }
}
