package gecko10000.geckojobs

import gecko10000.geckojobs.config.model.ActionCategory
import gecko10000.geckojobs.di.MyKoinComponent
import gecko10000.geckolib.playerplaced.PlayerPlacedBlockTracker
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTameEvent
import org.koin.core.component.inject

class Listeners : Listener, MyKoinComponent {

    private val plugin: GeckoJobs by inject()
    private val actionProgressManager: ActionProgressManager by inject()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    // Note: needs to be HIGHEST to allow PlayerPlacedBlockTracker
    // to remove entries at MONITOR
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private fun BlockBreakEvent.onMineBlock() {
        if (PlayerPlacedBlockTracker.isPlayerPlaced(block)) return
        actionProgressManager.addProgress(player, ActionCategory.MINE, block.type.toString().lowercase())
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun EntityDeathEvent.onPlayerKill() {
        val killer = entity.killer ?: return
        actionProgressManager.addProgress(killer, ActionCategory.KILL, entityType.toString().lowercase())
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun EntityTameEvent.onPlayerTame() {
        val tamer = owner as? Player ?: return
        actionProgressManager.addProgress(tamer, ActionCategory.TAME, entityType.toString().lowercase())
    }

}
