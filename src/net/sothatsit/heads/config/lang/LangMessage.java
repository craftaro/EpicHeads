package net.sothatsit.heads.config.lang;

import java.util.Arrays;

import net.sothatsit.heads.util.ArrayUtils;

import org.bukkit.command.CommandSender;

public final class LangMessage {
    
    private final String[] messages;
    private final Placeholder[] placeholders;

    public LangMessage(String... messages) {
        this(messages, new Placeholder[0]);
    }

    private LangMessage(String[] messages, Placeholder[] placeholders) {
        this.messages = messages;
        this.placeholders = placeholders;
    }

    public LangMessage with(Placeholder... placeholders) {
        return new LangMessage(messages, ArrayUtils.append(this.placeholders, placeholders));
    }

    public LangMessage with(String key, Object value) {
        return with(new Placeholder(key, value));
    }

    public int getLineCount() {
        return messages.length;
    }

    public boolean isEmpty() {
        return getLineCount() == 0;
    }

    @Override
    public String toString() {
        return getSingle();
    }

    public String getSingle() {
        return (isEmpty() ? "" : get()[0]);
    }
    
    public String[] get() {
        return Placeholder.applyAll(Placeholder.colourAll(messages), placeholders);
    }

    public void send(CommandSender sender) {
        for (String message : get()) {
            sender.sendMessage(message);
        }
    }

    public Object getConfigSaveValue() {
        if(isEmpty())
            return "";

        if(getLineCount() == 1)
            return messages[0];

        return Arrays.asList(messages);
    }
    
}
