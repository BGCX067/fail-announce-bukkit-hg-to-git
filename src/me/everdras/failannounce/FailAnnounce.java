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
    public static final File failpath = new File("plugins" + File.separator + "FailAnnounce" + File.separator + "Fail_Messages.txt");
    public static final File dir = new File("plugins" + File.separator + "FailAnnounce" + File.separator);
    public static final File winpath = new File("plugins" + File.separator + "FailAnnounce" + File.separator + "Win_Messages.txt");
    
    private final Player[] indicators = new Player[2];
    private final long[] indicateTime = new long[2];
    
    private PermissionHandler permissionHandler;
    
    private String[] failmessages, winmessages;
    private boolean custFail, custWin, ignoreDefaults;
    

    @Override
    public void onDisable() {
        Logger.getLogger("Minecraft").info("FailAnnounce unloaded.");
    }

    @Override
    public void onEnable() {
        setupPermissions();
        
        checkFiles();
        
        composeMessages();
        
        Logger.getLogger("Minecraft").info("FailAnnounce loaded.");
                
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Logger.getLogger("Minecraft").info("FailAnnounce: Cannot call this command from the console.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(shouldBeThrottled(player)) {
            if(permissionHandler != null ? !permissionHandler.has(player, "FailAnnounce.unlimited") : !player.isOp()) {
                    
                        sender.sendMessage("Chill out, Eager Beaver. You've indicated too many fails/wins recently!");
                        return true;
                    }
        }
        
        
        String pName = ((Player) sender).getName();
        
        
        
        if(label.equals("fail")) {
            if(args.length == 0) {
                getServer().broadcastMessage(ChatColor.DARK_RED + pName + getMessage(MessageType.FAIL));
                logIndication(player, System.currentTimeMillis());
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



               getServer().broadcastMessage(ChatColor.DARK_RED + sGetPlayer(args[0]).getName() + getMessage(MessageType.FAIL)); 
               logIndication(player, System.currentTimeMillis());
               return true;
            }
        }
        else if(label.equals("win")) {
            if(args.length == 0) {
                sender.sendMessage("Humility is a good quality!");
                return true;


            }

            else if(args.length == 1){
//                if(permissionHandler != null ? !permissionHandler.has(player, "FailAnnounce.other") : !player.isOp()) {
//                    sender.sendMessage("Insufficient permission.");
//                    return true;
//                }

                if(sGetPlayer(args[0]) == null) {
                   sender.sendMessage("That player is not online.");
                   return true;
               }

               getServer().broadcastMessage(ChatColor.DARK_GREEN + sGetPlayer(args[0]).getName() + getMessage(MessageType.WIN)); 
               logIndication(player, System.currentTimeMillis());
               return true;
            }
        }
        
        else if(label.equals("fa")) {
            if(permissionHandler == null ? !player.isOp() : !permissionHandler.has(player, "FailAnnounce.options")) {
                sender.sendMessage("Insufficient permissions.");
                return true;
            }
            if(args.length < 1) {
                sender.sendMessage("/fa ignoreDefaults true/false");
                return true;
            }
            
            if(args[1].equalsIgnoreCase("ignoreDefaults")) {
                if(args.length >= 2) {
                    if(args[1].equalsIgnoreCase("true")) {
                        ignoreDefaults = true;
                        sender.sendMessage("Now ignoring default messages.");
                        
                    }
                    else {
                        ignoreDefaults = false;
                        sender.sendMessage("Not ignoring default messages.");
                        
                    }
                    return true;
                }
                else {
                    sender.sendMessage("/fa ignoreDefaults true/false");
                    return true;
                }
            }
            
        }
        
        return false;
    }
    
    private String getMessage(MessageType type) {
        Random gen = new Random();
        
        
        if(ignoreDefaults) {
            if(winmessages.length == 0 || failmessages.length == 0) {
                Logger.getLogger("Minecraft").log(Level.WARNING, "FailAnnounce: Defaults are turned off, yet custom messages are not defined for either fail or win, or both! Problem!");
                return "";
            }
            
            switch(type) {
                case WIN:
                    return " " + winmessages[gen.nextInt(winmessages.length)];
                    
                case FAIL:
                    return " " + failmessages[gen.nextInt(failmessages.length)];
                default:
                    return "";
            }
        }
        else {
            int i = gen.nextInt(25);
            switch(type) {
                case WIN:
                    if(custWin && i < 12)
                        return " " + winmessages[gen.nextInt(winmessages.length)];
                    else {
                        switch(i) {
                            case 13: return " is the winner of a fine new chicken dinner. (Chicken dinner not included.)";
                            case 14: return " wins like George W. Bush. Not so great on the popular vote, but a bomb in the electoral college!";
                            case 15: return " is the meaning of life, the universe, and everything.";
                            case 16: return " is da bomb.";
                            case 17: return " has earned a place in Valhalla for his deeds.";
                                
                            default:
                                return " won the internet.";
                        }
                    }
                    
                case FAIL:
                    if(custFail && i < 12)
                        return " " + failmessages[gen.nextInt(failmessages.length)];
                    else {
                        switch(i) {
                            case 13: return " has apocalyptically failed.";
                            case 14: return " shall be forever known as \"Commodore Failure of the HMS Failtastrophe\".";
                            case 15: return " just failed so hard he won.";
                            case 16: return " has failed so much he should probably just stop trying.";
                            case 17: return " is the conductor of the fail train. Choo choo.";
                            
                            default:
                                return " has failed.";
                        }
                    }
                default:
                    return ", something kooky happened internally! Alert the plugin developer, Everdras!";
            }
            
        }
        
        
        
        
        
        
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
    
    private void checkFiles() {
        if(!dir.exists()) {
            dir.mkdir();
        }
        
        //check to make sure failmessages file exists
        if(!failpath.exists()) {
            try {
                failpath.createNewFile();
                //Logger.getLogger("Minecraft").info("Fail Announce: File created at: " + path.getAbsolutePath());
            } catch (Exception ex) {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "Unable to create file.");
            }
        }
        
        //check to make sure win messages file exists
        if(!winpath.exists()) {
            try {
                winpath.createNewFile();
                //Logger.getLogger("Minecraft").info("Fail Announce: File created at: " + path.getAbsolutePath());
            } catch (Exception ex) {
                Logger.getLogger("Minecraft").log(Level.SEVERE, "Unable to create file.");
            }
        }
    }
    
    private void composeMessages() {
        Scanner scan;
        //make fail messages
        try {
            scan = new Scanner(failpath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, "Error reading file.");
            scan = null;
        }
        
        LinkedList<String> temp = new LinkedList<String>();
        while(scan.hasNextLine()) {
            temp.add(scan.nextLine());
        }
        
        failmessages = new String[temp.size()];
        
        for(int i = 0; i < failmessages.length; i++) {
            failmessages[i] = temp.get(i);
        }
        
        scan.close();
        
        //make win messages
        try {
            scan = new Scanner(winpath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, "Error reading file.");
            scan = null;
        }
        
        temp = new LinkedList<String>();
        while(scan.hasNextLine()) {
            temp.add(scan.nextLine());
        }
        
        winmessages = new String[temp.size()];
        
        for(int i = 0; i < winmessages.length; i++) {
            winmessages[i] = temp.get(i);
        }
        
        
        
        Logger.getLogger("Minecraft").info("FailAnnounce: done loading lists.");
        
        if(failmessages.length == 0) {
            custFail = false;
        }
        else
            custFail = true;
        
        if(winmessages.length == 0) {
            custWin = false;
        }
        else
            custWin = true;
        
        
        scan.close();
    }
    
    private boolean shouldBeThrottled(Player p) {
        //if at least two indications are on record and they are by the same person
        if((indicators[0] != null && indicators[1] != null) && (indicators[0].equals(indicators[1]))) {
            //and it's been less than 30 seconds since the last fail and it's the same person
            if((System.currentTimeMillis() - indicateTime[0] < 30000) && p.equals(indicators[0]))
                //THROTTLE THAT MOFO
                return true;
        }
        
        
        //otherwise don't.
        return false;
    }
    
    private void logIndication(Player p, long time) {
        indicators[1] = indicators[0];
        indicators[0] = p;
        
        indicateTime[1] = indicateTime[0];
        indicateTime[0] = time;
    }
    
    public enum MessageType {
        FAIL,WIN;
    }
    
    
    
    
    
}
