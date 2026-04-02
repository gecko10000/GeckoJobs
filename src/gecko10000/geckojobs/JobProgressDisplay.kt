package gecko10000.geckojobs

import gecko10000.geckojobs.config.model.Job
import gecko10000.geckojobs.di.MyKoinComponent
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckolib.misc.Task
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.koin.core.component.inject
import java.util.*

class JobProgressDisplay : MyKoinComponent {

    private val plugin: GeckoJobs by inject()
    private val jobProgressStorage: JobProgressStorage by inject()

    private val shownBars = mutableMapOf<UUID, MutableMap<Job, BossBarData>>()

    fun showProgress(player: Player, job: Job) {
        val barMap = shownBars.computeIfAbsent(player.uniqueId) { _ -> mutableMapOf() }
        val currentValue = barMap[job]
        currentValue?.removalTask?.cancel()
        val newTask = Task.syncDelayed({ ->
            shownBars[player.uniqueId]?.remove(job)?.let {
                player.hideBossBar(it.bossBar)
            }
        }, plugin.config.bossBarDisplayTicks)
        val data = currentValue?.copy(removalTask = newTask)
            ?: BossBarData(createBossBar(job), newTask)
        barMap[job] = data

        val jobProgress = jobProgressStorage.getProgress(player, job)
        val nextLevelExp = jobProgressStorage.getExperienceNeededForLevel(jobProgress.level + 1)
        val progress = jobProgress.exp / nextLevelExp
        data.bossBar.progress(progress.toFloat())
        val precision = plugin.config.bossBarPrecision
        data.bossBar.name(
            MM.deserialize(
                plugin.config.bossBarFormat,
                Placeholder.component("job", job.name),
                Placeholder.unparsed("exp", String.format("%.${precision}f", jobProgress.exp)),
                Placeholder.unparsed("required", String.format("%.${precision}f", nextLevelExp)),
                Placeholder.unparsed("level", jobProgress.level.toString()),
            )
        )
        val needsShowing = currentValue == null
        if (needsShowing) {
            player.showBossBar(data.bossBar)
        }
    }

    private fun createBossBar(job: Job) = BossBar.bossBar(
        parseMM("<red>You're not supposed to see this"),
        1f,
        job.bossBarColor,
        BossBar.Overlay.PROGRESS
    )

    fun levelUp(player: Player, job: Job, newLevel: Int) {

    }

    data class BossBarData(
        val bossBar: BossBar,
        val removalTask: Task,
    )

}
