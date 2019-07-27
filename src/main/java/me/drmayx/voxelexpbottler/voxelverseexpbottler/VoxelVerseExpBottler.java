package me.drmayx.voxelexpbottler.voxelverseexpbottler;

import me.drmayx.voxelexpbottler.commands.ExpBottleCommand;
import me.drmayx.voxelexpbottler.listeners.ExpListener;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class VoxelVerseExpBottler extends JavaPlugin {

    private static String CONFIG_PATH = "plugins/VoxelVerseExpBottler/config.yml";
    private YamlConfiguration config;

    private ExpListener listenerObject = null;
    private ExpBottleCommand commandObject = null;

    @Override
    public void onEnable() {
        doConfig();
        String newlineIndicator = config.getString("newline_indicator");
        String helpMessage = config.getString("help_message");
        if(newlineIndicator != null) {
            helpMessage = helpMessage.replace(newlineIndicator, "\n");
        }

        listenerObject = new ExpListener();
        commandObject = new ExpBottleCommand(helpMessage);//, essentialsPlugin);

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
        config.options().header("Not much to configure. Just set the help message.");
        config.set("newline_indicator", "&nl");
        config.set("help_message", "&4-------------- Voxel Verse Exp Bottler -----------------&r&nl" +
                                  "&6 YOU NEED TO BE HOLDING EMPTY GLASS BOTTLE TO USE /bottle&r&nl" +
                                  "&e - /bottle - bottles default amount of xp,&r&nl" +
                                  "&2 - /bottle help|? - shows this message,&r&nl" +
                                  "&b - /bottle check - shows your current xp,&r&nl" +
                                  "&d - /bottle <amount> - puts given xp amount into the bottle,&r&nl" +
                                  "&5-------------------- by DrMayX -----------------------&r");
        this.saveConfig();
    }
}
