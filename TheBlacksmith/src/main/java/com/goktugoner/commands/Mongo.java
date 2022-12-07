package com.goktugoner.commands;

import com.goktugoner.*;
import com.mongodb.MongoException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Mongo implements ICommand {
    @Override
    public String getName() {
        return "mongo";
    }

    @Override
    public String getDescription() {
        return "MongoDB operations";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> mongoOptionsList = new ArrayList<>();
        mongoOptionsList.add(new OptionData(OptionType.STRING, "operation", "operation to perform", true));
        mongoOptionsList.add(new OptionData(OptionType.STRING, "query", "query", false));
        return mongoOptionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        //write the option filter
        String optionName = Objects.requireNonNull(event.getOption("operation")).getAsString();
        event.deferReply().queue();
        MongoDB mongo = new MongoDB();
        if(optionName.equals("userlist")){
            try{
                if(mongo.listCollectionsDB("UserTweetsDB") != null){
                    event.getInteraction().getHook().editOriginal("User list in database: " + mongo.listCollectionsDB("UserTweetsDB").toString()).complete();
                }else{
                    event.getInteraction().getHook().editOriginal("Twitter user database is empty.").complete();
                }
            }catch (MongoException m){
                throw new RuntimeException(m);
            }
        }else
        if(optionName.equals("querylist")){
            try{
                if(mongo.listCollectionsDB("QueryTweetsDB") != null){
                    event.getInteraction().getHook().editOriginal("Query list in database: " + mongo.listCollectionsDB("QueryTweetsDB").toString()).complete();
                }else{
                    event.getInteraction().getHook().editOriginal("Twitter query database is empty.").complete();
                }
            }catch (MongoException m){
                throw new RuntimeException(m);
            }
        }else
        if(optionName.equals("userpull")){
            //get query also to search for users and query
            if(event.getOption("query") == null){
                event.getInteraction().getHook().editOriginal("Username can't be null.").complete();
            }
            String query = Objects.requireNonNull(event.getOption("query")).getAsString();
            if(query.length() < 2){
                event.getInteraction().getHook().editOriginal("Username doesn't exist.").complete();
            }else{
                try {
                    ReturnType mongoFile = MongoDB.createExcelfromDB(MongoDB.exportDB(query, "UserTweetsDB"), query);
                    event.getInteraction().getHook().editOriginal(mongoFile.getMessage()).queue();
                    if(mongoFile.isSuccess()){
                        event.getInteraction().getHook().editOriginal("Generating download link for file...").queue();
                        String link = SeleniumWebDrive.getWeTransferLink(mongoFile.getFile()); //taking approx 15 secs
                        event.getInteraction().getHook().editOriginal("Download Link: " + link).complete();
                        mongoFile.getFile().delete(); //remove file from mongoDB after sending link
                    }else{
                        event.getInteraction().getHook().editOriginal("Couldn't generate link. User might not be in the database.").complete();
                    }
                } catch (MongoException m) {
                    throw new RuntimeException(m);
                }
            }
        }else
        if(optionName.equals("querypull")){
            if(event.getOption("query") == null){
                event.getInteraction().getHook().editOriginal("Username can't be null.").complete();
            }
            String query = Objects.requireNonNull(event.getOption("query")).getAsString();
            if(query.length() < 2){
                event.getInteraction().getHook().editOriginal("Query doesn't exist.").complete();
            }else{
                try {
                    ReturnType mongoFile = MongoDB.createExcelfromDB(MongoDB.exportDB(query, "QueryTweetsDB"), query);
                    event.getChannel().sendMessage(mongoFile.getMessage()).queue();
                    if(mongoFile.isSuccess()){
                        event.getChannel().sendMessage("Generating download link for file...").queue();
                        String link = SeleniumWebDrive.getWeTransferLink(mongoFile.getFile()); //taking approx 15 secs
                        event.getChannel().sendMessage("Download Link: " + link).complete();
                    }else{
                        event.getInteraction().getHook().editOriginal("Couldn't generate link. Query might not be in the database.").complete();
                    }
                } catch (MongoException m) {
                    throw new RuntimeException(m);
                }
            }
        }else
        if(optionName.equals("userremove")){
            if(event.getOption("query") == null){
                event.getInteraction().getHook().editOriginal("Username can't be null.").complete();
            }
            String query = Objects.requireNonNull(event.getOption("query")).getAsString();
            if(query.length() < 2) {
                event.getInteraction().getHook().editOriginal("Username doesn't exist.").complete();
            }else{
                try{
                    event.getInteraction().getHook().editOriginal(mongo.dropCollection(query, "UserTweetsDB")).queue();
                }catch (MongoException m){
                    throw new RuntimeException(m);
                }
            }
        }else
        if(optionName.equals("queryremove")){
            if(event.getOption("query") == null){
                event.getInteraction().getHook().editOriginal("Username can't be null.").complete();
            }
            String query = Objects.requireNonNull(event.getOption("query")).getAsString();
            if(query.length() < 2) {
                event.getInteraction().getHook().editOriginal("Query doesn't exist.").complete();
            }else{
                try{
                    event.getInteraction().getHook().editOriginal(mongo.dropCollection(query, "QueryTweetsDB")).queue();
                }catch (MongoException m){
                    throw new RuntimeException(m);
                }
            }
        }else{
            event.getInteraction().getHook().editOriginal("This is not a valid option. Refer to \"/commandhelp mongo\" for instructions.").complete();
        }
    }
}
