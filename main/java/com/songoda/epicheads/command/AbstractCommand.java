package com.songoda.epicheads.command;

import com.songoda.epicheads.EpicHeads;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand {

    public enum ReturnType { SUCCESS, FAILURE, SYNTAX_ERROR, NO_CONSOLE }

    private final AbstractCommand parent;

    private final String command;

    private final boolean noConsole;

    protected AbstractCommand(String command, AbstractCommand parent, boolean noConsole) {
        this.command = command;
        this.parent = parent;
        this.noConsole = noConsole;
    }

    public AbstractCommand getParent() {
        return parent;
    }

    public String getCommand() {
        return command;
    }

    public boolean isNoConsole() {
        return noConsole;
    }

    protected abstract ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args);

    public abstract String getPermissionNode();

    public abstract String getSyntax();

    public abstract String getDescription();
}
