package tasks

import contributors.MockGithubService
import contributors.concurrentProgressResults
import contributors.testRequestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class Request7ChannelsKtTest {
    @Test
    fun testChannels() = runTest {
        val startTime = currentTime
        var index = 0
        loadContributorsChannels(MockGithubService, testRequestData) {
                users, _ ->
            val expected = concurrentProgressResults[index++]
            val time = currentTime - startTime

            // TODO: uncomment this assertion
            Assertions.assertEquals(expected.timeFromStart, time,
                "Expected intermediate result after virtual ${expected.timeFromStart} ms:")

            Assertions.assertEquals(expected.users, users, "Wrong intermediate result after $time:")
        }
    }
}
