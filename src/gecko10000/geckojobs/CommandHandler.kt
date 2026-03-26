package gecko10000.geckojobs

import gecko10000.geckojobs.di.MyKoinComponent
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.strokkur.commands.Command
import net.strokkur.commands.Executes
import net.strokkur.commands.permission.Permission
import org.bukkit.command.CommandSender
import org.koin.core.component.inject

@Command("jobs")
@Permission("geckojobs.command")
class CommandHandler : MyKoinComponent {

    private val plugin: GeckoJobs by inject()
    private val jobConfigManager: JobConfigManager by inject()

    fun register() {
        plugin.lifecycleManager
            .registerEventHandler(LifecycleEvents.COMMANDS.newHandler(LifecycleEventHandler { event ->
                CommandHandlerBrigadier.register(
                    event.registrar()
                )
            }))
    }

    @Executes
    fun temp(sender: CommandSender) {
        sender.sendMessage(jobConfigManager.jobs.toString())
    }

    @Executes("reload")
    @Permission("geckojobs.command.reload")
    fun reload(sender: CommandSender) {
        plugin.reloadConfigs()
        sender.sendRichMessage("<green>Configs reloaded.")
    }
}
