package chess.domain.piece;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KingTest {

    @ParameterizedTest
    @CsvSource({"BLACK, true", "WHITE, false"})
    @DisplayName("해당 팀이 검정 팀인지 확인한다.")
    void isBlackTeamTest(Team team, boolean expected) {
        chess.domain.piece.King king = new chess.domain.piece.King(team);

        assertThat(king.isBlackTeam()).isEqualTo(expected);
    }
}