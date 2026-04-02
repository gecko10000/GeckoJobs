@file:UseSerializers(MMComponentSerializer::class)

package gecko10000.geckojobs.config.model

import gecko10000.geckolib.config.serializers.MMComponentSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component

@Serializable
data class Job(
    val id: String,
    val name: Component,
    val description: List<Component>,
    val bossBarColor: BossBar.Color,
    val internalMultiplier: Double = 1.0,
    val actions: Map<ActionCategory, Map<String, Double>>,
)
