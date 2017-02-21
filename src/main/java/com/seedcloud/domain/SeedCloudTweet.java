package com.seedcloud.domain;

import java.io.Serializable;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.data.solr.repository.Indexed;


@SolrDocument(solrCoreName = "tweets")
public class SeedCloudTweet implements Serializable {
  @Override
  public String toString() {
    return "SeedCloudTweet [id=" + id + ", text=" + tweetText + ", lang=" + lang + ", user=" + user
        + ", hashTags=" + hashTags + "]";
  }

  private @Id @Field String id;
  private @Indexed @Field String tweetText;
  private @Indexed @Field String lang;
  private @Indexed @Field String user;
  private @Indexed @Field List<String> hashTags;

  public SeedCloudTweet() {

  }

  public SeedCloudTweet(long id, String text, String lang, String user, List<String> hashTags) {
    super();
    this.id = String.valueOf(id);
    this.tweetText = text;
    this.lang = lang;
    this.user = user;
    this.hashTags = hashTags;
  }

  public String getId() {
    return this.id;
  }

  public String getText() {
    return this.tweetText;
  }

  public String getLang() {
    return lang;
  }

  public String getUser() {
    return user;
  }

  public List<String> getHashTags() {
    return hashTags;
  }

}
