package realmayus.youmatter;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

@Config(modid = YouMatter.MODID)
public class YMConfig {

//    @Config.Comment(value = "Thanks for using YouMatter. If you need any help with the mod, please don't hesitate to join my discord server: https://realmayus.xyz/discord")
//    public static boolean Hover_For_Info = true;
//
//    @Config.Comment(value = "Use specialItems as black (true) or as whitelist (false). Whitelist means, that you can only duplicate those items in that list. Blacklist is vice-versa.")
//    public static boolean useAsBlacklist = true;
//
//    @Config.Comment(value = "List of items that are being treated specially. See useAsBlacklist for further details.")
//    public static String[] specialItems = new String[] {"minecraft:iron_ingot"};

    @Config.Comment(value = "Overrides: Set your desired required U-Matter values for each item. Especially handy when used in conjunction with useAsBlacklist = false.")
    public static HashMap<String, Integer> overrides = new HashMap<String, Integer>() {{
        put("minecraft:diamond", 2500);
        put("minecraft:gold_ingot", 100);
    }};

    @Config.Comment(value = "How much U-Matter should you need for one item to be duplicated (that is not overridden)")
    public static int uMatterPerItem = 1000;


}
