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
            event.getChannel().sendMessage("Babana gotten mi gireyim(yatay)?").queue();
        }
        //more complex message tryout
        if (event.getMessage().getContentRaw().contains("!armory")) { //message should be like !armory geliyorum dragonmaw eu
            String message = event.getMessage().getContentRaw();
            WowArmoryFunctions parsing = new WowArmoryFunctions(); //declare an armory class
            if (parsing.parseArmoryLink(message).equals("error")) {
                event.getChannel().sendMessage("Server type mismatch! Please write server type as \"eu\" or \"us\".").queue();
            } else {
                event.getChannel().sendMessage(parsing.parseArmoryLink(message)).queue();
            }
        }
        //Twitter search
        if(event.getMessage().getContentRaw().contains("!twitterquery")){
            TwitterFunctions twitterFunctions = new TwitterFunctions(); //declare a twitter class
            String searchQuery = twitterFunctions.parseTwitterSearchText(event.getMessage().getContentRaw());
            twitterFunctions.searchTweet(searchQuery);
        }
        if(event.getMessage().getContentRaw().contains("!twitteruser")){
            TwitterFunctions twitterFunctions = new TwitterFunctions(); //declare a twitter class
            String username = twitterFunctions.parseTwitterUsername(event.getMessage().getContentRaw());
            if(username.equals("wrongUsrName")){
                event.getChannel().sendMessage("Username doesn't exist.").queue();
            }else{
                try {
                    event.getChannel().sendMessage(twitterFunctions.getUserTimeline(username)).queue();
                } catch (TwitterException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



}