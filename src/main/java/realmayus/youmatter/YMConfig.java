package realmayus.youmatter;

import net.minecraftforge.common.config.Config;

import java.util.HashMap;

@Config(modid = YouMatter.MODID)
public class YMConfig {


    @Config.Comment(value = "Use specialItems as black (true) or as whitelist (false). Whitelist means, that you can only duplicate those items in that list. Blacklist is vice-versa.")
    public static boolean useAsBlacklist = true;

    @Config.Comment(value = "List of items that are being treated specially. See useAsBlacklist for further details.")
    public static String[] itemList = new String[] {"minecraft:dragon_egg"};

    @Config.Comment(value = "Overrides: Set your desired required U-Matter values for each item. These do not apply when you e.g. have whitelist on but it doesn't include the desired override.")
    public static HashMap<String, Integer> overrides = new HashMap<String, Integer>() {{
        put("minecraft:diamond", 2500);
        put("minecraft:nether_star", 5000);
    }};

    @Config.Comment(value = "How much U-Matter (in mB) should you need for one item to be duplicated (that is not overridden)")
    public static int uMatterPerItem = 1000;

    @Config.Comment(value = "The energy consumption of the replicator per tick. Default: 2048")
    public static int energyReplicator = 2048;

    @Config.Comment(value = "The energy consumption of the encoder per tick. Default: 512")
    public static int energyEncoder = 512;

    @Config.Comment(value = "The energy consumption of the scanner per tick. Default: 512")
    public static int energyScanner = 512;

    @Config.Comment(value = "Determines how much U-Matter [in mB] the creator produces every work cycle. Energy is withdrawn like this: if energy more than 30% of max energy, consume 30% and add [whatever value below] of U-Matter to the tank. Default is 1mB/work cycle. Don't increase this too much due to balancing issues.")
    public static int productionPerWorkcycle = 1;
}
