package gecko10000.geckojobs

import gecko10000.geckojobs.config.model.ActionCategory
import gecko10000.geckojobs.config.model.ActionEntry
import gecko10000.geckojobs.config.model.Job
import gecko10000.geckojobs.di.MyKoinComponent
import gecko10000.geckolib.config.YamlFileManager
import gecko10000.geckolib.extensions.parseMM
import org.koin.core.component.inject

class JobConfigManager : MyKoinComponent {

    private val plugin: GeckoJobs by inject()

    private val jobsDirectory = plugin.dataFolder.resolve("jobs")
    private val jobsList = mutableListOf<YamlFileManager<Job>>()
    val jobs: List<Job>
        get() = jobsList.map { it.value }

    init {
        saveDefaultJobConfigs()
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

    fun reloadJobConfigs() {
        jobsList.clear()
        val jobFiles = jobsDirectory.listFiles { it.extension == "yml" }
        for (jobFile in jobFiles) {
            jobsList += YamlFileManager(
                configFile = jobFile,
                initialValue = DEFAULT_JOBS[0],
                serializer = Job.serializer(),
            )
        }
    }

    companion object {
        private val DEFAULT_JOBS = listOf(
            Job(
                id = "miner",
                name = parseMM("<light_gray><b>Miner"),
                description = listOf(
                    parseMM("<yellow>Ores: mined"),
                    parseMM("<yellow>Caves: found"),
                    parseMM("<yellow>Xray: off"),
                ),
                actions = mapOf(
                    ActionCategory.MINE to listOf(
                        ActionEntry("stone", 0.1),
                        ActionEntry("andesite", 0.1),
                        ActionEntry("diorite", 0.1),
                        ActionEntry("granite", 0.1),
                        ActionEntry("tuff", 0.1),
                        ActionEntry("calcite", 0.4),
                        ActionEntry("amethyst_block", 0.5),
                        ActionEntry("sandstone", 0.1),
                        ActionEntry("chiseled_sandstone", 0.1),
                        ActionEntry("cut_sandstone", 0.1),
                        ActionEntry("deepslate", 0.15),
                        ActionEntry("coal_ore", 0.2),
                        ActionEntry("deepslate_coal_ore", 0.2),
                        ActionEntry("redstone_ore", 0.2),
                        ActionEntry("deepslate_redstone_ore", 0.2),
                        ActionEntry("iron_ore", 0.3),
                        ActionEntry("deepslate_iron_ore", 0.3),
                        ActionEntry("copper_ore", 0.4),
                        ActionEntry("deepslate_copper_ore", 0.4),
                        ActionEntry("gold_ore", 0.8),
                        ActionEntry("deepslate_gold_ore", 0.8),
                        ActionEntry("lapis_ore", 0.6),
                        ActionEntry("deepslate_lapis_ore", 0.6),
                        ActionEntry("diamond_ore", 1.1),
                        ActionEntry("deepslate_diamond_ore", 1.1),
                        ActionEntry("emerald_ore", 3.0),
                        ActionEntry("deepslate_emerald_ore", 3.0),
                        ActionEntry("obsidian", 1.0),
                        ActionEntry("cobblestone_wall", 0.2),
                        ActionEntry("mossy_cobblestone_wall", 0.2),
                        ActionEntry("terracotta", 0.2),
                        ActionEntry("red_terracotta", 0.2),
                        ActionEntry("orange_terracotta", 0.2),
                        ActionEntry("yellow_terracotta", 0.2),
                        ActionEntry("brown_terracotta", 0.2),
                        ActionEntry("white_terracotta", 0.2),
                        ActionEntry("light_gray_terracotta", 0.2),

                        ActionEntry("nether_quartz_ore", 0.3),
                        ActionEntry("nether_gold_ore", 0.3),
                        ActionEntry("nether_bricks", 0.2),
                        ActionEntry("nether_brick_stairs", 0.2),
                        ActionEntry("nether_brick_fence", 0.2),
                        ActionEntry("netherrack", 0.05),
                        ActionEntry("basalt", 0.1),

                        ActionEntry("end_stone", 0.4),
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
                actions = mapOf(
                    ActionCategory.KILL to listOf(
                        ActionEntry("bat", 3.0),
                        ActionEntry("blaze", 0.8),
                        ActionEntry("bogged", 1.0),
                        ActionEntry("breeze", 0.4),
                        ActionEntry("cave_spider", 0.2),
                        ActionEntry("chicken", 0.4),
                        ActionEntry("cod", 0.4),
                        ActionEntry("cow", 0.4),
                        ActionEntry("creaking", 0.7),
                        ActionEntry("creeper", 0.5),
                        ActionEntry("dolphin", 0.3),
                        ActionEntry("drowned", 0.6),
                        ActionEntry("elder_guardian", 25.0),
                        ActionEntry("ender_dragon", 10.0),
                        ActionEntry("enderman", 0.04),
                        ActionEntry("endermite", 0.6),
                        ActionEntry("evoker", 2.5),
                        ActionEntry("ghast", 0.7),
                        ActionEntry("glow_squid", 0.5),
                        ActionEntry("goat", 0.4),
                        ActionEntry("guardian", 0.2),
                        ActionEntry("hoglin", 0.2),
                        ActionEntry("husk", 0.5),
                        ActionEntry("iron_golem", 0.8),
                        ActionEntry("magma_cube", 0.06),
                        ActionEntry("mooshroom", 0.5),
                        ActionEntry("nautilus", 0.6),
                        ActionEntry("parched", 0.5),
                        ActionEntry("phantom", 0.8),
                        ActionEntry("pig", 0.3),
                        ActionEntry("piglin", 0.8),
                        ActionEntry("piglin_brute", 2.0),
                        ActionEntry("pillager", 0.3),
                        ActionEntry("polar_bear", 0.6),
                        ActionEntry("pufferfish", 0.4),
                        ActionEntry("rabbit", 0.3),
                        ActionEntry("ravager", 0.9),
                        ActionEntry("salmon", 0.4),
                        ActionEntry("sheep", 0.3),
                        ActionEntry("shulker", 0.8),
                        ActionEntry("silverfish", 0.3),
                        ActionEntry("skeleton", 0.4),
                        ActionEntry("skeleton_horse", 1.0),
                        ActionEntry("slime", 0.06),
                        ActionEntry("spider", 0.4),
                        ActionEntry("squid", 0.4),
                        ActionEntry("stray", 0.5),
                        ActionEntry("tropical_fish", 0.4),
                        ActionEntry("vex", 1.2),
                        ActionEntry("vindicator", 0.8),
                        ActionEntry("warden", 3.6),
                        ActionEntry("witch", 0.5),
                        ActionEntry("wither", 15.0),
                        ActionEntry("wither_skeleton", 0.6),
                        ActionEntry("zoglin", 0.4),
                        ActionEntry("zombie", 0.3),
                        ActionEntry("zombie_horse", 1.0),
                        ActionEntry("zombie_villager", 0.3),
                        ActionEntry("zombified_piglin", 0.3),
                    ),
                    ActionCategory.TAME to listOf(
                        ActionEntry("wolf", 5.0),
                        ActionEntry("ocelot", 5.0),
                        ActionEntry("horse", 3.0),
                        ActionEntry("skeleton_horse", 5.0),
                        ActionEntry("zombie_horse", 4.0),
                        ActionEntry("donkey", 4.0),
                        ActionEntry("llama", 4.0),
                        ActionEntry("mule", 4.0),
                        ActionEntry("parrot", 4.0),
                        ActionEntry("nautilus", 4.0),
                        ActionEntry("zombie_nautilus", 4.0),
                    )
                )
            )
        )
    }

}
