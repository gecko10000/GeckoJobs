package gecko10000.geckojobs.di

import gecko10000.geckojobs.ActionProgressManager
import gecko10000.geckojobs.GeckoJobs
import gecko10000.geckojobs.JobConfigManager
import gecko10000.geckojobs.JobProgressStorage
import org.koin.dsl.module

fun pluginModules(plugin: GeckoJobs) = module {
    single { plugin }
    single(createdAtStart = true) { ActionProgressManager() }
    single(createdAtStart = true) { JobConfigManager() }
    single(createdAtStart = true) { JobProgressStorage() }
}
