package gecko10000.geckojobs.di

import gecko10000.geckojobs.GeckoJobs
import gecko10000.geckojobs.JobConfigManager
import org.koin.dsl.module

fun pluginModules(plugin: GeckoJobs) = module {
    single { plugin }
    single(createdAtStart = true) { JobConfigManager() }
}
