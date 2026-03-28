package gecko10000.geckojobs.command

import gecko10000.geckojobs.GeckoJobs
import gecko10000.geckojobs.JobProgressStorage
import gecko10000.geckojobs.config.model.Job
import gecko10000.geckojobs.di.MyKoinComponent
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.strokkur.commands.Command
import net.strokkur.commands.Executes
import net.strokkur.commands.Literal
import net.strokkur.commands.paper.arguments.CustomArg
import net.strokkur.commands.permission.Permission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

@Command("jobs")
@Permission("geckojobs.command")
class CommandHandler : MyKoinComponent {

    private val plugin: GeckoJobs by inject()
    private val jobProgressStorage: JobProgressStorage by inject()

    fun register() {
        plugin.lifecycleManager
            .registerEventHandler(LifecycleEvents.COMMANDS.newHandler(LifecycleEventHandler { event ->
                CommandHandlerBrigadier.register(
                    event.registrar()
                )
            }))
    }

    @Executes("set")
    @Permission("geckojobs.command.set")
    fun set(
        sender: CommandSender, target: Player,
        @Literal("level", "xp") levelOrXp: String,
        @CustomArg(JobArgumentType::class) job: Job,
        value: Double
    ) {
        val currentProgress = jobProgressStorage.getProgress(target, job)
        val newProgress = when (levelOrXp) {
            "level" -> JobProgressStorage.Progress(value.toInt(), 0.0)
            "xp" -> JobProgressStorage.Progress(currentProgress.level, value)
            else -> throw IllegalArgumentException("Shouldn't happen")
        }
        jobProgressStorage.setProgress(target, job, newProgress)
        sender.sendRichMessage(
            "<green>Set <player>'s <job> $levelOrXp to $value.",
            Placeholder.component("player", target.name()),
            Placeholder.component("job", job.name),
        )
    }

    @Executes("reload")
    @Permission("geckojobs.command.reload")
    fun reload(sender: CommandSender) {
        plugin.reloadConfigs()
        sender.sendRichMessage("<green>Configs reloaded.")
    }
}
