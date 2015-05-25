package com.walrusone.skywars.utilities;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
 
public class LoggerFilter implements Filter {
 
    @Override
    public boolean isLoggable(LogRecord arg0) {
    	if (arg0.getMessage().contains("Item Despawn Rate") || arg0.getMessage().contains("World Settings") || arg0.getMessage().contains("Allow Zombie Pigmen")
    			|| arg0.getMessage().contains("Item Merge Radius") || arg0.getMessage().contains("Chunks to Grow") || arg0.getMessage().contains("Clear tick list")
    			|| arg0.getMessage().contains("Experience Merge") || arg0.getMessage().contains("Max Entity Collisions") || arg0.getMessage().contains("Custom Map Seeds")
    			|| arg0.getMessage().contains("Arrow Despawn Rate") || arg0.getMessage().contains("Zombie Aggressive Towards") || arg0.getMessage().contains("View Distance")
    			|| arg0.getMessage().contains("Nerfing mobs spawned") || arg0.getMessage().contains("Anti X-Ray") || arg0.getMessage().contains("Engine Mode") 
    			|| arg0.getMessage().contains("Hidden Blocks") || arg0.getMessage().contains("Replace Blocks") || arg0.getMessage().contains("Cactus Growth Modifier") 
    			|| arg0.getMessage().contains("Cane Growth") || arg0.getMessage().contains("Melon Growth") || arg0.getMessage().contains("Mushroom Growth")
    			|| arg0.getMessage().contains("Pumpkin Growth") || arg0.getMessage().contains("Sapling Growth") || arg0.getMessage().contains("Wheat Growth")
    			|| arg0.getMessage().contains("Mob Spawn Range") || arg0.getMessage().contains("Entity Activation") || arg0.getMessage().contains("Entity Tracking")
    			|| arg0.getMessage().contains("Alternative Hopper") || arg0.getMessage().contains("Hopper Transfer") || arg0.getMessage().contains("Random Lighting")
    			|| arg0.getMessage().contains("Structure Info") || arg0.getMessage().contains("Sending up to") || arg0.getMessage().contains("Max TNT")
    			|| arg0.getMessage().contains("Preparing start region") || arg0.getMessage().contains("unloaded from memory") || arg0.getMessage().contains("removed from config")
    			|| arg0.getMessage().contains("was DELETED") || arg0.getMessage().contains("Loading World & Settings")) {
    		return false;
    	}
    	return true;
    }
 
}
