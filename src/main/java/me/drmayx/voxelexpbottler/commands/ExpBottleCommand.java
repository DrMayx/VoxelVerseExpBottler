package me.drmayx.voxelexpbottler.commands;

import me.drmayx.voxelexpbottler.utils.ExpUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;

public class ExpBottleCommand implements CommandExecutor {

    private String helpMessage = "Contact admin";
    private String bottleTitle = "&b&lExp Bottle&r";
    private List<String> loreData = new ArrayList<>();
    private Integer defaultXp = 420;

    public ExpBottleCommand(YamlConfiguration config){
        String newlineIndicator = config.getString("newline_indicator");
        String helpMessage = config.getString("help_message");
        if(newlineIndicator != null) {
            helpMessage = helpMessage.replace(newlineIndicator, "\n");
        }

        String title = config.getString("bottle_info.title");
        if(title != null){
            this.bottleTitle = title;
        }

        List<String> lore = config.getStringList("bottle_info.lore");
        if(lore != null){
            this.loreData = lore;
        }

        if(helpMessage != null){
            this.helpMessage = helpMessage;
        }

        int defaultValue = config.getInt("bottle_info.default_xp_value", -1);
        if(defaultValue > 0){
            this.defaultXp = defaultValue;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("bottle")) {

            if (args.length > 0) {
                handleArgumentedCommand(sender, args);
            } else {
                handleUnArgumentedCommand(sender);
            }

        }else{
                sender.sendMessage("Invalid command");
        }
        return true;
    }

    private boolean handleAdminCommands(String arg, CommandSender sender){

        switch(arg){
            case "help":
            case "?":
                displayHelp(sender);
                return true;
            case "reload":
                if(sender.hasPermission("expbottler.admin")) {
                    sender.sendMessage("Reloading the plugin ...");
                    System.out.println("Reloading the plugin. Caused by " + sender.getName());
                    Bukkit.getPluginManager().disablePlugin(Bukkit.getServer().getPluginManager().getPlugin("VoxelVerseExpBottler"));
                    Bukkit.getPluginManager().enablePlugin(Bukkit.getServer().getPluginManager().getPlugin("VoxelVerseExpBottler"));
                    sender.sendMessage("Reload complete.");
                }else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Insufficient permissions!&r"));
                }
                return true;
            default:
                return false;
        }
    }

    private ItemStack prepareXpBottle(String amount){
        ItemStack xpBottleItem = new ItemStack(Material.POTION);
        PotionMeta bottleMeta = (PotionMeta) xpBottleItem.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        if(this.loreData.size() > 0){
            for(String data : this.loreData){
                lore.add(ChatColor.translateAlternateColorCodes('&', data));
            }
        }

        lore.add(ChatColor.translateAlternateColorCodes('&', String.format("&7&oContains %s Exp&r", amount)));
        bottleMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.bottleTitle.replace("{amount}", amount)));
        bottleMeta.setLore(lore);
        bottleMeta.setColor(Color.GREEN);
        bottleMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES);

        xpBottleItem.setItemMeta(bottleMeta);

        return xpBottleItem;
    }

    private void handleBottling(Player player, Integer amount ){
        if(amount > 0) {
            float playerXp = ExpUtils.getUserExp(player);
            if (playerXp >= amount) {
                PlayerInventory playerInv = player.getInventory();
                if (playerInv.getItemInMainHand().getAmount() > 1) {
                    playerInv.getItemInMainHand().setAmount(playerInv.getItemInMainHand().getAmount() - 1);
                } else {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }

                String xpCommand = String.format("xp set %s %s ", player.getName(), (int) (playerXp - amount));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), xpCommand);

                Material mainHandType = player.getInventory().getItemInMainHand().getType();
                if(mainHandType.equals(Material.AIR) || mainHandType.equals(null)){
                    player.getInventory().setItemInMainHand(prepareXpBottle(amount.toString()));
                }else{
                    player.getWorld().dropItem(player.getLocation(), prepareXpBottle(amount.toString()));
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You dont have enough xp to store. " +
                        "( Needed at least " + amount + " xp)"));
            }
        }else{
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Exp amount has to be above 0!&r"));
        }
    }

    private void displayHelp(CommandSender sender){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',helpMessage));
    }


    private void handleArgumentedCommand(CommandSender sender, String[] args) {

        boolean isAdminCommand = handleAdminCommands(args[0], sender);
        boolean isPlayerCommand = !isAdminCommand;

        if(isAdminCommand){
            return;
        }

        if(sender instanceof Player) {
            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("check")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        String.format("Your current xp is: &2%s&r", ExpUtils.getUserExp(player))));
            }else {
                try {
                    int exp = Integer.parseInt(args[0]);
                    if (player.getInventory().getItemInMainHand().getType().equals(Material.GLASS_BOTTLE)) {
                        handleBottling(player, exp);
                    }else{
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&4You have to be holding an empty glass bottle in your main hand.&r"));
                    }
                }catch(NumberFormatException e){
                    isPlayerCommand = false;
                }
            }

        }else{
            sender.sendMessage("The command can only be run by an in-game player");
        }

        if(!isPlayerCommand){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4Invalid command&r"));
        }
    }

    private void handleUnArgumentedCommand(CommandSender sender) {
        if(sender instanceof Player){
            Player player = (Player)sender;

            if (player.getInventory().getItemInMainHand().getType().equals(Material.GLASS_BOTTLE)) {
                handleBottling(player, this.defaultXp);
            }else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&4You have to be holding an empty glass bottle in your main hand.&r"));
            }
        }else{
            sender.sendMessage("The command can only be run by an in-game player");
        }
    }
}
