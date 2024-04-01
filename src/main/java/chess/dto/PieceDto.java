package chess.dto;

import chess.domain.piece.Piece;
import chess.domain.piece.PieceType;
import chess.domain.piece.Team;

public class PieceDto {

    private final PieceType type;
    private final boolean isBlack;

    private PieceDto(PieceType type, boolean isBlack) {
        this.type = type;
        this.isBlack = isBlack;
    }

    static PieceDto from(Piece piece) {
        PieceType type = PieceType.from(piece);
        boolean isBlackTeam = piece.isBlackTeam();

        return new PieceDto(type, isBlackTeam);
    }

    public PieceType type() {
        return type;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public String getType() {
        return type.toString();
    }

    public String getTeam() {
        if (isBlack) {
            return Team.BLACK.toString();
        }
        return Team.WHITE.toString();
    }
}
