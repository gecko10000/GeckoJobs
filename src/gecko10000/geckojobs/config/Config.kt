package gecko10000.geckojobs.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val temp: Boolean = false,
)
