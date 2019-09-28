package me.drmayx.voxelexpbottler.voxelverseexpbottler;

import me.drmayx.voxelexpbottler.commands.ExpBottleCommand;
import me.drmayx.voxelexpbottler.listeners.ExpListener;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class VoxelVerseExpBottler extends JavaPlugin {

    private static String CONFIG_PATH = "plugins/VoxelVerseExpBottler/config.yml";
    private YamlConfiguration config;

    private ExpListener listenerObject = null;
    private ExpBottleCommand commandObject = null;

    @Override
    public void onEnable() {
        doConfig();

        listenerObject = new ExpListener();
        commandObject = new ExpBottleCommand(config);

        getServer().getPluginManager().registerEvents(listenerObject, this);
        getCommand("bottle").setExecutor(commandObject);

    }

    @Override
    public void onDisable() {
        commandObject = null;
        listenerObject = null;
    }

    private void doConfig(){
        File configFile = new File(CONFIG_PATH);

        if(!configFile.exists())
        {
            generateConfig();
            config = (YamlConfiguration)this.getConfig();
        }
        else{
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    private void generateConfig(){
        YamlConfiguration config = (YamlConfiguration) this.getConfig();
        config.options().header("To show amount of the exp in the bottle add '{amount}' to the title anywhere you want it.");
        config.set("newline_indicator", "&nl");
        config.set("help_message", "&4-------------- Voxel Verse Exp Bottler -----------------&r&nl" +
                                  "&6 YOU NEED TO BE HOLDING EMPTY GLASS BOTTLE TO USE /bottle&r&nl" +
                                  "&e - /bottle - bottles default amount of xp,&r&nl" +
                                  "&2 - /bottle help|? - shows this message,&r&nl" +
                                  "&b - /bottle check - shows your current xp,&r&nl" +
                                  "&d - /bottle <amount> - puts given xp amount into the bottle,&r&nl" +
                                  "&5-------------------- by DrMayX -----------------------&r");

        List<String> lore = new ArrayList<>();
        lore.add("Some custom lore");
        lore.add("&dEven more custom lore&r");

        config.set("bottle_info.title", "&b&lExp Bottle");
        config.set("bottle_info.default_xp_value", 69420);
        config.set("bottle_info.lore", lore);
        this.saveConfig();
    }
}
