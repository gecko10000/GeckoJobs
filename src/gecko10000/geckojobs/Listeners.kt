package gecko10000.geckojobs

import com.destroystokyo.paper.event.block.BlockDestroyEvent
import gecko10000.geckojobs.config.model.ActionCategory
import gecko10000.geckojobs.config.model.Harvestable
import gecko10000.geckojobs.di.MyKoinComponent
import gecko10000.geckolib.misc.Task
import gecko10000.geckolib.playerplaced.PlayerPlacedBlockTracker
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTameEvent
import org.bukkit.event.player.PlayerFishEvent
import org.koin.core.component.inject
import java.util.*

class Listeners : Listener, MyKoinComponent {

    private val plugin: GeckoJobs by inject()
    private val actionProgressManager: ActionProgressManager by inject()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    // Miner
    // Note: needs to be HIGHEST to allow PlayerPlacedBlockTracker
    // to remove entries at MONITOR
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun BlockBreakEvent.onMineNaturalBlock() {
        if (PlayerPlacedBlockTracker.isPlayerPlaced(block)) return
        actionProgressManager.addProgress(player, ActionCategory.MINE, block.type.toString().lowercase())
    }

    // Hunter
    // TODO: spawner mob multiplier?
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun EntityDeathEvent.onPlayerKill() {
        val killer = entity.killer ?: return
        actionProgressManager.addProgress(killer, ActionCategory.KILL, entityType.toString().lowercase())
    }

    // Hunter
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun EntityTameEvent.onTame() {
        val tamer = owner as? Player ?: return
        actionProgressManager.addProgress(tamer, ActionCategory.TAME, entityType.toString().lowercase())
    }

    // Fisherman
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun PlayerFishEvent.onCatch() {
        if (this.state != PlayerFishEvent.State.CAUGHT_FISH) return
        val itemEntity = caught as? Item ?: return
        val item = itemEntity.itemStack
        actionProgressManager.addProgress(player, ActionCategory.FISH, item.type.toString().lowercase())
    }

    // Enchanter
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun EnchantItemEvent.onEnchant() {
        for (entry in enchantsToAdd)
            actionProgressManager.addProgress(
                enchanter,
                ActionCategory.ENCHANT,
                entry.key.key.toString(),
                entry.value.toDouble(),
            )
    }

    private val sameBlockHarvestables = mapOf(
        Material.WHEAT to Harvestable.WHEAT,
        Material.POTATOES to Harvestable.POTATOES,
        Material.CARROTS to Harvestable.CARROTS,
        Material.BEETROOTS to Harvestable.BEETROOTS,
        Material.NETHER_WART to Harvestable.NETHER_WART,
        Material.SWEET_BERRY_BUSH to Harvestable.SWEET_BERRIES,
        Material.COCOA to Harvestable.COCOA,
    )

    // Farmer (classic crops/nether wart/sweet berries/cocoa)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun BlockBreakEvent.onHarvestSameBlockAgeable() {
        val ageable = block.blockData as? Ageable ?: return
        if (ageable.age != ageable.maximumAge) return
        val harvestable = sameBlockHarvestables[block.type] ?: return
        actionProgressManager.addProgress(player, ActionCategory.HARVEST, harvestable)
    }

    private val chainBreakingHarvestables = mapOf(
        Material.CACTUS to Harvestable.CACTUS,
        Material.SUGAR_CANE to Harvestable.SUGAR_CANE,
        Material.KELP to Harvestable.KELP,
        Material.KELP_PLANT to Harvestable.KELP,
        Material.BAMBOO to Harvestable.BAMBOO,
        Material.CHORUS_PLANT to Harvestable.CHORUS_FRUIT,
    )

    val savedBreakers = mutableMapOf<Block, UUID>()

    private val defaultFaces = setOf(BlockFace.UP)
    private val chorusFruitFaces = setOf(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)
    private fun getNextFaces(type: Material) = if (type == Material.CHORUS_PLANT) chorusFruitFaces else defaultFaces

    // Farmer: store breaker for the next blocks (for chain breaks)
    // Also pay if not player-placed.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun BlockBreakEvent.onBreakChainHarvestable() {
        val harvestable = chainBreakingHarvestables[block.type] ?: return
        val nextBlocks = getNextFaces(block.type).map { block.getRelative(it) }
        for (block in nextBlocks) {
            savedBreakers[block] = player.uniqueId
        }
        Task.syncDelayed({ -> for (block in nextBlocks) savedBreakers.remove(block) }, 2L)
        if (!PlayerPlacedBlockTracker.isPlayerPlaced(block)) {
            actionProgressManager.addProgress(player, ActionCategory.HARVEST, harvestable)
        }
    }

    // Farmer: pay for chain breaks if not player-placed.
    // Also, store the breaker for future use.
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun BlockDestroyEvent.onDestroy() {
        val harvestable = chainBreakingHarvestables[block.type] ?: return
        val breaker = savedBreakers[block] ?: return
        val nextBlocks = getNextFaces(block.type).map { block.getRelative(it) }
        for (block in nextBlocks) {
            savedBreakers[block] = breaker
        }
        Task.syncDelayed({ -> for (block in nextBlocks) savedBreakers.remove(block) }, 2L)
        val player = plugin.server.getPlayer(breaker)
        if (player != null && !PlayerPlacedBlockTracker.isPlayerPlaced(block)) {
            actionProgressManager.addProgress(player, ActionCategory.HARVEST, harvestable)
        }
    }

    private val newBlockHarvestables = mapOf(
        Material.VINE to Harvestable.VINES,
        Material.PUMPKIN to Harvestable.PUMPKIN,
        Material.MELON to Harvestable.MELON,
    )

}
