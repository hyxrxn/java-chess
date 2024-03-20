package chess.domain.movement;

import chess.domain.position.Position;
import chess.domain.position.Rank;
import java.util.List;

public class WhitePawnFirstMovement implements MovementRule {

    @Override
    public boolean isMovable(Position start, Position end) {
        int rankDifference = start.calculateRankDifference(end);
        int fileDifference = start.calculateFileDifference(end);

        return start.isSameRank(Rank.TWO) && rankDifference == 2 && fileDifference == 0;
    }

    @Override
    public List<Position> findPath(Position start, Position end) {
        return List.of(start.moveToNorth(), end);
    }
}
