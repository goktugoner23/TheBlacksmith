import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.Query;
import twitter4j.v1.QueryResult;
import twitter4j.v1.Status;
import java.util.List;

public class BotListeners extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { //ignore bot for not entering into infinite message loops
            return;
        }
        System.out.println("we received a message from " + event.getAuthor().getName() + ":" + event.getMessage().getContentDisplay());
        //standart message tryout
        if (event.getMessage().getContentRaw().equals("!ney")) {
            event.getChannel().sendMessage("Babana gotten mi gireyim?").queue();
        }
        //more complex message tryout
        if (event.getMessage().getContentRaw().contains("!armory")) { //message should be like !armory geliyorum dragonmaw eu
            String message = event.getMessage().getContentRaw();
            if (parseArmoryLink(message).equals("error")) {
                event.getChannel().sendMessage("Server type mismatch! Please write server type as \"eu\" or \"us\".").queue();
            } else {
                event.getChannel().sendMessage(parseArmoryLink(message)).queue();
            }
        }
        //Twitter search
        if(event.getMessage().getContentRaw().contains("!twitter")){
            String searchQuery = parseTwitterSearchText(event.getMessage().getContentRaw());
            searchTwitterUsername(searchQuery);
        }
    }

    public String parseArmoryLink(String message) {
        //parse the string that contains !armory and constructs a link and returns it
        //so the armory code should look like e.g: https://worldofwarcraft.com/en-gb/character/eu/dragonmaw/geliyorum
        String[] command = message.split(" ");
        //command[0] is the !armory
        String charName = command[1];
        String serverName = command[2];
        String serverType = command[3];
        String[] parsedLink = {"https://worldofwarcraft.com/en-gb/character", serverType, serverName, charName};
        if (!serverType.equals("eu") && !serverType.equals("us")) {
            return "error";
        }
        //construct the final link
        StringBuilder finalLink = new StringBuilder();
        for (String var : parsedLink) {
            finalLink.append(var);
            finalLink.append('/');
        }
        return finalLink.toString();
    }

    public String parseTwitterSearchText(String message){
        //parse the string that contains !twitter and constructs a search query and returns it
        String[] command = message.split(" ");
        return command[1]; //returns the username to search on Twitter
    }

    public void searchTwitterUsername(String username){
        if (username.length() < 1) {
            System.out.println("java twitter4j.examples.search.SearchTweets [query]");
            System.exit(-1);
        }
        //twitter4j.v1.SearchResource twitter = Twitter.getInstance().v1().search();
        Twitter twitter = Twitter.newBuilder().oAuthConsumer("", "").oAuthAccessToken("", "").build();
        try {
            Query query = Query.of(username);
            QueryResult result;
            do {
                result = twitter.v1().search().search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }

}