package me.drmayx.voxelexpbottler.utils;

import org.bukkit.entity.Player;

public class ExpUtils {

    public static int getUserExp(Player player){

        int playerLevel = player.getLevel();

        int xpat = getExpAtLevel(playerLevel);
        int reqxp = getExpRequiredToLevelUpFromLevel(playerLevel);
        float calcreq = reqxp * player.getExp();

        return (int) (xpat + (calcreq));
    }

    private static int getExpAtLevel(int level){

        if(level <= 16){
            return (level * level) + (6 * level);
        }
        else if(level > 16 && level <= 31){
            return (int) (2.5 * (level * level) - (40.5 * level) + 360);
        }
        else if(level > 31){
            return (int)(4.5 * (level *  level) - (162.5 * level) + 2220);
        }

        return -1;
    }

    private static int getExpRequiredToLevelUpFromLevel(int level){

        if(level <= 15){
            return (2 * level) + 7;
        }
        else if(level > 15 && level <= 30){
            return (5 * level) - 38;
        }
        else if(level > 30){
            return (9 * level) - 158;
        }

        return -1;
    }
}
