package com.craftaro.epicheads.commands;

import com.craftaro.core.chat.ChatMessage;
import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.epicheads.EpicHeads;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandHelp extends AbstractCommand {
    private final EpicHeads plugin;

    public CommandHelp(EpicHeads plugin) {
        super(CommandType.CONSOLE_OK, "help");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        sender.sendMessage("");
        new ChatMessage().fromText(String.format("#ff8080&l%s &8» &7Version %s Created with <3 by #ec4e74&l&oS#fa5b65&l&oo#ff6c55&l&on#ff7f44&l&og#ff9432&l&oo#ffaa1e&l&od#f4c009&l&oa",
                        this.plugin.getDescription().getName(), this.plugin.getDescription().getVersion()))
                .sendTo(sender);
        sender.sendMessage("");
        sender.sendMessage(TextUtils.formatText("&7Welcome to EpicHeads! To get started try using the command /heads to access the heads panel."));
        sender.sendMessage("");
        sender.sendMessage(TextUtils.formatText("&6Commands:"));
        for (AbstractCommand command : this.plugin.getCommandManager().getAllCommands()) {
            if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
                sender.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.YELLOW + command.getSyntax() + ChatColor.GRAY + " - " + command.getDescription());
            }
        }
        sender.sendMessage("");

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays this page.";
    }
}
