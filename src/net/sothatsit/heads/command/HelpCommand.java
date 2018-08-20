package net.sothatsit.heads.command;

import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.config.lang.Lang.HelpSection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getHelpCommand();
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Help.help();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] args) {
        return onCommand(sender, args, 10);
    }

    public boolean onCommand(CommandSender sender, String[] args, int screenSpace) {
        if(args.length > 2) {
            sendInvalidArgs(sender);
            return true;
        }

        int page = 1;

        if(args.length == 2) {
            try {
                page = Integer.valueOf(args[1]);
            } catch(NumberFormatException e) {
                Lang.Command.Errors.integer(args[1]).send(sender);
                return true;
            }
        }

        int lineLength = Lang.Command.Help.getLineCountPerLine();
        int linesPerPage = (lineLength >= 1 && lineLength <= (screenSpace - 2) ? (screenSpace - 2) / lineLength : 1);
        int pages = (linesPerPage + HeadsCommand.commands.length - 1) / linesPerPage;
        int nextPage = (page >= pages ? 1 : page + 1);

        if(page < 1 || page > pages) {
            Lang.Command.Help.unknownPage(page, pages).send(sender);
            return true;
        }

        Lang.Command.Help.header(page, pages, nextPage).send(sender);

        int startIndex = (page - 1) * linesPerPage;
        int endIndex = page * linesPerPage;

        if(endIndex > HeadsCommand.commands.length) {
            endIndex = HeadsCommand.commands.length;
        }

        for(int index = startIndex; index < endIndex; ++index) {
            HelpSection helpSection = HeadsCommand.commands[index].getHelp();

            Lang.Command.Help.line(helpSection).send(sender);
        }

        Lang.Command.Help.footer(page, pages, nextPage).send(sender);
        return true;
    }
}
