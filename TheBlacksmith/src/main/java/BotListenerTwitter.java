import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
public class BotListenerTwitter extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { //ignore bot for not entering into infinite message loops
            return;
        }
        String messageReceived = event.getMessage().getContentRaw();
        //Twitter query search
        if(messageReceived.indexOf("!twitterquery") == 0){
            TwitterFunctions twitterFunctions = new TwitterFunctions(); //declare a twitter class
            String searchQuery = twitterFunctions.parseTwitterSearchText(messageReceived);
            twitterFunctions.searchTweet(searchQuery);
        }
        //Twitter user search and push to database than download Excel file if needed
        if(messageReceived.indexOf("!twitteruserpull") == 0){
            TwitterFunctions twitterFunctions = new TwitterFunctions(); //declare a twitter class
            String username = twitterFunctions.parseTwitterUsername(messageReceived);
            if(username.equals("wrongUsrName")){
                event.getChannel().sendMessage("Usernames must be one word only.").queue();
            }else{
                event.getChannel().sendMessage("Getting @" + username + "'s tweets...").queue();
                event.getChannel().sendMessage(twitterFunctions.getUserTimeline(username)).queue();
            }
        }
        //Find user profile link and post it
        if(messageReceived.indexOf("!twitteruserfind") == 0){
            TwitterFunctions twitterFunctions = new TwitterFunctions(); //declare a twitter class
            String username = twitterFunctions.parseTwitterUsername(messageReceived);
            if(username.equals("wrongUsrName")){
                event.getChannel().sendMessage("Usernames must be one word only.").queue();
            }else{
                event.getChannel().sendMessage(twitterFunctions.findUser(username)).queue();
            }
        }
    }
}
