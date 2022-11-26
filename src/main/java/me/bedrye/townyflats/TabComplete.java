package me.bedrye.townyflats;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete (@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args){
        List<String> l = new ArrayList<>();
        if(label.equalsIgnoreCase("tapp")){
            if (sender instanceof Player) {
                if (args.length == 1) {
                    if (sender.hasPermission("townyapartments.claim")) {
                        l.add("claim");
                    }
                    if (sender.hasPermission("townyapartments.delete")) {
                        l.add("delete");
                    }
                    if (sender.hasPermission("townyapartments.reload")) {
                        l.add("reload");
                    }
                    l.add("buy");
                    l.add("list");
                    l.add("sell");
                    l.add("info");
                    l.add("remove");
                    l.add("add");

                }
                if (args.length == 2) {
                    switch (args[0].toLowerCase()) {
                        case "sell":
                            l.add("COST");
                            break;
                        case "claim":
                            l.add("NAME");
                            break;
                        case "list":
                            l.add("player");
                            l.add("town");
                            l.add("chunk");
                            break;

                    }
                }


                return l;
            }
    }return null;
}
}

