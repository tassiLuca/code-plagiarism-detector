package org.danilopianini.plagiarismdetector.input.cli.provider

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.choice
import org.danilopianini.plagiarismdetector.commons.BitBucket
import org.danilopianini.plagiarismdetector.commons.GitHub
import org.danilopianini.plagiarismdetector.commons.HostingService
import org.danilopianini.plagiarismdetector.input.SupportedOptions
import org.danilopianini.plagiarismdetector.provider.criteria.ByBitbucketName
import org.danilopianini.plagiarismdetector.provider.criteria.ByBitbucketUser
import org.danilopianini.plagiarismdetector.provider.criteria.ByGitHubName
import org.danilopianini.plagiarismdetector.provider.criteria.ByGitHubUser
import org.danilopianini.plagiarismdetector.provider.criteria.SearchCriteria
import java.net.URL

/**
 * A class encapsulating repository provider configurations.
 */
sealed class ProviderCommand(
    name: String,
    help: String,
) : CliktCommand(name = name, help = help) {

    private val criteriaOptions by CriteriaOptions()
        .cooccurring()

    /**
     * The repository [URL]s to use.
     */
    val url by option(help = URL_HELP_MSG)
        .convert { URL(it) }
        .split(",")

    /**
     * The [SearchCriteria] to use.
     */
    val criteria by lazy {
        criteriaOptions?.criteria
    }

    override fun run() = require(url != null || criteriaOptions != null) {
        "At least one between url and criteria must be valued in `$commandName` command."
    }

    companion object {
        private const val URL_HELP_MSG = "The URL address of the repository."
        private const val SERVICE_HELP_MSG = "The hosting service."
        private const val USER_HELP_MSG = "The hosting service username of the repositories owner to search."
        private const val REPO_NAME_HELP_MSG = "The name of the searched repositories."
    }

    private inner class CriteriaOptions : OptionGroup("Options to specify for search by criteria") {

        private val service by option(help = SERVICE_HELP_MSG)
            .choice(*SupportedOptions.services.map(HostingService::name).toTypedArray())
            .split(",")
            .required()
        private val user by option(help = USER_HELP_MSG)
            .split(",")
            .required()
        private val repositoryName by option(help = REPO_NAME_HELP_MSG)
            .split(",")
            .required()

        /**
         * Gets a sequence of configured [SearchCriteria], with all combinations of given options.
         */
        val criteria: Sequence<SearchCriteria<*, *>> by lazy {
            service.map(this::serviceBy)
                .flatMap { s -> user.flatMap { u -> repositoryName.map { byCriteria(s, u, it) } } }
                .asSequence()
        }

        private fun serviceBy(name: String): HostingService =
            SupportedOptions.services.find { it.name == name } ?: error("$name not supported!")

        private fun byCriteria(service: HostingService, user: String, repositoryName: String?): SearchCriteria<*, *> =
            when (service) {
                GitHub -> repositoryName?.let { ByGitHubName(it, ByGitHubUser(user)) } ?: ByGitHubUser(user)
                BitBucket -> repositoryName?.let { ByBitbucketName(it, ByBitbucketUser(user)) } ?: ByBitbucketUser(user)
            }
    }
}
