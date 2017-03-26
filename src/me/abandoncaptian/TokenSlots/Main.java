package me.abandoncaptian.TokenSlots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Listener {

    Logger Log = Bukkit.getLogger();
    public static Economy econ = null;
    File configFile;
    FileConfiguration config;
    MacUtil mu;
    EcoMan em;
    SignMan sm;
    int maxBet;
    double winTax;
    boolean wild;
    int wildReward;
    boolean spinSound;
    int soundVol;
    ItemStack filler;
    String tempVal;
    String[] tempValSplit;
    List < Material > block = new ArrayList < Material > ();
    List < Integer > chance = new ArrayList < Integer > ();
    List < Integer > reward = new ArrayList < Integer > ();
    int conItemsSize = 1;
    boolean check;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            Log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Log.info("---------- [ Token Slots ] ----------");
        Log.info(" ");
        Log.info("               Enabled!              ");
        Log.info(" ");
        Log.info("-------------------------------------");
        this.configFile = new File("plugins/TokenSlots/config.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.check = true;
        if (!(configFile.exists())) {
            config.options().copyDefaults(true);
            this.saveDefaultConfig();
            this.saveConfig();
            Log.info("File Didn't Exist ----");
        }
        while (this.check) {
            if ((config.getString("Items." + this.conItemsSize + ".Item") != null) && (config.getString("Items." + this.conItemsSize + ".Chance") != null) && (config.getString("Items." + this.conItemsSize + ".Reward") != null)) {
                this.conItemsSize++;
            } else {
                this.check = false;
            }
        }
        for (int i = 1; i <= (this.conItemsSize - 1); i++) {
            this.block.add(Material.matchMaterial(config.getString("Items." + i + ".Item")));
            this.chance.add(config.getInt("Items." + i + ".Chance"));
            this.reward.add(config.getInt("Items." + i + ".Reward"));
        }
        this.maxBet = config.getInt("MaxBet");
        this.winTax = config.getDouble("WinTax");
        this.wild = config.getBoolean("WildCard");
        this.wildReward = config.getInt("WildCardReward");
        this.spinSound = config.getBoolean("SpinSound");
        this.soundVol = config.getInt("SoundVolume");
        this.tempVal = config.getString("SpaceFiller");
        this.tempValSplit = tempVal.split("/");
        Log.info("Split Temp Val: " + tempValSplit);
        this.filler = new ItemStack(Material.matchMaterial(this.tempValSplit[0]), 1, (short) Short.valueOf(this.tempValSplit[1]));
        this.mu = new MacUtil(this);
        this.em = new EcoMan(this);
        this.sm = new SignMan(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(mu, this);
        Bukkit.getPluginManager().registerEvents(sm, this);
    }

    @Override
    public void onDisable() {
        Log.info("---------- [ Token Slots ] ----------");
        Log.info(" ");
        Log.info("              Disabled!              ");
        Log.info(" ");
        Log.info("-------------------------------------");
        this.block.clear();
        this.chance.clear();
        this.reward.clear();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider < Economy > rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    @EventHandler
    public void invManagment(InventoryClickEvent e) {
        if (e.getInventory().getName().startsWith("�b�lToken Slots")) {
            e.setCancelled(true);
        }
    }

    public boolean onCommand(CommandSender theSender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("tokenslots")) {
            if (args.length == 0) {
                theSender.sendMessage("�cInvalid Arguments!");
                theSender.sendMessage("�9Usage: �b/tokenslots reload or settings");
                return true;
            } else if (args[0].equalsIgnoreCase("reload") && (theSender.hasPermission("tokenslots.reload") || theSender.hasPermission("tokenslots.*"))) {
                if (args.length == 1) {
                    onDisable();
                    Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                        @Override
                        public void run() {
                            onEnable();
                            theSender.sendMessage("�aReload Successful!");
                        }
                    }, 10);
                    return true;
                } else if (args.length > 1) {
                    theSender.sendMessage("�cToo Many Arguments!");
                    theSender.sendMessage("�9Usage: �b/tokenslots reload");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("settings") && (theSender.hasPermission("tokenslots.settings") || theSender.hasPermission("tokenslots.*"))) {
                if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("maxbet")) {
                        try {
                            Integer.valueOf(args[2]);
                        } catch (NumberFormatException ex) {
                            theSender.sendMessage("�cThat is not a number!");
                            return true;
                        }
                        int val = Integer.valueOf(args[2]);
                        config.set("MaxBet", val);
                        try {
                            this.config.save(configFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.maxBet = val;
                        theSender.sendMessage("�9[�6�lTokenSlots�9] �6MaxBet has been set to " + val);
                        return true;
                    } else if (args[1].equalsIgnoreCase("wintax")) {
                        try {
                            Double.valueOf(args[2]);
                        } catch (NumberFormatException ex) {
                            theSender.sendMessage("�cThat is not a decimal!");
                            return true;
                        }
                        double val = Double.valueOf(args[2]);
                        if (val >= 1) {
                            theSender.sendMessage("�cThat is not a decimal!");
                        }
                        config.set("WinTax", val);
                        try {
                            this.config.save(configFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.winTax = val;
                        theSender.sendMessage("�9[�6�lTokenSlots�9] �6WinTax has been set to " + val);
                        return true;
                    } else if (args[1].equalsIgnoreCase("wildcard")) {
                        try {
                            Boolean.valueOf(args[2].toLowerCase());
                        } catch (NumberFormatException ex) {
                            theSender.sendMessage("�cThat is not true or false!");
                            return true;
                        }
                        boolean val = Boolean.valueOf(args[2].toLowerCase());
                        config.set("WildCard", val);
                        try {
                            this.config.save(configFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.wild = val;
                        theSender.sendMessage("�9[�6�lTokenSlots�9] �6WildCard has been set to " + val);
                        return true;
                    } else if (args[1].equalsIgnoreCase("spinsound")) {
                        try {
                            Boolean.valueOf(args[2].toLowerCase());
                        } catch (NumberFormatException ex) {
                            theSender.sendMessage("�cThat is not true or false!");
                            return true;
                        }
                        boolean val = Boolean.valueOf(args[2].toLowerCase());
                        config.set("SpinSound", val);
                        try {
                            this.config.save(configFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.spinSound = val;
                        theSender.sendMessage("�9[�6�lTokenSlots�9] �6SpinSound has been set to " + val);
                        return true;
                    } else if (args[1].equalsIgnoreCase("soundvolume") || args[1].equalsIgnoreCase("soundvolume")) {
                        try {
                            Integer.valueOf(args[2]);
                        } catch (NumberFormatException ex) {
                            theSender.sendMessage("�cThat is not a number!");
                            return true;
                        }
                        int val = Integer.valueOf(args[2]);
                        config.set("SoundVolume", val);
                        try {
                            this.config.save(configFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        this.soundVol = val;
                        theSender.sendMessage("�9[�6�lTokenSlots�9] �6Sound Volume has been set to " + val);
                        return true;
                    } else {
                        theSender.sendMessage("�9Settings:");
                        theSender.sendMessage("�b/tokenslots settings MaxBet <NUMBER>");
                        theSender.sendMessage("�b/tokenslots settings WinTax <DECIMAL>");
                        theSender.sendMessage("�b/tokenslots settings WildCard <true/false>");
                        theSender.sendMessage("�b/tokenslots settings SpinSound <true/false>");
                        theSender.sendMessage("�b/tokenslots settings SoundVolume <NUMBER>");
                        return true;
                    }
                } else if (args.length == 1) {
                    theSender.sendMessage("�9Usage: �b/tokenslots settings <Setting> <Value>");
                    theSender.sendMessage("�9Settings:");
                    theSender.sendMessage("�b/tokenslots settings MaxBet <NUMBER>");
                    theSender.sendMessage("�b/tokenslots settings WinTax <DECIMAL>");
                    theSender.sendMessage("�b/tokenslots settings WildCard <true/false>");
                    theSender.sendMessage("�b/tokenslots settings SpinSound <true/false>");
                    theSender.sendMessage("�b/tokenslots settings SoundVolume <NUMBER>");
                    return true;
                } else if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("maxbet")) theSender.sendMessage("�b/tokenslots settings MaxBet <NUMBER>");
                    else if (args[1].equalsIgnoreCase("wintax")) theSender.sendMessage("�b/tokenslots settings WinTax <DECIMAL>");
                    else if (args[1].equalsIgnoreCase("wildcard")) theSender.sendMessage("�b/tokenslots settings WildCard <true/false>");
                    else if (args[1].equalsIgnoreCase("spinsound")) theSender.sendMessage("�b/tokenslots settings SpinSound <true/false>");
                    else if (args[1].equalsIgnoreCase("soundvolume")) theSender.sendMessage("�b/tokenslots settings SoundVolume <NUMBER>");
                    else {
                        theSender.sendMessage("�9Settings:");
                        theSender.sendMessage("�b/tokenslots settings MaxBet <NUMBER>");
                        theSender.sendMessage("�b/tokenslots settings WinTax <DECIMAL>");
                        theSender.sendMessage("�b/tokenslots settings WildCard <true/false>");
                        theSender.sendMessage("�b/tokenslots settings SpinSound <true/false>");
                        theSender.sendMessage("�b/tokenslots settings SoundVolume <NUMBER>");
                    }
                    return true;
                } else if (args.length > 3) {
                    theSender.sendMessage("�cToo Many Arguments!");
                    theSender.sendMessage("�9Usage: �b/tokenslots settings <Setting> <Value>");
                    return true;
                }
            } else {
                theSender.sendMessage("�cInvalid Arguments!");
                theSender.sendMessage("�9Usage: �b/tokenslots reload or settings");
                return true;
            }
        }
        if (commandLabel.equalsIgnoreCase("slots") && (theSender instanceof Player)) {
            Player p = (Player) theSender;
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("max")) {
                    if (em.MoneyCheck(p, maxBet)) {
                        mu.runMachine(p, maxBet);
                        em.removeMoney(p, maxBet);
                        return true;
                    } else {
                        p.sendMessage("�cYou need $" + maxBet + " to do a max bet");
                        return true;
                    }
                }
                try {
                    Integer.valueOf(args[0]);
                } catch (NumberFormatException ex) {
                    p.sendMessage("�cThat is not a number or the word max!");
                    return true;
                }
                if (Integer.parseInt(args[0]) <= maxBet) {
                    if (em.MoneyCheck(p, Integer.parseInt(args[0]))) {
                        if (Integer.parseInt(args[0]) >= 1) {
                            mu.runMachine(p, Integer.parseInt(args[0]));
                            em.removeMoney(p, Integer.parseInt(args[0]));
                            return true;
                        } else {
                            p.sendMessage("�cNow what do you think �6$0 �cwill win you???");
                            return true;
                        }
                    } else {
                        p.sendMessage("�cInsufficient Funds!");
                        return true;
                    }
                } else {
                    p.sendMessage("�cThat bet is too high! �bMax Bet: " + maxBet);
                    return true;
                }

            } else if (args.length > 1) {
                p.sendMessage("�cToo Many Arguments");
                p.sendMessage("�9Usage: �b/slots <amount> �9or �b/slots max");
                return true;
            } else {
                p.sendMessage("�cToo Few Arguments");
                p.sendMessage("�9Usage: �b/slots <amount> �9or �b/slots max");
                return true;
            }
        }
        return true;
    }
}