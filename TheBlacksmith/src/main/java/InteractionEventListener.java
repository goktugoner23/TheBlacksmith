import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class InteractionEventListener extends ListenerAdapter {
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
        super.onSlashCommandInteraction(event);
        switch(event.getName()){
            case "twitterhelp":
                event.reply(twitterHelpText()).setEphemeral(true).queue();
                break;
            /*case "purge":
                event.reply("Purges 100 messages (Not finished).").setEphemeral(true).queue();
                break;*/
        }
    }

    private String twitterHelpText(){
        return "!twitteruserfind <username> - Finds a user on Twitter and posts the profile link.\n" +
                "\n" +
                "!twitteruserpull <username> - Gets the user's timeline and saves it into a database.\n" +
                "\n" +
                "!twitterquery - <search query> Twitter query search (not finished)\n" +
                "\n" +
                "!twitteruserlist - Shows Twitter user database that you pulled.\n" +
                "\n" +
                "!twitteruserremove <username> - Removes a user from the database.\n" +
                "\n" +
                "!mongopull <username> - Pulls a user's timeline from the database and pushes it into an Excel file. You can download it later(not finished)";
    }
}
