package gecko10000.geckojobs

import gecko10000.geckojobs.config.model.ActionCategory
import gecko10000.geckojobs.config.model.Harvestable
import gecko10000.geckojobs.config.model.Job
import gecko10000.geckojobs.di.MyKoinComponent
import gecko10000.geckolib.config.YamlFileManager
import gecko10000.geckolib.extensions.parseMM
import net.kyori.adventure.bossbar.BossBar
import org.koin.core.component.inject

class JobConfigManager : MyKoinComponent {

    private val plugin: GeckoJobs by inject()

    private val jobsDirectory = plugin.dataFolder.resolve("jobs")
    private val jobsList = mutableListOf<YamlFileManager<Job>>()
    val jobs: List<Job>
        get() = jobsList.map { it.value }

    private val actionToJobMap = mutableMapOf<ActionCategory, Map<String, List<Job>>>()

    fun findJobsFor(category: ActionCategory, actionId: String) =
        actionToJobMap[category]?.get(actionId) ?: emptyList()

    init {
        reloadJobConfigs()
    }

    private fun saveDefaultJobConfigs() {
        // Don't overwrite possibly-changed existing configs
        if (!jobsDirectory.mkdir()) return
        for (job in DEFAULT_JOBS) {
            YamlFileManager(
                configDirectory = jobsDirectory,
                configName = "${job.id}.yml",
                initialValue = job,
                serializer = Job.serializer()
            ).save()
        }
    }

    private fun recomputeActionToJobMap() {
        actionToJobMap.clear()
        for (job in jobs) {
            for (category in job.actions) {
                for (entry in category.value) {
                    actionToJobMap.compute(category.key) { _, existingMapping ->
                        val newMap = existingMapping?.toMutableMap() ?: mutableMapOf()
                        newMap.compute(entry.key) { _, jobs ->
                            val newList = jobs?.toMutableList() ?: mutableListOf()
                            newList.add(job)
                            return@compute newList
                        }
                        return@compute newMap
                    }
                }
            }
        }
    }

    fun reloadJobConfigs() {
        jobsList.clear()
        saveDefaultJobConfigs()
        val jobFiles = jobsDirectory.listFiles { it.extension == "yml" }
        for (jobFile in jobFiles) {
            jobsList += YamlFileManager(
                configFile = jobFile,
                initialValue = DEFAULT_JOBS[0],
                serializer = Job.serializer(),
            )
        }
        recomputeActionToJobMap()
    }

    companion object {
        private val DEFAULT_JOBS = listOf(
            Job(
                id = "miner",
                name = parseMM("<gray><b>Miner"),
                description = listOf(
                    parseMM("<yellow>Ores: mined"),
                    parseMM("<yellow>Caves: found"),
                    parseMM("<yellow>Xray: off"),
                ),
                bossBarColor = BossBar.Color.WHITE,
                actions = mapOf(
                    ActionCategory.MINE to mapOf(
                        "stone" to 0.1,
                        "andesite" to 0.1,
                        "diorite" to 0.1,
                        "granite" to 0.1,
                        "tuff" to 0.1,
                        "calcite" to 0.4,
                        "amethyst_block" to 0.5,
                        "sandstone" to 0.1,
                        "chiseled_sandstone" to 0.1,
                        "cut_sandstone" to 0.1,
                        "deepslate" to 0.15,
                        "coal_ore" to 0.2,
                        "deepslate_coal_ore" to 0.2,
                        "redstone_ore" to 0.2,
                        "deepslate_redstone_ore" to 0.2,
                        "iron_ore" to 0.3,
                        "deepslate_iron_ore" to 0.3,
                        "copper_ore" to 0.4,
                        "deepslate_copper_ore" to 0.4,
                        "gold_ore" to 0.8,
                        "deepslate_gold_ore" to 0.8,
                        "lapis_ore" to 0.6,
                        "deepslate_lapis_ore" to 0.6,
                        "diamond_ore" to 1.1,
                        "deepslate_diamond_ore" to 1.1,
                        "emerald_ore" to 3.0,
                        "deepslate_emerald_ore" to 3.0,
                        "obsidian" to 1.0,
                        "cobblestone_wall" to 0.2,
                        "mossy_cobblestone_wall" to 0.2,
                        "terracotta" to 0.2,
                        "red_terracotta" to 0.2,
                        "orange_terracotta" to 0.2,
                        "yellow_terracotta" to 0.2,
                        "brown_terracotta" to 0.2,
                        "white_terracotta" to 0.2,
                        "light_gray_terracotta" to 0.2,

                        "nether_quartz_ore" to 0.3,
                        "nether_gold_ore" to 0.3,
                        "nether_bricks" to 0.2,
                        "nether_brick_stairs" to 0.2,
                        "nether_brick_fence" to 0.2,
                        "netherrack" to 0.05,
                        "basalt" to 0.1,

                        "end_stone" to 0.4,
                    )
                )
            ),
            Job(
                id = "hunter",
                name = parseMM("<dark_red><b>Hunter"),
                description = listOf(
                    parseMM("<yellow>Kill mobs. Behead mobs."),
                    parseMM("<yellow>Roundhouse kick mobs into the nether."),
                ),
                bossBarColor = BossBar.Color.RED,
                actions = mapOf(
                    ActionCategory.KILL to mapOf(
                        "bat" to 3.0,
                        "blaze" to 0.8,
                        "bogged" to 1.0,
                        "breeze" to 0.4,
                        "cave_spider" to 0.2,
                        "chicken" to 0.4,
                        "cod" to 0.4,
                        "cow" to 0.4,
                        "creaking" to 0.7,
                        "creeper" to 0.5,
                        "dolphin" to 0.3,
                        "drowned" to 0.6,
                        "elder_guardian" to 25.0,
                        "ender_dragon" to 10.0,
                        "enderman" to 0.04,
                        "endermite" to 0.6,
                        "evoker" to 2.5,
                        "ghast" to 0.7,
                        "glow_squid" to 0.5,
                        "goat" to 0.4,
                        "guardian" to 0.2,
                        "hoglin" to 0.2,
                        "husk" to 0.5,
                        "iron_golem" to 0.8,
                        "magma_cube" to 0.06,
                        "mooshroom" to 0.5,
                        "nautilus" to 0.6,
                        "parched" to 0.5,
                        "phantom" to 0.8,
                        "pig" to 0.3,
                        "piglin" to 0.8,
                        "piglin_brute" to 2.0,
                        "pillager" to 0.3,
                        "polar_bear" to 0.6,
                        "pufferfish" to 0.4,
                        "rabbit" to 0.3,
                        "ravager" to 0.9,
                        "salmon" to 0.4,
                        "sheep" to 0.3,
                        "shulker" to 0.8,
                        "silverfish" to 0.3,
                        "skeleton" to 0.4,
                        "skeleton_horse" to 1.0,
                        "slime" to 0.06,
                        "spider" to 0.4,
                        "squid" to 0.4,
                        "stray" to 0.5,
                        "tropical_fish" to 0.4,
                        "vex" to 1.2,
                        "vindicator" to 0.8,
                        "warden" to 3.6,
                        "witch" to 0.5,
                        "wither" to 15.0,
                        "wither_skeleton" to 0.6,
                        "zoglin" to 0.4,
                        "zombie" to 0.3,
                        "zombie_horse" to 1.0,
                        "zombie_villager" to 0.3,
                        "zombified_piglin" to 0.3,
                    ),
                    ActionCategory.TAME to mapOf(
                        "wolf" to 5.0,
                        "ocelot" to 5.0,
                        "horse" to 3.0,
                        "skeleton_horse" to 5.0,
                        "zombie_horse" to 4.0,
                        "donkey" to 4.0,
                        "llama" to 4.0,
                        "mule" to 4.0,
                        "parrot" to 4.0,
                        "nautilus" to 4.0,
                        "zombie_nautilus" to 4.0,
                    )
                )
            ),
            Job(
                id = "fisherman",
                name = parseMM("<#00b3a8><b>Fisherman"),
                description = listOf(
                    parseMM("<yellow>Don't mind the smell."),
                    parseMM("<yellow>No scam here."),
                ),
                bossBarColor = BossBar.Color.BLUE,
                actions = mapOf(
                    ActionCategory.FISH to mapOf(
                        "cod" to 0.5,
                        "salmon" to 0.8,
                        "pufferfish" to 1.4,
                        "tropical_fish" to 3.0,
                        "enchanted_book" to 10.0,
                        "bow" to 5.0,
                        "fishing_rod" to 5.0,
                        "name_tag" to 5.0,
                        "nautilus_shell" to 3.0,
                        "saddle" to 5.0,
                        "lily_pad" to 4.0,
                        "bone" to 1.0,
                        "bowl" to 1.0,
                        "leather" to 1.0,
                        "leather_boots" to 1.0,
                        "rotten_flesh" to 1.0,
                        "potion" to 1.0,
                        "tripwire_hook" to 1.0,
                        "stick" to 1.0,
                        "string" to 1.0,
                        "ink_sac" to 1.0,
                        "bamboo" to 4.0,
                    )
                )
            ),
            Job(
                id = "enchanter",
                name = parseMM("<#9966cc><b>Enchanter"),
                description = listOf(
                    parseMM("<yellow>You're a hairy wizard."),
                ),
                bossBarColor = BossBar.Color.PURPLE,
                actions = mapOf(
                    ActionCategory.ENCHANT to mapOf(
                        "minecraft:aqua_affinity" to 2.0,
                        "minecraft:bane_of_arthropods" to 0.6,
                        "minecraft:blast_protection" to 0.8,
                        "minecraft:breach" to 0.8,
                        "minecraft:channeling" to 2.4,
                        "minecraft:density" to 0.7,
                        "minecraft:depth_strider" to 1.0,
                        "minecraft:efficiency" to 1.0,
                        "minecraft:feather_falling" to 0.8,
                        "minecraft:fire_aspect" to 1.2,
                        "minecraft:fire_protection" to 0.9,
                        "minecraft:flame" to 2.0,
                        "minecraft:fortune" to 1.5,
                        "minecraft:impaling" to 0.5,
                        "minecraft:infinity" to 4.0,
                        "minecraft:knockback" to 1.5,
                        "minecraft:looting" to 1.5,
                        "minecraft:loyalty" to 0.7,
                        "minecraft:luck_of_the_sea" to 0.9,
                        "minecraft:lunge" to 0.8,
                        "minecraft:lure" to 0.9,
                        "minecraft:multishot" to 3.0,
                        "minecraft:piercing" to 0.6,
                        "minecraft:power" to 0.5,
                        "minecraft:projectile_protection" to 0.6,
                        "minecraft:protection" to 0.7,
                        "minecraft:punch" to 1.4,
                        "minecraft:quick_charge" to 0.9,
                        "minecraft:respiration" to 1.1,
                        "minecraft:riptide" to 0.9,
                        "minecraft:sharpness" to 0.8,
                        "minecraft:silk_touch" to 3.0,
                        "minecraft:smite" to 0.5,
                        "minecraft:sweeping_edge" to 1.4,
                        "minecraft:thorns" to 0.9,
                        "minecraft:unbreaking" to 1.5,
                    )
                )
            ),
            Job(
                id = "farmer",
                name = parseMM("<#e6a500><b>Farmer"),
                description = listOf(
                    parseMM("<yellow>Working at the farmacy."),
                ),
                bossBarColor = BossBar.Color.YELLOW,
                actions = mapOf(
                    ActionCategory.HARVEST to mapOf(
                        Harvestable.WHEAT to 0.9,
                        Harvestable.POTATOES to 0.9,
                        Harvestable.CARROTS to 0.9,
                        Harvestable.BEETROOTS to 0.9,
                        Harvestable.CACTUS to 1.7,
                        Harvestable.SUGAR_CANE to 0.5,
                        Harvestable.PUMPKIN to 1.5,
                        Harvestable.MELON to 1.5,
                        Harvestable.CHORUS_FRUIT to 0.8,
                        Harvestable.CHORUS_FLOWER to 5.0,
                        Harvestable.BAMBOO to 0.3,
                        Harvestable.KELP to 0.3,
                        Harvestable.COCOA to 1.8,
                        Harvestable.GLOW_LICHEN to 2.0,
                        Harvestable.GLOW_BERRIES to 3.0,
                        Harvestable.MOSS to 1.6,
                        Harvestable.NETHER_WART to 1.2,
                        Harvestable.SEAGRASS to 0.9,
                        Harvestable.SEA_PICKLE to 3.0,
                        Harvestable.SWEET_BERRIES to 2.0,
                        Harvestable.VINES to 4.0,
                    )
                )
            ),
            Job(
                id = "lumberjack",
                name = parseMM("<#996f3d><b>Lumberjack"),
                description = listOf(
                    parseMM("<yellow>Got wood?"),
                    parseMM("<yellow>I know I do..."),
                ),
                bossBarColor = BossBar.Color.GREEN,
                actions = mapOf(
                    ActionCategory.MINE to mapOf(
                        "oak_log" to 0.5,
                    )
                )
            ),
            Job(
                id = "digger",
                name = parseMM("<#ffc400><b>Digger"),
                description = listOf(
                    parseMM("<yellow>With a <u>D</u>. No N's."),
                ),
                bossBarColor = BossBar.Color.YELLOW,
                actions = mapOf(
                    ActionCategory.MINE to mapOf()
                )
            ),
            Job(
                id = "brewer",
                name = parseMM("<#cc5252><b>Brewer"),
                description = listOf(
                    parseMM("<yellow>Don't get high on"),
                    parseMM("<yellow>your own supply."),
                ),
                bossBarColor = BossBar.Color.PINK,
                actions = mapOf(
                    ActionCategory.BREW to mapOf()
                )
            ),
        )
    }

}
