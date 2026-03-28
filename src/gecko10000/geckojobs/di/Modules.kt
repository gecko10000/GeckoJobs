package gecko10000.geckojobs.di

import gecko10000.geckojobs.*
import org.koin.dsl.module

fun pluginModules(plugin: GeckoJobs) = module {
    single { plugin }
    single(createdAtStart = true) { ActionProgressManager() }
    single(createdAtStart = true) { JobConfigManager() }
    single(createdAtStart = true) { JobProgressDisplay() }
    single(createdAtStart = true) { JobProgressStorage() }
}
