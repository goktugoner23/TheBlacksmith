import com.mongodb.MongoException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class BotListenerMongoDB extends ListenerAdapter{

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { //ignore bot for not entering into infinite message loops
            return;
        }
        String messageReceived = event.getMessage().getContentRaw();
        //get the userlist in mongoDB
        if(messageReceived.indexOf("!twitteruserlist") == 0){
            MongoDB mongo = new MongoDB();
            try{
                if(mongo.listCollectionsDB() != null){
                    event.getChannel().sendMessage(mongo.listCollectionsDB().toString()).queue();
                }else{
                    event.getChannel().sendMessage("Twitter user database is empty.").queue();
                }
            }catch (MongoException m){
                throw new RuntimeException(m);
            }
        }
        //Download user Excel from mongoDB
        if(messageReceived.indexOf("!mongopull") == 0){
            TwitterFunctions twitterFunctions = new TwitterFunctions(); //declare a twitter class
            String username = twitterFunctions.parseTwitterUsername(messageReceived);
            if(username.equals("wrongUsrName")){
                event.getChannel().sendMessage("Username doesn't exist.").queue();
            }else{
                try {
                    ReturnType mongoFile = MongoDB.createExcelfromDB(MongoDB.exportDB(username), username);
                    event.getChannel().sendMessage(mongoFile.getMessage()).queue();
                    if(mongoFile.isSuccess()){
                        event.getChannel().sendMessage("Generating download link for file...").queue();
                        String link = SeleniumWebDrive.getWeTransferLink(mongoFile.getFile()); //taking approx 15 secs
                        event.getChannel().sendMessage("Download Link: " + link).queue();
                    }
                } catch (MongoException m) {
                    throw new RuntimeException(m);
                }
            }
        }
        //remove userlist from mongoDB
        if(messageReceived.indexOf("!twitteruserremove") == 0){
            TwitterFunctions twitterFunctions = new TwitterFunctions(); //declare a twitter class
            String username = twitterFunctions.parseTwitterUsername(messageReceived);
            if(username.equals("wrongUsrName")) {
                event.getChannel().sendMessage("Username doesn't exist.").queue();
            }else{
                try{
                    MongoDB mongo = new MongoDB();
                    event.getChannel().sendMessage(mongo.dropCollection(username)).queue();
                }catch (MongoException m){
                    throw new RuntimeException(m);
                }
            }
        }
    }
}
