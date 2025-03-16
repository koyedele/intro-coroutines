package tasks

import contributors.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class Request9RxProgressKtTest {
    @BeforeEach
    fun setUp() {
        testScheduler.advanceTimeTo(0, TimeUnit.MILLISECONDS)
    }

    @Test
    fun loadContributorsReactiveProgress() {
        val testObserver =
            tasks.loadContributorsReactiveProgress(MockGithubService, testRequestData, testScheduler).test()
        testObserver.assertValueAt(0, emptyList())

        val startTime = testScheduler.now(TimeUnit.MILLISECONDS)

        concurrentProgressResults.forEachIndexed { index: Int, expected: TestResults ->
            println("index: $index expected: $expected")
            testScheduler.advanceTimeTo(expected.timeFromStart, TimeUnit.MILLISECONDS)
            testObserver.assertValueAt(index + 1, expected.users)

            val time = testScheduler.now(TimeUnit.MILLISECONDS) - startTime
            Assertions.assertEquals(expected.timeFromStart, time,
                "Expected intermediate result after virtual ${expected.timeFromStart} ms:")
        }
        testObserver.assertComplete()
    }
}