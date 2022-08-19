package provider

import org.kohsuke.github.GHRepository
import java.io.InputStream

private const val RECURSION = 1

/**
 * A GitHub repository adapter.
 * @property repository the [GHRepository] to be adapted
 */
data class GitHubRepository(private val repository: GHRepository) : AbstractRepository() {
    override val name: String
        get() = repository.name

    override val contributors: Iterable<String>
        get() = repository.listContributors().map { it.name }

    override fun extractSources(languageExtensions: Iterable<String>): Iterable<InputStream> {
        val defaultBranch = repository.defaultBranch
            ?: error("The repository has not yet appropriately configured: no default branch configured.")
        return repository.getTreeRecursive(defaultBranch, RECURSION).tree
            .asSequence()
            .filter { it.type == "blob" && extensionFilter(it.path, languageExtensions) }
            .map { it.readAsBlob() }
            .toSet()
    }

    private fun extensionFilter(path: String, extensions: Iterable<String>) =
        extensions.any { path.endsWith(it) }
}