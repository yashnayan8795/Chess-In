package test.com.chess.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Master test suite — runs all chess engine tests.
 * Execute with: mvn test -pl chess-tests
 */
@Suite
@SelectClasses({
    TestBoard.class,
    TestPieces.class,
    TestCheckmate.class,
    TestStaleMate.class,
    TestCastling.class,
    TestMiniMax.class,
    TestPGNParser.class
})
public class ChessTestSuite {
    // JUnit 5 Suite runner — no body needed
}
