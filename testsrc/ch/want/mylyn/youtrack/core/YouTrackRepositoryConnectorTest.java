package ch.want.mylyn.youtrack.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class YouTrackRepositoryConnectorTest {

    @ParameterizedTest
    @CsvSource({ "https://solivagant.youtrack.cloud/issue/DEMO-11/Import-issues-and-articles,DEMO-11", //
        "https://solivagant.youtrack.cloud/issue/DEMO-7,DEMO-7", //
        "https://solivagant.youtrack.cloud/issue/DEMO-7?id=177-1,DEMO-7", // with query param
        ",", // null value
        "https://solivagant.youtrack.cloud/dashboard?id=177-1,", // some other URL
    })
    void getTaskIdFromTaskUrl(final String url, final String expected) {
        final YouTrackRepositoryConnector testee = new YouTrackRepositoryConnector();
        // act
        final String actual = testee.getTaskIdFromTaskUrl(url);
        // assert
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({ "https://solivagant.youtrack.cloud/issue/DEMO-11/Import-issues-and-articles,https://solivagant.youtrack.cloud", //
        "https://solivagant.youtrack.cloud/dashboard?id=177-1,", // some other URL
    })
    void getRepositoryUrlFromTaskUrl(final String url, final String expected) {
        final YouTrackRepositoryConnector testee = new YouTrackRepositoryConnector();
        // act
        final String actual = testee.getRepositoryUrlFromTaskUrl(url);
        // assert
        Assertions.assertEquals(expected, actual);
    }
}
