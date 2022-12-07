package com.goktugoner.commands;

import com.goktugoner.ICommand;
import com.goktugoner.SeleniumWebDrive;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
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
        armoryOptionsList.add(new OptionData(OptionType.STRING, "race", "race", false));
        armoryOptionsList.add(new OptionData(OptionType.STRING, "class", "class", false));
        armoryOptionsList.add(new OptionData(OptionType.STRING, "faction", "faction", false));
        armoryOptionsList.add(new OptionData(OptionType.STRING, "realm", "realm", false));
        return armoryOptionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        List<String> optionsList = new ArrayList<>();
        for(OptionMapping option : event.getOptions()){
            optionsList.add(option.getAsString());
        }
        String asd = event.getUser().getAsTag();
        String link = "";
        event.getInteraction().deferReply().setEphemeral(true).queue(); //this should wait for responding but it's not
        link = SeleniumWebDrive.navigateWowArmory(optionsList);
        event.getInteraction().getHook().editOriginal("Your link is ready!").setActionRow(Button.link(link, "Go to URL")).complete();
    }
}
