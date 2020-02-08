package realmayus.youmatter;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

@Config(modid = YouMatter.MODID)
public class YMConfig {


    @Config.Comment(value = "Use specialItems as black (true) or as whitelist (false). Whitelist means, that you can only duplicate those items in that list. Blacklist is vice-versa.")
    public static boolean useAsBlacklist = false;

    @Config.Comment(value = "List of items that are being treated specially. See useAsBlacklist for further details.")
    public static String[] itemList = new String[] {"minecraft:dragon_egg"};

    @Config.Comment(value = "Overrides: Set your desired required U-Matter values for each item. These do not apply when you e.g. have whitelist on but it doesn't include the desired override.")
    public static HashMap<String, Integer> overrides = new HashMap<String, Integer>() {{
        put("minecraft:diamond", 2500);
        put("minecraft:nether_star", 5000);
    }};

    @Config.Comment(value = "How much U-Matter (in mB) should you need for one item to be duplicated (that is not overridden)")
    public static int uMatterPerItem = 1000;


}
