import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException, InterruptedException {
        String token = "DC TOKEN";
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );
        JDA jda = JDABuilder.createDefault(token, intents).setActivity(Activity.watching("Seni")).addEventListeners(new BotListeners(), new BotListenerTwitter(), new BotListenerMongoDB(), new InteractionEventListener()).build().awaitReady();
        jda.upsertCommand("twitterhelp","Twitter commands").setGuildOnly(true).queue();
        //jda.upsertCommand("purge","clear 100 message").setGuildOnly(true).queue();
        //https://www.youtube.com/watch?v=W3zMezRw38c
    }
}
