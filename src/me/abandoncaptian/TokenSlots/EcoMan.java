package me.abandoncaptian.TokenSlots;

import org.bukkit.entity.Player;

public class EcoMan {
    Main pl;
    int winning = 0;
    public EcoMan(Main plugin) {
        this.pl = plugin;
    }

    @SuppressWarnings("deprecation")
    public void rewardMoney(Player p, double amount) {
        Main.econ.depositPlayer(p.getName(), amount);
        p.sendMessage("�9[�6�lTokenSlots�9] �bYou Won �6$" + amount);
        return;
    }

    @SuppressWarnings("deprecation")
    public void removeMoney(Player p, int amount) {
        Main.econ.withdrawPlayer(p.getName(), amount);
    }

    @SuppressWarnings("deprecation")
    public boolean MoneyCheck(Player p, int amount) {
        if (Main.econ.getBalance(p.getName()) >= amount) return true;
        else return false;
    }
}