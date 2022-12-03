import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.*;
import twitter4j.v1.Paging;

import java.util.ArrayList;
import java.util.List;

public class TwitterFunctions {
    private Twitter twitterLogin(){
        String oAutConsumerKey = "CONSUMER";
        String oAuthConsumerSecret = "CONSUMERSECRET";
        String oAuthAccessToken = "ACCESS";
        String oAuthAccessTokenSecret = "ACCESSSECRET";
        return Twitter.newBuilder().oAuthConsumer(oAutConsumerKey, oAuthConsumerSecret)
                .oAuthAccessToken(oAuthAccessToken, oAuthAccessTokenSecret).tweetModeExtended(true)
                .build();
    }
    public String parseTwitterSearchText(String message){
        //parse the string that contains !twitter and constructs a search query and returns it
        String[] command = message.split(" ");
        StringBuilder rest = new StringBuilder();
        for(int i = 1; i < command.length; i++){
            rest.append(command[i]).append(" ");
        }
        return rest.toString(); //returns the query to search on Twitter
    }

    public String parseTwitterUsername(String message){
        //parse the string that contains !twitter and constructs a search query and returns it
        String[] command = message.split(" ");
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

    public String getUserTimeline(String username){
        if (username.length() < 1) {
            return "Username length is shorter than the minimum.";
        }
        try {
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
            if(mongo.addToDB(tweets, username).equals("DBSuccess")){
                return "@" + user.getScreenName() + " - (" + user.getName() + ")'s timeline successfully retrieved and pushed to database.";
            }
            if(mongo.addToDB(tweets, username).equals("DBExists")){
                return "@" + user.getScreenName() + " - (" + user.getName() + ")'s timeline ALREADY retrieved and pushed to database.";
            }
        } catch (TwitterException te){
            return "User doesn't exist.";
        }
        return "Unable to retrieve user's timeline.";
    }

    public String findUser(String username){
        try{
            twitter4j.Twitter twitter = twitterLogin();
            UsersResources users = twitter.v1().users();
            User user = users.showUser(username);
            return "https://twitter.com/" + user.getScreenName();
        } catch (TwitterException te){
            return "User doesn't exist.";
        }
    }
}
