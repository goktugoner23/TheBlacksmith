package com.goktugoner.commands;

import com.goktugoner.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandHelp implements ICommand {
    //Slash command /help class
    //@Override
    @Override
    public String getName() {
        return "commandhelp";
    }

    @Override
    public String getDescription() {
        return "General help commands";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> helpOptionsList = new ArrayList<>();
        helpOptionsList.add(new OptionData(OptionType.STRING, "operation", "twitter, mongo or null", false));
        return helpOptionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getOption("operation") == null){
            event.reply(generalHelpText()).complete();
        }else{
            switch (Objects.requireNonNull(event.getOption("operation")).getAsString()){
                case "twitter":
                    event.reply(twitterHelpText()).complete();
                    break;
                case "mongo":
                    event.reply(mongoHelpText()).complete();
            }
        }
    }

    public String generalHelpText(){
        return "/commandhelp twitter - Twitter commands\n" +
                "\n" +
                "/commandhelp mongo - MongoDB commands\n" +
                "\n" +
                "/armory <charactername> <race> <class> <faction> <realm> - Searches for character in WoW Armory and pastes URL. Only <charactername> is mandatory.\n" +
                "\n" +
                "/dalle <prompt> - Generate an image with DALL-E (OpenAI) based on the prompt the user give.\n" +
                "\n" +
                "/gpt <prompt> - Ask ChatGPT (You need to have a paid plan)";
    }

    public String twitterHelpText(){
        return "/twitter userfind <username> - Finds a user on Twitter and posts profile info.\n" +
                "\n" +
                "/twitter userpull <username> - Gets the user's timeline and saves it into a database.\n" +
                "\n" +
                "/twitter querypull <query> - Searches for tweets including the query and saves it into a database.";
    }

    public String mongoHelpText(){
        return "/mongo userlist - Shows Twitter user database that you pulled.\n" +
                "\n" +
                "/mongo querylist - Shows Twitter query database that you pulled.\n" +
                "\n" +
                "/mongo userpull <username> - Pulls a query from the database and pushes it into an Excel file. Gives you a weTransfer link to download the file.\n" +
                "\n" +
                "/mongo querypull <query> - Pulls a user's timeline from the database and pushes it into an Excel file. Gives you a weTransfer link to download the file.\n" +
                "\n" +
                "/mongo userremove <username> - Removes the user from the database.\n" +
                "\n" +
                "/mongo queryremove <query> - Removes the query from the database.";
    }
}
