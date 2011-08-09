/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.everdras.failannounce;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author Josh
 */
public class FailAnnounce extends JavaPlugin {
    public static final File path = new File("plugins" + File.separator + "FailAnnounce" + File.separator + "FA_Messages.txt");
    public static final File dir = new File("plugins" + File.separator + "FailAnnounce" + File.separator);
    private final Player[] failers = new Player[2];
    private PermissionHandler permissionHandler;
    
    private String[] messages;
    private boolean defaultMessages;
    

    @Override
    public void onDisable() {
        Logger.getLogger("Minecraft").info("FailAnnounce unloaded.");
    }

    @Override
    public void onEnable() {
        setupPermissions();
        
        if(!dir.exists()) {
            dir.mkdir();
        }
        
        if(!path.exists()) {
            try {
                path.createNewFile();
                //Logger.getLogger("Minecraft").info("Fail Announce: File created at: " + path.getAbsolutePath());
            } catch (Exception ex) {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "Unable to create file.");
            }
        }
        
        Scanner scan;
        
        try {
            scan = new Scanner(path);
        } catch (FileNotFoundException ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, "Error reading file.");
            scan = null;
        }
        
        LinkedList<String> temp = new LinkedList<String>();
        while(scan.hasNextLine()) {
            temp.add(scan.nextLine());
        }
        
        messages = new String[temp.size()];
        
        for(int i = 0; i < messages.length; i++) {
            messages[i] = temp.get(i);
        }
        
        Logger.getLogger("Minecraft").info("FailAnnounce: done loading lists.");
        
        if(messages.length == 0) {
            defaultMessages = true;
        }
        else
            defaultMessages = false;
        
        
        
       
        
                scan.close();
        Logger.getLogger("Minecraft").info("FailAnnounce loaded.");
                
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Logger.getLogger("Minecraft").info("FailAnnounce: Cannot call this command from the console.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(failers[0] != null && failers[1] != null) {
            if(failers[1].equals(failers[0])) {
                if(((Player) sender).equals(failers[0])) {
                    
                    if(permissionHandler != null ? !permissionHandler.has(player, "FailAnnounce.unlimited") : !player.isOp()) {
                    
                        sender.sendMessage("You've already indicated the past 2 fails. Give someone else a turn.");
                        return true;
                    }
                    
                }
            }
        }
        
        
        String pName = ((Player) sender).getName();
        
        
//        if(!label.equalsIgnoreCase("fail")) {
//           return false; 
//        }
        
        if(args.length == 0) {
            getServer().broadcastMessage(ChatColor.DARK_RED + pName + getFailMessage());
            failers[1] = failers[0];
            failers[0] = (Player) sender;
            return true;
            
            
        }
        else if(args.length == 1){
            if(permissionHandler != null ? !permissionHandler.has(player, "FailAnnounce.other") : !player.isOp()) {
                sender.sendMessage("Insufficient permission.");
                return true;
            }
            
            if(sGetPlayer(args[0]) == null) {
               sender.sendMessage("That player is not online.");
               return true;
           }
            
           if(sGetPlayer(args[0]).getName().equalsIgnoreCase("Everdras")) {
               sender.sendMessage(ChatColor.BLUE + "Everdras is infallible.");
               return true;
           }
           
           
           
           getServer().broadcastMessage(ChatColor.DARK_RED + sGetPlayer(args[0]).getName() + getFailMessage()); 
           Logger.getLogger("Minecraft").info(pName + " has indicated that " + args[0] + " has failed.");
           failers[1] = failers[0];
           failers[0] = (Player) sender;
           return true;
        }
        
        return false;
    }
    
    private String getFailMessage() {
        Random gen = new Random();
        String msg = "";
        
        
        
        if (defaultMessages) {
            switch (gen.nextInt(30)) {
                case 0:
                    msg = " has apocalyptically failed.";
                    break;
                case 1:
                    msg = " shall be forever known as \"Commodore Failure of the HMS Failtastrophe\".";
                    break;
                case 2:
                    msg = " just failed so hard he won.";
                    break;
                case 3:                    
                    msg = " has failed so much he should probably just stop trying.";
                    break;
                case 4:
                    msg = " is the conductor of the fail train. Choo choo.";
                    break;
                default:
                    msg = " has failed.";
                    break;
            }
        } else {
            msg += " ";
        
            msg += messages[gen.nextInt(messages.length)];
        }
        
        return msg;
    }
    
    private Player sGetPlayer(String name) {
        Player bestMatch = null;
        Server server = getServer();
        
        
        Player[] onlines = server.getOnlinePlayers();
        
        for(Player p : onlines) {
            if(p.getName().startsWith(name)) {
                return p;
            }
            
        }
        
        
        
        
        
        return null;
    }
    
    private void setupPermissions() {
        if (permissionHandler != null) {
            return;
        }



        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (permissionsPlugin == null) {
            Logger.getLogger("Minecraft").log(Level.WARNING, "Permissions hook-in failed for Fail Announce.");
            return;
        }

        permissionHandler = ((Permissions) permissionsPlugin).getHandler();
        Logger.getLogger("Minecraft").info("FailAnnounce: Permissions hook-in successful.");
    }
    
    
    
    
    
}
