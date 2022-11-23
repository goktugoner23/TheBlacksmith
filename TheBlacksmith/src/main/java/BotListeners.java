import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
}