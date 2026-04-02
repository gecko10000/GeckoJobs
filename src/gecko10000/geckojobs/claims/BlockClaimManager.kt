package gecko10000.geckojobs.claims

import gecko10000.geckojobs.GeckoJobs
import gecko10000.geckojobs.di.MyKoinComponent
import gecko10000.geckolib.extensions.parseMM
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject
import java.util.*

/**
 * Stores the UUID of the player in the claimed block.
 * Also stores a counter in the player and in the claimed block
 */
class BlockClaimManager : Listener, MyKoinComponent {

    private val plugin: GeckoJobs by inject()

    private val claimableMaterials = mapOf(
        Material.BREWING_STAND to ClaimType.BREWING_STAND,
        Material.FURNACE to ClaimType.FURNACE,
        Material.BLAST_FURNACE to ClaimType.FURNACE,
        Material.SMOKER to ClaimType.FURNACE,
    )

    private val claimOwnerKey = NamespacedKey(plugin, "claim_owner")
    private fun claimIterKey(claimType: ClaimType) = NamespacedKey(plugin, "citer_${claimType.name.lowercase()}")
    private fun claimCountKey(claimType: ClaimType) = NamespacedKey(plugin, "ccount_${claimType.name.lowercase()}")

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    private fun PlayerJoinEvent.onJoin() {
        for (type in ClaimType.entries) {
            if (!player.persistentDataContainer.has(claimIterKey(type))) {
                player.persistentDataContainer.set(claimIterKey(type), PersistentDataType.INTEGER, 0)
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun BlockPlaceEvent.onPlaceClaimableBlock() {
        val claimType = claimableMaterials[block.type] ?: return
        // Ensure room for claim
        val maxClaims = getMaxClaims(player, claimType)
        val currentClaims =
            player.persistentDataContainer.get(claimCountKey(claimType), PersistentDataType.INTEGER) ?: 0
        if (currentClaims >= maxClaims) {
            player.sendActionBar(parseMM("<red>Too many $claimType claims ($currentClaims)"))
            return
        }
        // Increment claim count in player
        player.persistentDataContainer.set(claimCountKey(claimType), PersistentDataType.INTEGER, currentClaims + 1)

        // Save player UUID in block
        val state = block.getState(false) as PersistentDataHolder
        state.persistentDataContainer.set(claimOwnerKey, PersistentDataType.STRING, player.uniqueId.toString())

        // Save player iteration in block
        val iteration = player.persistentDataContainer.get(claimIterKey(claimType), PersistentDataType.INTEGER)!!
        state.persistentDataContainer.set(claimIterKey(claimType), PersistentDataType.INTEGER, iteration)
        player.sendActionBar(parseMM("<green>Claimed $claimType (${currentClaims + 1}/$maxClaims)"))
    }

    private fun doesClaimIterMatch(player: Player, blockState: PersistentDataHolder, claimType: ClaimType): Boolean {
        val playerIter = player.persistentDataContainer.get(claimIterKey(claimType), PersistentDataType.INTEGER)!!
        val blockIter = blockState.persistentDataContainer.get(claimIterKey(claimType), PersistentDataType.INTEGER)
        return playerIter == blockIter
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun BlockBreakEvent.onBreakClaimableBlock() {
        val claimType = claimableMaterials[block.type] ?: return
        val state = block.getState(false) as PersistentDataHolder
        val ownerIdString = state.persistentDataContainer.get(claimOwnerKey, PersistentDataType.STRING) ?: return
        val player = plugin.server.getPlayer(UUID.fromString(ownerIdString)) ?: return
        if (!doesClaimIterMatch(player, state, claimType)) return
        // Decrement count if player online
        val claimCount = player.persistentDataContainer.get(claimCountKey(claimType), PersistentDataType.INTEGER) ?: 1
        player.persistentDataContainer.set(claimCountKey(claimType), PersistentDataType.INTEGER, claimCount - 1)
        val max = getMaxClaims(player, claimType)
        player.sendActionBar(parseMM("<green>Unclaimed $claimType (${claimCount - 1}/$max)"))
    }

    fun resetAllClaims(player: Player, claimType: ClaimType) {
        val currentIter = player.persistentDataContainer
            .getOrDefault(claimIterKey(claimType), PersistentDataType.INTEGER, 0)
        player.persistentDataContainer.set(claimIterKey(claimType), PersistentDataType.INTEGER, currentIter + 1)
        player.persistentDataContainer.set(claimCountKey(claimType), PersistentDataType.INTEGER, 0)
    }

    fun getClaimOwner(block: Block, claimType: ClaimType): Player? {
        val state = block.getState(false) as? PersistentDataHolder ?: return null
        val uuidString = state.persistentDataContainer.get(claimOwnerKey, PersistentDataType.STRING) ?: return null
        val player = plugin.server.getPlayer(UUID.fromString(uuidString)) ?: return null
        if (!doesClaimIterMatch(player, state, claimType)) return null
        return player
    }

    private fun getPermPrefix(claimType: ClaimType): String =
        "geckojobs.max_claims.${claimType.name.lowercase()}."

    fun getMaxClaims(player: Player, claimType: ClaimType): Int {
        val permPrefix = getPermPrefix(claimType)
        return player.effectivePermissions
            .asSequence()
            .filter { it.value }
            .map { it.permission }
            .filter { it.startsWith(permPrefix) }
            .map { it.substringAfter(permPrefix) }
            .mapNotNull { it.toIntOrNull() }
            .maxOrNull() ?: plugin.config.defaultMaxClaims[claimType] ?: 0
    }

}
