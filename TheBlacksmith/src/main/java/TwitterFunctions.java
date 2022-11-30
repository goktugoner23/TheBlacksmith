import net.dv8tion.jda.internal.requests.Route;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.*;
import twitter4j.v1.Paging;

import java.util.ArrayList;
import java.util.List;

public class TwitterFunctions {
    private Twitter twitterLogin(){
        String oAutConsumerKey = "";
        String oAuthConsumerSecret = "";
        String oAuthAccess = "";
        String oAuthAccessToken = "";
        return Twitter.newBuilder().oAuthConsumer(oAutConsumerKey, oAuthConsumerSecret)
                .oAuthAccessToken(oAuthAccess, oAuthAccessToken)
                .build();
    }
    public String parseTwitterSearchText(String message){
        //parse the string that contains !twitter and constructs a search query and returns it
        String[] command = message.split(" ");
        StringBuilder rest = new StringBuilder();
        for(int i = 1; i < command.length; i++){
            rest.append(command[i] + " ");
        }
        return rest.toString(); //returns the query to search on Twitter
    }

    public String parseTwitterUsername(String username){
        //parse the string that contains !twitter and constructs a search query and returns it
        String[] command = username.split(" ");
        if(command.length > 2){
            return "wrongUsrName";
        }
        return command[1]; //returns the query to search on Twitter
    }

    public void searchTweet(String tweetQuery){
        if (tweetQuery.length() < 1) {
            System.out.println("java twitter4j.examples.search.SearchTweets [query]");
            System.exit(-1);
        }
        twitter4j.Twitter twitter = twitterLogin();
        SearchResource resource = twitter.v1().search();
        try {
            Query query = Query.of(tweetQuery);
            QueryResult result;
            do {
                result = resource.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
            //System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }

    public String getUserTimeline(String username) throws TwitterException {
        if (username.length() < 1) {
            return "Username length is shorter than the minimum.";
        }
        twitter4j.Twitter twitter = twitterLogin();
        //int numberOfTweets = 3200; //rate limit
        long lastID = Long.MAX_VALUE;
        UsersResources users = twitter.v1().users();
        User user = users.showUser(username);
        System.out.println(user);
        System.out.println(user.getStatusesCount());
        List<Status> tweets = new ArrayList<>();
        List<Status> newTweets = null; //check if the new tweet list is empty, if it's empty there's no new tweets so break
        if(user.isProtected()){
            System.out.println("@" + username + "'s profile is protected.");
            return "@" + username + "'s profile is protected.";
        }
        do {
            try {
                newTweets = twitter.v1().timelines().getUserTimeline(username, Paging.ofMaxId(lastID - 1));
                tweets.addAll(newTweets);
                if (tweets.get(tweets.size() - 1).getId() < lastID) lastID = tweets.get(tweets.size() - 1).getId();
            } catch (TwitterException te) {
                te.printStackTrace();
                System.out.println("Failed to get timeline: " + te.getMessage());
                System.exit(-1);
            }
        }while(!newTweets.isEmpty());
        System.out.println("Showing @" + username + "'s user timeline.");
        for(Status status : tweets){
            System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText() + " - " + status.getCreatedAt() + " - "
                    + status.getGeoLocation() +" - " + status.getFavoriteCount() + " - " + status.getRetweetCount() + " - "
                    + status.getInReplyToScreenName() + " - " + status.isRetweeted());
        }
        System.out.println("Total of " + tweets.size() + " tweets.");
        System.out.println(twitter.v1().timelines().getUserTimeline().getRateLimitStatus());
        return "User timeline succesfully retrieved.";
    }
}
