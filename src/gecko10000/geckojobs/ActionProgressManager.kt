package gecko10000.geckojobs

import gecko10000.geckojobs.config.model.ActionCategory
import gecko10000.geckojobs.config.model.Job
import gecko10000.geckojobs.di.MyKoinComponent
import org.bukkit.entity.Player
import org.koin.core.component.inject

class ActionProgressManager : MyKoinComponent {

    companion object {
        private const val JOB_BOOST_PREFIX = "geckojobs.boost."
    }

    private val plugin: GeckoJobs by inject()
    private val jobConfigManager: JobConfigManager by inject()
    private val jobProgressStorage: JobProgressStorage by inject()

    fun addProgress(player: Player, actionCategory: ActionCategory, identifier: String, givenMult: Double = 1.0) {
        val jobsToProgress = jobConfigManager.findJobsFor(actionCategory, identifier)
        jobsToProgress.forEach { addProgressToJob(player, it, actionCategory, identifier, givenMult) }
    }

    private fun addProgressToJob(
        player: Player,
        job: Job,
        actionCategory: ActionCategory,
        identifier: String,
        givenMult: Double
    ) {
        val baseExperience = job.actions[actionCategory]?.get(identifier) ?: return
        val permissionMultiplier = 1 + getPermissionBoost(player, job)
        val finalExperience = baseExperience * permissionMultiplier * givenMult
        jobProgressStorage.addExperience(player, job, finalExperience)
    }

    // All-job boost stacks with boost for specific job
    private fun getPermissionBoost(player: Player, job: Job): Double {
        val allJobBoost = getMaxPermForPrefix(player, "${JOB_BOOST_PREFIX}all.") ?: 0.0
        val specificJobBoost = getMaxPermForPrefix(player, "${JOB_BOOST_PREFIX}${job.id}.") ?: 0.0
        return allJobBoost + specificJobBoost
    }

    private fun getMaxPermForPrefix(player: Player, prefix: String): Double? {
        return player.effectivePermissions
            .asSequence()
            .filter { it.value }
            .map { it.permission }
            .filter { it.startsWith(prefix) }
            .map { it.substringAfter(prefix) }
            .mapNotNull { it.toDoubleOrNull() }
            .maxOrNull()
    }

}
