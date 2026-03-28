package gecko10000.geckojobs

import gecko10000.geckojobs.config.model.Job
import gecko10000.geckojobs.di.MyKoinComponent
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject

class JobProgressStorage : MyKoinComponent {

    private val plugin: GeckoJobs by inject()
    private val jobProgressDisplay: JobProgressDisplay by inject()

    private fun jobLevelKey(job: Job) = NamespacedKey(plugin, "level_${job.id}")
    private fun jobExpKey(job: Job) = NamespacedKey(plugin, "exp_${job.id}")

    fun addExperience(player: Player, job: Job, exp: Double) {
        val currentProgress = getProgress(player, job)
        val rawNewProgress = currentProgress.copy(exp = currentProgress.exp + exp)
        val newProgress = normalize(rawNewProgress)
        setProgress(player, job, newProgress)
        jobProgressDisplay.showProgress(player, job)
    }

    // Gets progress to within [0, expForLevel(level+1))
    private fun normalize(originalProgress: Progress): Progress {
        var progress = originalProgress
        // level down
        while (progress.exp < 0) {
            progress = progress.copy(
                level = progress.level - 1,
                exp = progress.exp + getExperienceNeededForLevel(progress.level)
            )
        }

        // level up
        var expForNextLevel = getExperienceNeededForLevel(progress.level + 1)
        while (progress.exp > expForNextLevel) {
            progress = progress.copy(level = progress.level + 1, exp = progress.exp - expForNextLevel)
            expForNextLevel = getExperienceNeededForLevel(progress.level + 1)
        }
        return progress
    }

    fun getExperienceNeededForLevel(level: Int) =
        plugin.config.jobLevelExpression.evaluate(level.toDouble())

    fun getProgress(player: Player, job: Job): Progress {
        val level = player.persistentDataContainer.get(jobLevelKey(job), PersistentDataType.INTEGER) ?: 0
        val exp = player.persistentDataContainer.get(jobExpKey(job), PersistentDataType.DOUBLE) ?: 0.0
        return Progress(level, exp)
    }

    fun setProgress(player: Player, job: Job, progress: Progress) {
        player.persistentDataContainer.set(jobLevelKey(job), PersistentDataType.INTEGER, progress.level)
        player.persistentDataContainer.set(jobExpKey(job), PersistentDataType.DOUBLE, progress.exp)
    }

    data class Progress(
        val level: Int,
        val exp: Double,
    )

}
