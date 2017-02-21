package com.seedcloud.converter;

import java.util.ArrayList;
import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Status;

import com.seedcloud.domain.SeedCloudTweet;

public class TweetConverter implements Converter<Status, SeedCloudTweet> {

  @Override
  public SeedCloudTweet convert(Status status) {
    List<String> hashTags = new ArrayList<String>();
    for (HashtagEntity entity : status.getHashtagEntities()) {
      hashTags.add("#" + entity.getText());
    }
    SeedCloudTweet tweet =
        new SeedCloudTweet(status.getId(), status.getText(), status.getLang(), status.getUser()
            .getName(), hashTags);
    return tweet;
  }
}
