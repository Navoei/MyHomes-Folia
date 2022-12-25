package me.navoei.myhomes.commands.player;

import me.navoei.myhomes.MyHomes;
import me.navoei.myhomes.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class SetHomeCommand implements CommandExecutor {

    MyHomes plugin = MyHomes.getInstance();
    BukkitScheduler scheduler = plugin.getServer().getScheduler();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 1) {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }



        if (args.length == 1) {

            plugin.getRDatabase().getHomeInfo(player, args[0]).thenAccept(result_homeInfo -> {

               if (!result_homeInfo.isEmpty()) {
                   String homeName = result_homeInfo.get(0);
                   scheduler.runTaskAsynchronously(plugin, () -> plugin.getRDatabase().updateHomeLocation(player, homeName));

                   if (homeName.equalsIgnoreCase("Home")) {
                       player.sendMessage(Lang.PREFIX.toString() + Lang.HOME_UPDATED);
                   } else {
                       player.sendMessage(Lang.PREFIX + Lang.HOME_SPECIFIED_UPDATED.toString().replace("%home%", homeName));
                   }

                   return;
               }

                if (args[0].equalsIgnoreCase("Home")) {
                    scheduler.runTaskAsynchronously(plugin, () -> plugin.getRDatabase().setHomeColumns(player, "Home", false));
                    player.sendMessage(Lang.PREFIX.toString() + Lang.SET_HOME);
                } else {
                    scheduler.runTaskAsynchronously(plugin, () -> plugin.getRDatabase().setHomeColumns(player, args[0], false));
                    player.sendMessage(Lang.PREFIX + Lang.SET_HOME_SPECIFIED.toString().replace("%home%", args[0]));
                }

            });
            return true;
        }

        plugin.getRDatabase().getHomeInfo(player, "Home").thenAccept(result_homeInfo -> {
           if (!result_homeInfo.isEmpty()) {
               scheduler.runTaskAsynchronously(plugin, () -> plugin.getRDatabase().updateHomeLocation(player, "Home"));
               player.sendMessage(Lang.PREFIX.toString() + Lang.HOME_UPDATED);
               return;
           }

           scheduler.runTaskAsynchronously(plugin, () -> plugin.getRDatabase().setHomeColumns(player, "Home", false));
           player.sendMessage(Lang.PREFIX.toString() + Lang.SET_HOME);

        });

        return false;
    }
}
