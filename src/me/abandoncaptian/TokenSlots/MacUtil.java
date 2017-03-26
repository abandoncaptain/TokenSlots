package me.abandoncaptian.TokenSlots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public class MacUtil implements Listener {
    Main pl;
    int winning = 0;
    int timing = (20 * 7);
    BukkitTask runningC1;
    BukkitTask runningC2;
    BukkitTask runningC3;
    List < String > coolDown = new ArrayList < String > ();
    HashMap < String, Inventory > active = new HashMap < String, Inventory > ();
    List < Material > choices = new ArrayList < Material > ();
    double numMax = 0;
    public MacUtil(Main plugin) {
        this.pl = plugin;
        this.choices.clear();
        for (int num = 0; num <= (pl.chance.size() - 1); num++) {
            this.numMax += pl.chance.get(num);
            for (int val = 1; val <= pl.chance.get(num); val++) {
                this.choices.add(pl.block.get(num));
            }
        }
        if (pl.wild) {
            numMax++;
            this.choices.add(Material.PAPER);
        }
    }
    public void runMachine(Player p, int price) {
        if (coolDown.contains(p.getName())) {
            p.sendMessage("�9[�6�lTokenSlots�9] �cYour cool down is active please wait!");
        } else {
            active.put(p.getName(), Bukkit.createInventory(p, 45, "�b�lToken Slots �7�l- �6�l$" + price));
            coolDown.add(p.getName());
            ItemStack holder = pl.filler;
            for (int num = 0; num <= 45; num++) {
                if (num < 11) {
                    active.get(p.getName()).setItem(num, holder);
                } else if (num == 12) {
                    active.get(p.getName()).setItem(num, holder);
                } else if (num == 14) {
                    active.get(p.getName()).setItem(num, holder);
                } else if (num > 15 && num < 20) {
                    active.get(p.getName()).setItem(num, holder);
                } else if (num == 21) {
                    active.get(p.getName()).setItem(num, holder);
                } else if (num == 23) {
                    active.get(p.getName()).setItem(num, holder);
                } else if (num > 24 && num < 29) {
                    active.get(p.getName()).setItem(num, holder);
                } else if (num == 30) {
                    active.get(p.getName()).setItem(num, holder);
                } else if (num == 32) {
                    active.get(p.getName()).setItem(num, holder);
                } else if (num > 33 && num < 45) {
                    active.get(p.getName()).setItem(num, holder);
                }
            }
            p.openInventory(active.get(p.getName()));
            runningC1 = Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {

                @Override
                public void run() {
                    if (active.containsKey(p.getName())) {
                        active.get(p.getName()).setItem(11, randomItem());
                        active.get(p.getName()).setItem(20, randomItem());
                        active.get(p.getName()).setItem(29, randomItem());
                    }
                }
            }, 0, 2);
            runningC2 = Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {

                @Override
                public void run() {
                    if (active.containsKey(p.getName())) {
                        active.get(p.getName()).setItem(13, randomItem());
                        active.get(p.getName()).setItem(22, randomItem());
                        active.get(p.getName()).setItem(31, randomItem());
                    }
                }
            }, 0, 2);
            runningC3 = Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {

                @Override
                public void run() {
                    if (active.containsKey(p.getName())) {
                        active.get(p.getName()).setItem(15, randomItem());
                        active.get(p.getName()).setItem(24, randomItem());
                        active.get(p.getName()).setItem(33, randomItem());
                        if (pl.spinSound == true) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_SNARE, (float) pl.soundVol, 1);
                    }
                }
            }, 0, 2);

            Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                @Override
                public void run() {
                    if (active.containsKey(p.getName())) {
                        runningC1.cancel();
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_SNARE, (float) 10, 1);
                    }
                }
            }, (timing - (20 * 4)));

            Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                @Override
                public void run() {
                    if (active.containsKey(p.getName())) {
                        runningC2.cancel();
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_SNARE, (float) 10, 1);
                        winning = winCheck1(active.get(p.getName()).getItem(20), active.get(p.getName()).getItem(22), p);
                        if (winning == 2) {
                            active.get(p.getName()).setItem(18, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
                            active.get(p.getName()).setItem(19, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
                            active.get(p.getName()).setItem(21, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
                        }
                    }
                }
            }, (timing - (20 * 2)));

            Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
                @Override
                public void run() {
                    if (active.containsKey(p.getName())) {
                        runningC3.cancel();
                        if (winning == 2) {
                            int prize;
                            winning = winCheck2(active.get(p.getName()).getItem(20), active.get(p.getName()).getItem(22), active.get(p.getName()).getItem(24), p);
                            if (winning == 3) {
                                active.get(p.getName()).setItem(23, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
                                active.get(p.getName()).setItem(25, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
                                active.get(p.getName()).setItem(26, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
                            }
                            p.sendMessage("�9[�6�lTokenSlots�9] �b" + winning + " �9connected!");
                            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, (float) 10, 1);
                            if (active.get(p.getName()).getItem(20).getType() != Material.PAPER) prize = winningCal(active.get(p.getName()).getItem(20), winning, p, price);
                            else if (active.get(p.getName()).getItem(22).getType() != Material.PAPER) prize = winningCal(active.get(p.getName()).getItem(22), winning, p, price);
                            else if (active.get(p.getName()).getItem(24).getType() != Material.PAPER) prize = winningCal(active.get(p.getName()).getItem(22), winning, p, price);
                            else prize = winningCal(active.get(p.getName()).getItem(20), winning, p, price);

                            if (prize < 1) prize = 1;
                            double taxes = prize * pl.winTax;
                            Bukkit.getServer().broadcastMessage("�9[�6�lTokenSlots�9] �6" + p.getName() + " �bhas won �6$" + (prize - taxes));
                            Location loc = p.getLocation();
                            World world = loc.getWorld();
                            world.spawn(loc, Firework.class);
                            pl.em.rewardMoney(p, (prize - taxes));
                        } else {
                            p.sendMessage("�9[�6�lTokenSlots�9] �cYou Lost");
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, (float) 10, 1);
                        }
                    }
                }
            }, timing);

            Bukkit.getScheduler().runTaskLater(pl, new Runnable() {

                @Override
                public void run() {
                    if (active.get(p.getName()).getTitle().startsWith("�b�lToken Slots �7�l- �6�l$")) {
                        p.closeInventory();
                    }
                    if (active.containsKey(p.getName())) active.remove(p.getName());
                    coolDown.remove(p.getName());
                    p.sendMessage("�9[�6�lTokenSlots�9] �6Cool Down Finished! You can now do /slots!");
                }
            }, (timing + (20 * 2)));
        }

    }

    public ItemStack randomItem() {
        int numGen = (int)(Math.random() * numMax);
        ItemStack item = new ItemStack(this.choices.get(numGen), 1);
        if (item.getType() == Material.PAPER) {
            ItemMeta im = item.getItemMeta();
            im.setDisplayName("�bWild");
            item.setItemMeta(im);
            return item;
        } else {
            return item;
        }
    }

    public int winCheck1(ItemStack i1, ItemStack i2, Player p) {
        if ((i1.getType() == i2.getType()) || ((i2.getType() == Material.PAPER) || (i1.getType() == Material.PAPER))) {
            return 2;
        } else {
            return 0;
        }

    }
    public int winCheck2(ItemStack i1, ItemStack i2, ItemStack i3, Player p) {
        if (i1.getType() == Material.PAPER) {
            if (i2.getType() == Material.PAPER) {
                if (i3.getType() == Material.PAPER) {
                    return 3;
                } else {
                    return 3;
                }
            } else if (i2.getType() == i3.getType()) {
                return 3;
            } else if (i3.getType() == Material.PAPER) {
                return 3;
            } else {
                return 2;
            }
        } else if (i2.getType() == Material.PAPER) {
            if (i1.getType() == i3.getType()) {
                return 3;
            } else {
                return 2;
            }
        } else if (i3.getType() == Material.PAPER) {
            if (i1.getType() == i2.getType()) {
                return 3;
            } else {
                return 2;
            }
        } else if (i2.getType() == i3.getType()) {
            return 3;
        } else {
            return 2;
        }
    }

    public int winningCal(ItemStack i1, int amount, Player p, int price) {
        amount--;
        int index = pl.block.indexOf(i1.getType());
        return (int)((pl.reward.get(index) * amount) * Math.floor(price * 0.5));

    }

    @EventHandler
    public void invClose(InventoryCloseEvent e) {
        if (active.containsKey(e.getPlayer().getName())) {
            if (e.getInventory().getName().startsWith("�b�lToken Slots")) {
                active.remove(e.getPlayer().getName());
                this.runningC1.cancel();
                this.runningC2.cancel();
                this.runningC3.cancel();
                e.getPlayer().sendMessage("�9[�6�lTokenSlots�9] �6You closed the gui before it finished!");
                e.getPlayer().sendMessage("�9[�6�lTokenSlots�9] �cYou Lost!");
            }
        }
    }


}