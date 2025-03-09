package tasks

import contributors.MockGithubService
import contributors.expectedConcurrentResults
import contributors.testRequestData
import contributors.testScheduler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class Request8RxKtTest {

    @BeforeEach
    fun setUp() {
        testScheduler.advanceTimeTo(0, TimeUnit.MILLISECONDS)
    }

    @Test
    fun loadContributorsReactive() {
        val testObserver = loadContributorsReactive(MockGithubService, testRequestData, testScheduler).test()
        testObserver.assertNoValues()

        val startTime = testScheduler.now(TimeUnit.MILLISECONDS)
        testScheduler.advanceTimeBy(expectedConcurrentResults.timeFromStart, TimeUnit.MILLISECONDS)
        testObserver.assertValue(expectedConcurrentResults.users)

        val totalTime = testScheduler.now(TimeUnit.MILLISECONDS) - startTime
        Assertions.assertEquals(expectedConcurrentResults.timeFromStart, totalTime,
            "The calls run concurrently, so the total virtual time should be 2200 ms: " +
                    "1000 ms for repos request plus max(1000, 1200, 800) = 1200 ms for concurrent contributors requests)"
        )
    }

}