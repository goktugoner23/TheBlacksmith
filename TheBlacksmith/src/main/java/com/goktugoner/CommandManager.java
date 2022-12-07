package com.goktugoner;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    private final List<ICommand> commandList = new ArrayList<>(); //I made this final

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
            for(ICommand command : commandList){
                if(command.getOptions() == null) {
                    jda.upsertCommand(command.getName(), command.getDescription()).queue();
                } else {
                    jda.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
                }
            }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for(ICommand command : commandList){
            if(command.getName().equals(event.getName())){
                command.execute(event);
                return;
            }
        }
    }

    public void add(ICommand command){
        commandList.add(command);
    }
}
