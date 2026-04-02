package gecko10000.geckojobs.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import gecko10000.geckojobs.JobConfigManager
import gecko10000.geckojobs.config.model.Job
import gecko10000.geckojobs.di.MyKoinComponent
import gecko10000.geckolib.extensions.MM
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture

class JobArgumentType : CustomArgumentType.Converted<Job, String>, MyKoinComponent {

    private val jobConfigManager: JobConfigManager by inject()

    companion object {
        private val ERROR_INVALID_JOB = DynamicCommandExceptionType { name ->
            MessageComponentSerializer.message()
                .serialize(
                    MM.deserialize(
                        "<red>Job <name> doesn't exist!",
                        Placeholder.unparsed("name", name.toString())
                    )
                )
        }
    }

    override fun convert(nativeType: String): Job {
        return jobConfigManager.jobs.firstOrNull { it.id == nativeType }
            ?: throw ERROR_INVALID_JOB.create(nativeType)
    }

    override fun getNativeType(): ArgumentType<String> {
        return StringArgumentType.word()
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        jobConfigManager.jobs.map(Job::id).forEach { builder.suggest(it) }
        return builder.buildFuture()
    }

}
