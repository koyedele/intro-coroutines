package tasks

import contributors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    coroutineScope {
        val repos = service
            .getOrgRepos(req.org)
            .also { logRepos(req, it) }
            .body() ?: emptyList()

        val channel = Channel<List<User>>()
        repos.map { repo ->
            launch {
                log("start loading contributors for ${repo.name}")
                val users = service
                    .getRepoContributors(req.org, repo.name)
                    .also { logUsers(repo, it) }
                    .bodyList()
                channel.send(users)
            }
        }

        var allUsers = emptyList<User>()
        repeat(repos.size) { i ->
            val users = channel.receive()
            allUsers = (allUsers + users).aggregate()
            updateResults(allUsers, i == repos.lastIndex)
        }
    }
}
