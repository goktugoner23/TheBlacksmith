import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class BotListeners extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { //ignore bot for not entering into infinite message loops
            return;
        }
        System.out.println("I received a message from " + event.getAuthor().getName() + ":" + event.getMessage().getContentDisplay());
        String messageReceived = event.getMessage().getContentRaw();
        //standart message tryout
        if (messageReceived.equals("!ney")) {
            event.getChannel().sendMessage("Babana gotten mi gireyim(yatay)?").queue();
        }
        //more complex message tryout
        if (messageReceived.indexOf("!armory") == 0) { //message should be like !armory geliyorum dragonmaw eu
            String message = event.getMessage().getContentRaw();
            WowArmoryFunctions parsing = new WowArmoryFunctions(); //declare an armory class
            if (parsing.parseArmoryLink(message).equals("error")) {
                event.getChannel().sendMessage("Server type mismatch! Please write server type as \"eu\" or \"us\".").queue();
            } else {
                event.getChannel().sendMessage(parsing.parseArmoryLink(message)).queue();
            }
        }
        //purge the channel, will go into interactioneventlistener
        if (messageReceived.equals("!purge")) {
            List<Message> messageList = event.getChannel().getHistory().retrievePast(10).complete();
            event.getChannel().asTextChannel().deleteMessages(messageList).queue();
        }
    }
}
