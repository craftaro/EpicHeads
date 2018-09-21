package com.songoda.epicheads.command;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.commands.*;
import com.songoda.epicheads.util.Methods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private EpicHeads instance;

    private List<AbstractCommand> commands = new ArrayList<>();

    public CommandManager(EpicHeads instance) {
        this.instance = instance;

        instance.getCommand("EpicHeads").setExecutor(this);

        AbstractCommand commandEpicHeads = addCommand(new CommandOpenMenu());

        addCommand(new CommandHelp(commandEpicHeads));
        addCommand(new CommandReload(commandEpicHeads));
        addCommand(new CommandAdd(commandEpicHeads));
        addCommand(new CommandGive(commandEpicHeads));
        addCommand(new CommandCost(commandEpicHeads));
        addCommand(new CommandId(commandEpicHeads));
        addCommand(new CommandSearch(commandEpicHeads));
        addCommand(new CommandHand(commandEpicHeads));
        addCommand(new CommandItemEco(commandEpicHeads));
        addCommand(new CommandRandom(commandEpicHeads));
        addCommand(new CommandRemove(commandEpicHeads));
        addCommand(new CommandRename(commandEpicHeads));
        addCommand(new CommandGet(commandEpicHeads));
        addCommand(new CommandCategoryCost(commandEpicHeads));
    }

    private AbstractCommand addCommand(AbstractCommand abstractCommand) {
        commands.add(abstractCommand);
        return abstractCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        for (AbstractCommand abstractCommand : commands) {
            if (abstractCommand.getCommand().equalsIgnoreCase(command.getName())) {
                if (strings.length == 0) {
                    processRequirements(abstractCommand, commandSender, strings);
                    return true;
                }
            } else if (strings.length != 0 && abstractCommand.getParent() != null && abstractCommand.getParent().getCommand().equalsIgnoreCase(command.getName())) {
                String cmd = strings[0];
                if (cmd.equalsIgnoreCase(abstractCommand.getCommand())) {
                    processRequirements(abstractCommand, commandSender, strings);
                    return true;
                }
            }
        }
        commandSender.sendMessage(instance.getReferences().getPrefix() + Methods.formatText("&7The command you entered does not exist or is spelt incorrectly."));
        return true;
    }

    private void processRequirements(AbstractCommand command, CommandSender sender, String[] strings) {
        if (!(sender instanceof Player) && command.isNoConsole() ) {
            sender.sendMessage(instance.getLocale().getMessage("command.error.noconsole"));
            return;
        }
        if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
             AbstractCommand.ReturnType returnType = command.runCommand(instance, sender, strings);
            if (returnType == AbstractCommand.ReturnType.NO_CONSOLE) {
                sender.sendMessage(instance.getLocale().getMessage("command.error.noconsole"));
                return;
            }
             if (returnType == AbstractCommand.ReturnType.SYNTAX_ERROR) {
                 sender.sendMessage(instance.getReferences().getPrefix() + Methods.formatText("&cInvalid Syntax!"));
                 sender.sendMessage(instance.getReferences().getPrefix() + Methods.formatText("&7The valid syntax is: &6" + command.getSyntax() + "&7."));
             }
            return;
        }
        sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("event.general.nopermission"));
    }

    public List<AbstractCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
