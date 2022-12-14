package com.goktugoner.commands;

import com.goktugoner.ICommand;
import com.goktugoner.SeleniumWebDrive;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Armory implements ICommand {
    @Override
    public String getName() {
        return "armory";
    }

    @Override
    public String getDescription() {
        return "Gets character info from Wow Armory";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> armoryOptionsList = new ArrayList<>();
        armoryOptionsList.add(new OptionData(OptionType.STRING, "name", "charactername", true));
        armoryOptionsList.add(new OptionData(OptionType.STRING, "level", "level", false));
        armoryOptionsList.add(new OptionData(OptionType.STRING, "race", "race", false));
        armoryOptionsList.add(new OptionData(OptionType.STRING, "class", "class", false));
        armoryOptionsList.add(new OptionData(OptionType.STRING, "faction", "faction", false));
        armoryOptionsList.add(new OptionData(OptionType.STRING, "realm", "realm", false));
        return armoryOptionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        //write the option filter
        //get the options and write them to a string list to pass onto selenium //FUCK 5 IF-ELSE LOOP?? SHORTEN THIS BITCH
        //name - race - class - faction - realm
        event.deferReply().queue();
        String charName = toUpper(Objects.requireNonNull(event.getOption("name")).getAsString());
        LinkedHashSet<String> charAttributes = new LinkedHashSet<>();
        if(event.getOptions().size() > 1){ //check if there are more options than only name
            event.getOptions().remove(0); //remove the name variable
            for(OptionMapping opt : event.getOptions()){
                if(opt != null){
                    if(opt.getName().equals("realm")){
                        charAttributes.add(correctAtt(opt.getAsString()));
                    }else{
                        charAttributes.add(toUpper(opt.getAsString()));
                    }
                }
            }
        }
        //need to make every server name, race and class referenced from a dictionary
        List<LinkedHashSet<String>> fullCharList = SeleniumWebDrive.navigateWowArmory(charName); //full charlist from selenium - size 6
        if(fullCharList == null){ //empty condition
            event.getInteraction().getHook().editOriginal("Character not found.").complete();
        }else{
            List<LinkedHashSet<String>> finalCharList = new ArrayList<>(); //final list according to options the user entered
            for(LinkedHashSet<String> lhs : fullCharList){
                if(lhs.containsAll(charAttributes)){
                    finalCharList.add(lhs);
                }
            }
            EmbedBuilder charProfile = new EmbedBuilder();
            charProfile.setTitle("Found Characters: " + charName);
            //charProfile.setDescription();
            for(LinkedHashSet<String> lhs : finalCharList){
                String[] writer = new String[lhs.size()];
                writer = lhs.toArray(writer); //we made set as array, we don't need the Name
                charProfile.addField("Level", writer[1], true);
                charProfile.addField("Race", writer[2], true);
                charProfile.addField("Class", writer[3], true);
                charProfile.addField("Faction", writer[4], true);
                charProfile.addField("Realm", writer[5], true);
                charProfile.addField("URL", writer[6], false);
            }
            charProfile.setColor(Color.CYAN);
            event.getInteraction().getHook().editOriginalEmbeds(charProfile.build()).complete();
        }

        //finalCharList is the filtered list so we'll return that as an embed list in discord
    }

    private String toUpper(String name){
        String lower = name.toLowerCase();
        String s1 = lower.substring(0, 1).toUpperCase();
        return s1 + name.substring(1);
    }

    private String correctAtt(String name){ //correct race class servername
        //DIDNT TEST THIS OUT YET - TESTED CONTAINS() DOESNT WORK
        String lowerName = name.toLowerCase();
        //server names with a space or apostrophe in them
        List<String> servers = Arrays.asList("Aerie Peak", "Ahn'Qiraj", "Al'Akir", "Azjol-Nerub",
                "Blade's Edge", "Bronze Dragonflight", "Burning Blade", "Burning Legion", "Burning Steppes",
                "Chamber of Aspects", "Darkmoon Faire", "Defias Brotherhood", "Drak'thul", "Earthen Ring",
                "Emerald Dream", "Grim Batol", "Kor'gall", "Kul Tiras", "Laughing Skull", "Lightning's Blade",
                "Quel'thalas", "Scarshield Legion", "Shadow Moon", "Shattered Halls", "Shattered Hand",
                "Steamwheedle Cartel", "Tarren Mill", "The Maelstrom", "The Sha'tar", "The Venture Co",
                "Twilight's Hammer", "Twisting Nether", "Vek'nilash");
        for(String s : servers){
            if(s.contains(lowerName)){
                return s;
            }
        }
        return "realmnamemismatch";
    }
}
