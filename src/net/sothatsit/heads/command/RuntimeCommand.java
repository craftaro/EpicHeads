package net.sothatsit.heads.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RuntimeCommand extends Command {
    
    private CommandExecutor executor;
    
    public RuntimeCommand(String name) {
        super(name);
    }
    
    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }
    
    public CommandExecutor getExecutor() {
        return executor;
    }
    
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return executor.onCommand(sender, this, label, args);
    }
    
}
