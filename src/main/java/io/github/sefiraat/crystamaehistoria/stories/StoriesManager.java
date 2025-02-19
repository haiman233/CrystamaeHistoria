package io.github.sefiraat.crystamaehistoria.stories;

import io.github.mooy1.infinitylib.configuration.AddonConfig;
import io.github.sefiraat.crystamaehistoria.CrystamaeHistoria;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StoriesManager {

    @Getter
    private final Map<Material, StoriedBlockDefinition> storiedBlockDefinitionMap = new EnumMap<>(Material.class);
    @Getter
    private final Map<Integer, BlockTier> blockTierMap = new HashMap<>();

    @Getter
    private final Map<Integer, Story> storyMapCommon = new HashMap<>();
    @Getter
    private final Map<Integer, Story> storyMapUncommon = new HashMap<>();
    @Getter
    private final Map<Integer, Story> storyMapRare = new HashMap<>();
    @Getter
    private final Map<Integer, Story> storyMapEpic = new HashMap<>();
    @Getter
    private final Map<Integer, Story> storyMapMythical = new HashMap<>();
    @Getter
    private final Map<Integer, Story> storyMapUnique = new HashMap<>();

    public StoriesManager() {
        fillBlockTierMap();
        fillStories();
        fillBlockDefinitions();
    }


    private void fillBlockTierMap() {
        blockTierMap.put(
                1,
                new BlockTier(
                        1,
                        900,
                        3,
                        1,
                        new StoryChances(
                                90,
                                10,
                                0,
                                0,
                                0
                        )
                )
        );
    }

    private void fillStories() {

        AddonConfig c = CrystamaeHistoria.inst().getConfig();

        ConfigurationSection common = c.getConfigurationSection("stories.common");
        ConfigurationSection uncommon = c.getConfigurationSection("stories.uncommon");
        ConfigurationSection rare = c.getConfigurationSection("stories.rare");
        ConfigurationSection epic = c.getConfigurationSection("stories.epic");
        ConfigurationSection mythical = c.getConfigurationSection("stories.mythical");
        ConfigurationSection unique = c.getConfigurationSection("stories.unique");

        Validate.notNull(common, "Common story configuration is not found, changed or deleted.");
        Validate.notNull(uncommon, "Uncommon story configuration is not found, changed or deleted.");
        Validate.notNull(rare, "Rare story configuration is not found, changed or deleted.");
        Validate.notNull(epic, "Epic story configuration is not found, changed or deleted.");
        Validate.notNull(mythical, "Mythical story configuration is not found, changed or deleted.");
        Validate.notNull(unique, "Unique story configuration is not found, changed or deleted.");

        fillMap(storyMapCommon, common, StoryRarity.Common);
        fillMap(storyMapUncommon, uncommon, StoryRarity.Uncommon);
        fillMap(storyMapRare, rare, StoryRarity.Rare);
        fillMap(storyMapEpic, epic, StoryRarity.Epic);
        fillMap(storyMapMythical, mythical, StoryRarity.Mythical);
        fillMap(storyMapUnique, unique, StoryRarity.Unique);

    }

    private void fillMap(Map<Integer, Story> map, ConfigurationSection section, StoryRarity rarity) {
        for (String s : section.getKeys(false)) {
            Story story = parseStory(section.getConfigurationSection(s), rarity);
            Validate.notNull(story, "wut ._.");
            map.put(Integer.parseInt(s), story);
        }
    }

    private static Story parseStory(ConfigurationSection sConf, StoryRarity rarity) {
        String name = sConf.getString("name");
        String type = sConf.getString("type");
        StoryType storyType = StoryType.valueOf(type.toUpperCase(Locale.ROOT));
        List<Integer> spList = sConf.getIntegerList("shardprofile");
        StoryShardProfile shardProfile = new StoryShardProfile(
                spList.get(0),
                spList.get(1),
                spList.get(2),
                spList.get(3),
                spList.get(4),
                spList.get(5),
                spList.get(6),
                spList.get(7),
                spList.get(8)
        );
        List<String> lore = sConf.getStringList("lore");

        return new Story(
                Integer.parseInt(sConf.getName()),
                name,
                rarity,
                storyType,
                shardProfile,
                lore.toArray(new String[0])
        );
    }

    private void fillBlockDefinitions() {
        storiedBlockDefinitionMap.put(
                Material.STONE,
                new StoriedBlockDefinition(
                        blockTierMap.get(1),
                        Arrays.asList(
                             StoryType.ELEMENTAL,
                             StoryType.HISTORICAL
                        ),
                        storyMapUnique.get(1)
                )
        );
    }

    public Story getStory(int id, StoryRarity storyRarity) {
        switch (storyRarity) {
            case Common:
                return storyMapCommon.get(id);
            case Uncommon:
                return storyMapUncommon.get(id);
            case Rare:
                return storyMapRare.get(id);
            case Epic:
                return storyMapEpic.get(id);
            case Mythical:
                return storyMapMythical.get(id);
            case Unique:
                return storyMapUnique.get(id);
            default:
                throw new IllegalStateException("Unexpected value: " + storyRarity);
        }
    }


}
