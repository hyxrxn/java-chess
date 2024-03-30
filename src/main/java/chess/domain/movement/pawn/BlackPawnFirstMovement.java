package chess.domain.movement.pawn;

import chess.domain.movement.MovementRule;
import chess.domain.position.Position;
import chess.domain.position.Rank;
import java.util.List;

public final class BlackPawnFirstMovement implements MovementRule {

    public boolean isMovable(Position start, Position end, boolean hasEnemy) {
        int rankDifference = start.calculateRankDifference(end);
        int fileDifference = start.calculateFileDifference(end);

        return !hasEnemy && start.isOnSameRank(Rank.SEVEN) && rankDifference == -2 && fileDifference == 0;
    }

    public List<Position> findPath(Position start, Position end, boolean hasEnemy) {
        if (!isMovable(start, end, hasEnemy)) {
            throw new IllegalArgumentException("경로가 존재하지 않습니다.");
        }

        Position middle = start.moveToSouth();
        return List.of(middle);
    }
}
