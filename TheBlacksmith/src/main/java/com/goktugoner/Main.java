package com.goktugoner;

import com.goktugoner.commands.Armory;
import com.goktugoner.commands.CommandHelp;
import com.goktugoner.commands.Mongo;
import com.goktugoner.commands.Twitter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws InterruptedException {
        String token = "";
        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );
        //commands
        CommandManager manager = new CommandManager();
        manager.add(new CommandHelp());
        manager.add(new Twitter());
        manager.add(new Mongo());
        manager.add(new Armory());
        JDA jda = JDABuilder.createDefault(token, intents).setActivity(Activity.playing("with your life - /commandhelp")).addEventListeners(new BotListener(), manager).build().awaitReady();
        jda.updateCommands().queue();
        //jda.upsertCommand("purge","clear 100 message").setGuildOnly(true).queue();
        //https://www.youtube.com/watch?v=W3zMezRw38c
    }
}
