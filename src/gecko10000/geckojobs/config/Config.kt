package gecko10000.geckojobs.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import redempt.crunch.CompiledExpression
import redempt.crunch.Crunch
import redempt.crunch.functional.EvaluationEnvironment

@Serializable
data class Config(
    @SerialName("job-level-expression")
    private val jobLevelExpressionString: String = "n * 1000",
    val bossBarDisplayTicks: Long = 100,
    val bossBarFormat: String = "<gray><job> <dark_green><level></dark_green>: <yellow><exp><gray>/</gray><required>"
) {
    val jobLevelExpression: CompiledExpression by lazy {
        val env = EvaluationEnvironment()
        env.setVariableNames("n")
        return@lazy Crunch.compileExpression(jobLevelExpressionString, env)
    }
}
