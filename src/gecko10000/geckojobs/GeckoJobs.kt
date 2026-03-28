package gecko10000.geckojobs

import gecko10000.geckojobs.command.CommandHandler
import gecko10000.geckojobs.config.Config
import gecko10000.geckojobs.di.MyKoinComponent
import gecko10000.geckojobs.di.MyKoinContext
import gecko10000.geckolib.config.YamlFileManager
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject

class GeckoJobs : JavaPlugin(), MyKoinComponent {

    private val jobConfigManager: JobConfigManager by inject()

    private val configFile = YamlFileManager(
        configDirectory = dataFolder,
        initialValue = Config(),
        serializer = Config.serializer()
    )
    val config: Config
        get() = configFile.value

    override fun onEnable() {
        MyKoinContext.init(this)
        CommandHandler().register()
        Listeners()
    }

    fun reloadConfigs() {
        configFile.reload()
        jobConfigManager.reloadJobConfigs()
    }

}
