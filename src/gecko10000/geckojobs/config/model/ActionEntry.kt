package gecko10000.geckojobs.config.model

import kotlinx.serialization.Serializable

@Serializable
data class ActionEntry(
    val actionId: String,
    val baseExperience: Double,
)
