package com.goktugoner;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.*;
import twitter4j.v1.Paging;

import java.util.*;

public class TwitterFunctions {
    private static Twitter twitterLogin(){
        String oAutConsumerKey = "";
        String oAuthConsumerSecret = "";
        String oAuthAccessToken = "";
        String oAuthAccessTokenSecret = "";
        return Twitter.newBuilder().oAuthConsumer(oAutConsumerKey, oAuthConsumerSecret)
                .oAuthAccessToken(oAuthAccessToken, oAuthAccessTokenSecret).tweetModeExtended(true)
                .build();
    }

    public List<String> findUser(String username){
        //returns a list for posting an embed on discord
        List<Object> profileList;
        List<String> resultList = new ArrayList<>();
        try{
            twitter4j.Twitter twitter = twitterLogin();
            UsersResources users = twitter.v1().users();
            User user = users.showUser(username);
            System.out.println(user);
            profileList = Arrays.asList(user.getScreenName(), user.getName(), user.getDescription(),
                    user.getURL(), user.getEmail(), user.getLocation(), user.getProfileImageURLHttps(),
                    user.getBiggerProfileImageURLHttps(), "https://twitter.com/" + user.getScreenName(), user.getFollowersCount());
            for(Object obj : profileList){
                if(obj != null){
                    resultList.add(obj.toString());
                }else{
                    resultList.add("N/A");
                }
            }
        } catch (TwitterException te){
            return null;
        }
        return resultList;
    }

    public String searchTweet(String tweetQuery){
        if (tweetQuery.length() < 1 || tweetQuery.startsWith(" ")) {
            return "Query length is shorter than the minimum.";
        }
        try{
            if(MongoDB.collectionExists("QueryTweetsDB", tweetQuery)){
                return "Tweets including \"" + tweetQuery + "\" ALREADY retrieved and pushed to database. You can use /mongo querypull \"" + tweetQuery + "\"(without quote) to generate a download link.";
            }
            twitter4j.Twitter twitter = twitterLogin();
            SearchResource resource = twitter.v1().search();
            Query query = Query.of(tweetQuery);
            QueryResult result;
            List<Status> tweets = new ArrayList<>();
            RateLimitStatus ratelimit;
            do {
                result = resource.search(query);
                tweets.addAll(result.getTweets());
                System.out.println(result.getRateLimitStatus());
                ratelimit = result.getRateLimitStatus();
            } while ((query = result.nextQuery()) != null && ratelimit.getRemaining() > 0);
            MongoDB mongo = new MongoDB();
            if(mongo.addToDB(tweets, tweetQuery, "QueryTweetsDB").equals("DBSuccess")){
                return "Tweets including \"" + tweetQuery + "\" successfully retrieved and pushed to database. You have " + ratelimit.getSecondsUntilReset() + " seconds remaining for another query search. You can use /mongo querypull \"" + tweetQuery + "\"(without quote) to generate a download link.";
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            return "The query I've searched doesn't exist.";
        }
        return "Unable to retrieve tweets.";
    }

    public String getUserTimeline(String username){
        if (username.length() < 1) {
            return "Username length is shorter than the minimum.";
        }
        try {
            if(MongoDB.collectionExists("UserTweetsDB", username)){
                return "@" + username + "'s timeline ALREADY retrieved and pushed to database. You can use /mongo userpull " + username + " to generate a download link.";
            }
            twitter4j.Twitter twitter = twitterLogin();
            //int numberOfTweets = 3200; //rate limit
            long lastID = Long.MAX_VALUE;
            UsersResources users = twitter.v1().users();
            User user = users.showUser(username);
            System.out.println(user);
            System.out.println(user.getStatusesCount());
            List<Status> tweets = new ArrayList<>();
            List<Status> newTweets;
            /*if(user.isProtected()){ //if the profile is locked
                System.out.println("@" + username + "'s profile is protected.");
                return "@" + username + "'s profile is protected.";
            }*/
            //check if the new tweet list is empty, if it's empty there's no new tweets so break
            do {
                try {
                    newTweets = twitter.v1().timelines().getUserTimeline(username, Paging.ofMaxId(lastID - 1));
                    tweets.addAll(newTweets);
                    if (tweets.get(tweets.size() - 1).getId() < lastID) lastID = tweets.get(tweets.size() - 1).getId(); //continue from which getId we left off
                } catch (TwitterException te) {
                    te.printStackTrace();
                    return "Unable to retrieve user's timeline. (" + te.getMessage() + ")";
                }
            } while(!newTweets.isEmpty());

            //push user timeline to database
            MongoDB mongo = new MongoDB();
            if(mongo.addToDB(tweets, username, "UserTweetsDB").equals("DBSuccess")){
                return "@" + user.getScreenName() + " - (" + user.getName() + ")'s timeline successfully retrieved and pushed to database. You can use /mongo userpull " + user.getScreenName() + " to generate a download link.";
            }
        } catch (TwitterException te){
            return "User doesn't exist.";
        }
        return "Unable to retrieve user's timeline.";
    }

    public static int checkRateLimitStatus() throws TwitterException {
        //CHECK RATE LIMIT FOR TWITTER
        return -1;
    }
}
