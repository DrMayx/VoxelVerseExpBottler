package me.drmayx.voxelexpbottler.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ExpListener implements Listener {

    @EventHandler
    public void onBottleUsed(PlayerItemConsumeEvent event){
        ItemStack xpbottle = event.getItem();
        if(xpbottle.getType().equals(Material.POTION)){
            try{
                List<String> loreData = xpbottle.getItemMeta().getLore();
                String expInformation = loreData.get(loreData.size() - 1);
                if(expInformation.contains("Contains")){
                    Player player = event.getPlayer();

                    String data = expInformation.split(" ")[1];
                    int exp = Integer.parseInt(data);

                    String xpCommand = String.format("xp give %s %s", player.getName(), exp);
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
