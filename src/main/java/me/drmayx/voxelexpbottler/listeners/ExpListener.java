package me.drmayx.voxelexpbottler.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ExpListener implements Listener {

    @EventHandler
    public void onBottleUsed(PlayerItemConsumeEvent event){
        ItemStack xpbottle = event.getItem();
        if(xpbottle.getType().equals(Material.POTION)){
            try{
                if(xpbottle.getItemMeta().getLore().get(0).contains("xp in a bottle")){
                    Player player = event.getPlayer();

                    String data = xpbottle.getItemMeta().getLore().get(0).split(" ")[1];
                    System.out.println(data);
                    int exp = Integer.parseInt(data);

                    String xpCommand = String.format("xp give %s %s", player.getName(), exp);
                    System.out.println(xpCommand);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), xpCommand);

                    player.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE));
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    event.setCancelled(true);
                }
            }catch (NullPointerException e){
                //do nothing this is just some other potion
            }
            catch(Exception e){
                event.getPlayer().sendMessage("Error! Tell admin to read the log.");
                e.printStackTrace();
            }
        }
    }
}
