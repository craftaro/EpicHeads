package net.sothatsit.heads.config.lang;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.cache.CacheHead;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Lang {
    
    public static LangMessage get(String key) {
        return Heads.getLangConfig().getMessage(key);
    }
    
    public static class HelpSection {
        
        private String key;
        
        public HelpSection(String key) {
            this.key = key;
        }
        
        public String key() {
            return key + ".help";
        }
        
        public String command() {
            return get(key() + ".command").getSingle();
        }
        
        public String description() {
            return get(key() + ".description").getSingle();
        }

        public void sendInvalidArgs(CommandSender sender) {
            Lang.Command.Errors.invalidArgs(this).send(sender);
        }
        
    }

    public static class Currency {

        public static String key() {
            return "currency";
        }

        public static LangMessage format(Player player, double amount) {
            if(amount > 0 && player != null && Heads.getInstance().isExemptFromCost(player))
                return exempt(amount);

            return format(amount);
        }

        public static LangMessage format(double amount) {
            return (amount <= 0 ? zero() : nonZero(amount));
        }

        public static LangMessage zero() {
            return get(key() + ".zero");
        }

        public static LangMessage nonZero(double amount) {
            return get(key() + ".non-zero").with("%amount%", Heads.getEconomy().formatBalance(amount));
        }

        public static LangMessage exempt(double amount) {
            return get(key() + ".exempt").with("%cost%", format(amount));
        }

    }
    
    public static class Menu {
        
        public static String key() {
            return "menu";
        }
        
        public static class Get {
            
            public static String key() {
                return Menu.key() + ".get";
            }
            
            public static LangMessage open() {
                return get(key() + ".open");
            }
            
            public static LangMessage added(String name) {
                return get(key() + ".added")
                        .with("%name%", name);
            }

            public static LangMessage purchased(String name, double cost) {
                return get(key() + ".purchased")
                        .with("%name%", name)
                        .with("%cost%", Currency.format(cost));
            }

            public static LangMessage notEnoughMoney(String name, double cost) {
                return get(key() + ".not-enough-money")
                        .with("%name%", name)
                        .with("%cost%", cost);
            }
            
            public static LangMessage transactionError(String name, double cost) {
                return get(key() + ".transaction-error")
                        .with("%name%", name)
                        .with("%cost%", cost);
            }
            
            public static LangMessage categoryPermission(String category) {
                return get(key() + ".category-permission")
                        .with("%category%", category);
            }
            
        }

        public static class Search {

            public static String key() {
                return Menu.key() + ".get";
            }

            public static LangMessage added(String name) {
                return get(key() + ".added")
                        .with("%name%", name);
            }

            public static LangMessage notEnoughMoney(String name, double cost) {
                return get(key() + ".not-enough-money")
                        .with("%name%", name)
                        .with("%cost%", cost);
            }

            public static LangMessage transactionError(String name, double cost) {
                return get(key() + ".transaction-error")
                        .with("%name%", name)
                        .with("%cost%", cost);
            }

            public static LangMessage categoryPermission(String category) {
                return get(key() + ".category-permission").with("%category%", category);
            }

        }
        
        public static class Remove {
            
            public static String key() {
                return Menu.key() + ".remove";
            }
            
            public static LangMessage open() {
                return get(key() + ".open");
            }
            
            public static LangMessage removed(String name) {
                return get(key() + ".removed")
                        .with("%name%", name);
            }
            
        }
        
        public static class Rename {
            
            public static String key() {
                return Menu.key() + ".rename";
            }
            
            public static LangMessage open(String newName) {
                return get(key() + ".open")
                        .with("%newname%", newName);
            }
            
            public static LangMessage renamed(String oldName, String newName) {
                return get(key() + ".renamed")
                        .with("%name%", oldName)
                        .with("%newname%", newName);
            }
            
        }
        
        public static class Cost {
            
            public static String key() {
                return Menu.key() + ".cost";
            }
            
            public static LangMessage open(double newCost) {
                return get(key() + ".open")
                        .with("%newcost%", Currency.format(newCost));
            }
            
            public static LangMessage setCost(String name, double newCost) {
                return get(key() + ".set-cost")
                        .with("%name%", name)
                        .with("%newcost%", Currency.format(newCost));
            }
            
        }

        public static class CategoryCost {

            public static String key() {
                return Menu.key() + ".category-cost";
            }

            public static LangMessage open(double newCost) {
                return get(key() + ".open")
                        .with("%newcost%", Currency.format(newCost));
            }

            public static LangMessage setCost(String category, double newCost) {
                return get(key() + ".set-cost")
                        .with("%category%", category)
                        .with("%newcost%", Currency.format(newCost));
            }

            public static LangMessage openRemove(double newCost) {
                return get(key() + ".open-remove")
                        .with("%newcost%", Currency.format(newCost));
            }

            public static LangMessage removeCost(String category, double newCost) {
                return get(key() + ".remove-cost")
                        .with("%category%", category)
                        .with("%newcost%", Currency.format(newCost));
            }

        }
        
        public static class Id {
            
            public static String key() {
                return Menu.key() + ".id";
            }
            
            public static LangMessage open() {
                return get(key() + ".open");
            }
            
            public static LangMessage clicked(String name, int id) {
                return get(key() + ".clicked")
                        .with("%name%", name)
                        .with("%id%", id);
            }
            
        }
        
    }
    
    public static class Command {
        
        public static String key() {
            return "command";
        }

        public static LangMessage unknownCommand(String command) {
            return get(key() + ".unknown-command")
                    .with("%command%", command);
        }
        
        public static class Errors {
            
            public static String key() {
                return Command.key() + ".errors";
            }

            public static LangMessage mustBePlayer() {
                return get(key() + ".must-be-player");
            }
            
            public static LangMessage noPermission() {
                return get(key() + ".no-permission");
            }

            public static LangMessage invalidArgs(HelpSection commandHelp) {
                return invalidArgs(commandHelp.command());
            }

            public static LangMessage invalidArgs(String valid) {
                return get(key() + ".invalid-arguments")
                        .with("%valid%", valid);
            }
            
            public static LangMessage integer(String number) {
                return get(key() + ".integer")
                        .with("%number%", number);
            }
            
            public static LangMessage number(String number) {
                return get(key() + ".number")
                        .with("%number%", number);
            }

            public static LangMessage negative(String number) {
                return get(key() + ".negative")
                        .with("%number%", number);
            }
            
        }
        
        public static class Help {
            
            public static String key() {
                return Command.key() + ".help";
            }
            
            public static LangMessage header(int page, int pages, int nextPage) {
                return get(key() + ".header")
                        .with("%page%", page)
                        .with("%pages%", pages)
                        .with("%next-page%", nextPage);
            }

            public static int getLineCountPerLine() {
                return get(key() + ".line").getLineCount();
            }

            public static LangMessage line(HelpSection commandHelp) {
                return line(commandHelp.command(), commandHelp.description());
            }

            public static LangMessage line(String command, String description) {
                return get(key() + ".line")
                        .with("%command%", command)
                        .with("%description%", description);
            }

            public static LangMessage footer(int page, int pages, int nextPage) {
                return get(key() + ".footer")
                        .with("%page%", page)
                        .with("%pages%", pages)
                        .with("%next-page%", nextPage);
            }

            public static LangMessage unknownPage(int page, int pages) {
                return get(key() + ".unknown-page")
                        .with("%page%", page)
                        .with("%pages%", pages);
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }

        public static class Reload {

            public static String key() {
                return Command.key() + ".reload";
            }

            public static LangMessage reloaded() {
                return get(key() + ".reloaded");
            }

            public static HelpSection help() {
                return new HelpSection(key());
            }

        }
        
        public static class Get {
            
            public static String key() {
                return Command.key() + ".get";
            }
            
            public static LangMessage headName(String name) {
                return get(key() + ".head-name")
                        .with("%name%", name);
            }
            
            public static LangMessage oldMethod() {
                return get(key() + ".old-method");
            }
            
            public static LangMessage adding(String name) {
                return get(key() + ".adding")
                        .with("%name%", name);
            }
            
            public static LangMessage fetching() {
                return get(key() + ".fetching");
            }
            
            public static LangMessage cantFind(String name) {
                return get(key() + ".cant-find")
                        .with("%name%", name);
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Random {
            
            public static String key() {
                return Command.key() + ".random";
            }

            public static LangMessage noHeads() {
                return get(key() + ".no-heads");
            }

            public static LangMessage cantFindPlayer(String name) {
                return get(key() + ".cant-find-player")
                        .with("%name%", name);
            }

            public static LangMessage retrievingOwn(CacheHead head) {
                return get(key() + ".retrieve-own")
                        .with("%name%", head.getName())
                        .with("%category%", head.getCategory());
            }

            public static LangMessage retrieving(CacheHead head) {
                return get(key() + ".retrieve")
                        .with("%name%", head.getName())
                        .with("%category%", head.getCategory());
            }

            public static LangMessage give(Player player, CacheHead head) {
                return get(key() + ".give")
                        .with("%player%", player.getName())
                        .with("%name%", head.getName())
                        .with("%category%", head.getCategory());
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Add {
            
            public static String key() {
                return Command.key() + ".add";
            }
            
            public static LangMessage notSupported() {
                return get(key() + ".not-supported");
            }
            
            public static LangMessage categoryLength(String category) {
                return get(key() + ".category-length")
                        .with("%category%", category)
                        .with("%length%", category.length());
            }
            
            public static LangMessage added(String name, String category) {
                return get(key() + ".added")
                        .with("%name%", name)
                        .with("%category%", category);
            }
            
            public static LangMessage fetching() {
                return get(key() + ".fetching");
            }
            
            public static LangMessage cantFind(String name) {
                return get(key() + ".cant-find")
                        .with("%name%", name);
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Hand {
            
            public static String key() {
                return Command.key() + ".hand";
            }
            
            public static LangMessage notSupported() {
                return get(key() + ".not-supported");
            }
            
            public static LangMessage noTextureProperty() {
                return get(key() + ".no-texture-property");
            }
            
            public static LangMessage noNameProperty() {
                return get(key() + ".no-name-property");
            }
            
            public static LangMessage notSkull() {
                return get(key() + ".not-skull");
            }
            
            public static LangMessage categoryLength(String category) {
                return get(key() + ".category-length")
                        .with("%category%", category)
                        .with("%length%", category.length());
            }
            
            public static LangMessage adding(String name, String category) {
                return get(key() + ".adding")
                        .with("%name%", name)
                        .with("%category%", category);
            }
            
            public static LangMessage fetching() {
                return get(key() + ".fetching");
            }
            
            public static LangMessage cantFind(String name) {
                return get(key() + ".cant-find")
                        .with("%name%", name);
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Give {
            
            public static String key() {
                return Command.key() + ".give";
            }
            
            public static LangMessage cantFindPlayer(String name) {
                return get(key() + ".cant-find-player")
                        .with("%name%", name);
            }
            
            public static LangMessage cantFindHead(int id) {
                return get(key() + ".cant-find-head")
                        .with("%id%", id);
            }
            
            public static LangMessage give(int amount, String head, String name) {
                return get(key() + ".give")
                        .with("%amount%", amount)
                        .with("%head%", head)
                        .with("%name%", name);
            }
            
            public static LangMessage invalidAmount(String number) {
                return get(key() + ".invalid-amount")
                        .with("%number%", number);
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class OpenMenu {
            
            public static String key() {
                return Command.key() + ".open-menu";
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Remove {
            
            public static String key() {
                return Command.key() + ".remove";
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }
        
        public static class Rename {
            
            public static String key() {
                return Command.key() + ".rename";
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }

        public static class Search {

            public static String key() {
                return Command.key() + ".search";
            }

            public static LangMessage found(String query, int heads) {
                return get(key() + ".found")
                        .with("%query%", query)
                        .with("%heads%", heads);
            }

            public static LangMessage noneFound(String query) {
                return get(key() + ".none-found")
                        .with("%query%", query);
            }

            public static HelpSection help() {
                return new HelpSection(key());
            }

        }
        
        public static class Cost {
            
            public static String key() {
                return Command.key() + ".cost";
            }
            
            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }

        public static class CategoryCost {

            public static String key() {
                return Command.key() + ".category-cost";
            }

            public static HelpSection help() {
                return new HelpSection(key());
            }

        }
        
        public static class Id {
            
            public static String key() {
                return Command.key() + ".id";
            }

            public static LangMessage holdSkull() {
                return get(key() + ".hold-skull");
            }

            public static LangMessage unknownHead(String name) {
                return get(key() + ".unknown-head")
                        .with("%name%", name);
            }

            public static LangMessage foundID(String name, int id) {
                return get(key() + ".found-id")
                        .with("%name%", name)
                        .with("%id%", id);
            }

            public static HelpSection help() {
                return new HelpSection(key());
            }
            
        }

        public static class ItemEco {

            public static String key() {
                return Command.key() + ".item-eco";
            }

            public static HelpSection help() {
                return new HelpSection(key());
            }

            public static class Set {

                public static String key() {
                    return ItemEco.key() + ".set";
                }

                public static HelpSection help() {
                    return new HelpSection(key());
                }

                public static LangMessage set() {
                    return get(key() + ".set");
                }

                public static LangMessage noItem() {
                    return get(key() + ".no-item");
                }

            }

            public static class Get {

                public static String key() {
                    return ItemEco.key() + ".get";
                }

                public static HelpSection help() {
                    return new HelpSection(key());
                }

                public static LangMessage got(int amount) {
                    return get(key() + ".got")
                            .with("%amount%", amount);
                }

            }

            public static class Give {

                public static String key() {
                    return ItemEco.key() + ".give";
                }

                public static HelpSection help() {
                    return new HelpSection(key());
                }

                public static LangMessage got(int amount) {
                    return get(key() + ".got")
                            .with("%amount%", amount);
                }

                public static LangMessage given(String player, int amount) {
                    return get(key() + ".given")
                            .with("%player%", player)
                            .with("%amount%", amount);
                }

                public static LangMessage unknownPlayer(String player) {
                    return get(key() + ".unknown-player").with("%player%", player);
                }

            }

        }
        
    }
    
}
