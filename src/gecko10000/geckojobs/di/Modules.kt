package gecko10000.geckojobs.di

import gecko10000.geckojobs.*
import gecko10000.geckojobs.claims.BlockClaimManager
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun pluginModules(plugin: GeckoJobs) = module {
    single { plugin }
    single { Json }
    single(createdAtStart = true) { ActionProgressManager() }
    single(createdAtStart = true) { BlockClaimManager() }
    single(createdAtStart = true) { JobConfigManager() }
    single(createdAtStart = true) { JobProgressDisplay() }
    single(createdAtStart = true) { JobProgressStorage() }
}
