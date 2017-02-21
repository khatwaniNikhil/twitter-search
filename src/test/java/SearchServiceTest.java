import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.seedcloud.dao.TweetDao;
import com.seedcloud.domain.SeedCloudTweet;
import com.seedcloud.service.SearchService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:/Users/nikhil/Documents/workspace/twitter-search/src/main/resources/beans_test.xml")
public class SearchServiceTest {

  @Autowired
  private SearchService searchService;

  @Autowired
  private TweetDao searchResultDao;

  @Autowired
  private SolrTemplate solrTemplate;

  @Before
  public void setup() throws SolrServerException, IOException {
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.set("q", "*:*");
    solrTemplate.getSolrServer().deleteByQuery(solrQuery.getQuery());
    List<SeedCloudTweet> tweets = new ArrayList<SeedCloudTweet>();
    tweets.add(new SeedCloudTweet(1, "text1 text2 user1 user2", "en", "user1", Arrays.asList(
        "tag1", "tag2", "text1", "text2")));
    tweets.add(new SeedCloudTweet(2, "text1 text2 user1 user2", "en", "user2", Arrays.asList(
        "tag1", "tag2", "text1", "text2", "tag3")));
    tweets.add(new SeedCloudTweet(3, "text1 text2 user1 user2 user3", "en", "user3", Arrays.asList(
        "tag1", "tag2", "text2", "tag3")));
    for (SeedCloudTweet tweet : tweets) {
      searchResultDao.saveOrUpdate(tweet);
    }
  }

  @After
  public void tearDown() throws SolrServerException, IOException {
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.set("q", "*:*");
    solrTemplate.getSolrServer().deleteByQuery(solrQuery.getQuery());
  }

  @Test
  public void testQueryingTweetsFromSolr() throws SolrServerException {
    String term = "text1";
    Pair<SolrDocumentList, Map<String, Integer>> resultPair =
        searchService.searchTermInTweets(term, null);
    SolrDocumentList docList = resultPair.getLeft();
    List<String> resultDocIds = Arrays.asList("1", "2", "3");
    Assert.assertEquals(docList.getNumFound(), 3);
    for (SolrDocument doc : docList) {
      Assert.assertTrue(resultDocIds.contains(doc.getFieldValue("id")));
    }
  }

  @Test
  public void testFilterTweetsByUser() throws SolrServerException {
    String term = "text1";
    Pair<SolrDocumentList, Map<String, Integer>> resultPair =
        searchService.filterSearchResultsByField(term, "user");
    SolrDocumentList docList = resultPair.getLeft();
    Assert.assertEquals(docList.size(), 0);
  }

  @Test
  public void testFilterTweetsByTweetText() throws SolrServerException {
    String term = "text1";
    Pair<SolrDocumentList, Map<String, Integer>> resultPair =
        searchService.filterSearchResultsByField(term, "tweetText");
    SolrDocumentList docList = resultPair.getLeft();
    List<String> resultDocIds = Arrays.asList("1", "2", "3");
    Assert.assertEquals(docList.getNumFound(), 3);
    for (SolrDocument doc : docList) {
      Assert.assertTrue(resultDocIds.contains(doc.getFieldValue("id")));
    }
  }

  @Test
  public void testFilterTweetsByHashTags() throws SolrServerException {
    String term = "text1";
    Pair<SolrDocumentList, Map<String, Integer>> resultPair =
        searchService.filterSearchResultsByField(term, "hashTags");
    SolrDocumentList docList = resultPair.getLeft();
    List<String> resultDocIds = Arrays.asList("1", "2");
    Assert.assertEquals(docList.getNumFound(), 2);
    for (SolrDocument doc : docList) {
      Assert.assertTrue(resultDocIds.contains(doc.getFieldValue("id")));
    }
  }
}
