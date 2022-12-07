package com.goktugoner.commands;

import com.goktugoner.ICommand;
import com.goktugoner.TwitterFunctions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Twitter implements ICommand {
    @Override
    public String getName() {
        return "twitter";
    }

    @Override
    public String getDescription() {
        return "Twitter operations";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> twitterOptionsList = new ArrayList<>();
        twitterOptionsList.add(new OptionData(OptionType.STRING, "operation", "userfind, userpull or querypull", true));
        twitterOptionsList.add(new OptionData(OptionType.STRING, "query", "query to search", true));
        return twitterOptionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        //write the option filter
        String optionName = Objects.requireNonNull(event.getOption("operation")).getAsString();
        event.deferReply().queue();
        TwitterFunctions twitterFunctions = new TwitterFunctions();
        //get query also to search for users and query
        if(optionName.equals("userfind")){
            if(event.getOption("query") == null){
                event.getInteraction().getHook().editOriginal("Username can't be null.").complete();
            }
            String query = Objects.requireNonNull(event.getOption("query")).getAsString();
            if(query.length() < 2){
                event.getInteraction().getHook().editOriginal("Username must be longer than 2 characters.").complete();
            }else{
                //find the user
                List<String> userVars = twitterFunctions.findUser(query);
                EmbedBuilder twitterProfile = new EmbedBuilder();
                twitterProfile.setThumbnail(userVars.get(7)); //image on top right
                twitterProfile.setTitle(userVars.get(0));
                twitterProfile.setDescription(userVars.get(2));
                twitterProfile.addField("Name",userVars.get(1), false); //inline=true means same row, false means separate rows
                twitterProfile.addField("Display URL", userVars.get(3), false);
                twitterProfile.addField("E-mail", userVars.get(4).toString(), false);
                twitterProfile.addField("Location",userVars.get(5), false);
                twitterProfile.addField("Followers", userVars.get(9), false);
                twitterProfile.addField("Profile Image URL", userVars.get(6), false);
                twitterProfile.addField("Bigger Profile Image URL", userVars.get(7), false);
                twitterProfile.addField("Profile URL", userVars.get(8), false);
                twitterProfile.setColor(Color.BLUE);
                event.getInteraction().getHook().editOriginalEmbeds(twitterProfile.build()).complete();
            }
        }else
        if(optionName.equals("userpull")){
            if(event.getOption("query") == null){
                event.getInteraction().getHook().editOriginal("Username can't be null.").complete();
            }
            String query = Objects.requireNonNull(event.getOption("query")).getAsString();
            if(query.length() < 2 || query.contains(" ")){
                event.getInteraction().getHook().editOriginal("Username must be longer than 1 character and contain no spaces.").complete();
            }else{
                event.getInteraction().getHook().editOriginal("Getting @" + query + "'s tweets...").queue();
                event.getInteraction().getHook().editOriginal(twitterFunctions.getUserTimeline(query)).complete();
            }
        }else
        if(optionName.equals("querypull")){
            if(event.getOption("query") == null){
                event.getInteraction().getHook().editOriginal("Search query can't be null").complete();
            }
            String query = Objects.requireNonNull(event.getOption("query")).getAsString();
            if(query.length() < 2){
                event.getInteraction().getHook().editOriginal("Search query must be longer than 1 character.").complete();
            }else{
                event.getInteraction().getHook().editOriginal("Getting tweets including \""+ query + "\" ...").queue();
                event.getInteraction().getHook().editOriginal(twitterFunctions.searchTweet(query)).queue();
            }
        }else{
            event.getInteraction().getHook().editOriginal("This is not a valid option. Refer to \"/commandhelp twitter\" for instructions.").complete();
        }
    }
}
