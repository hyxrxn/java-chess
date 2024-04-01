package chess.domain.piece;

import java.util.Arrays;
import java.util.function.Function;

public enum PieceType {

    KING(King.class, 0, King::new),
    QUEEN(Queen.class, 9, Queen::new),
    ROOK(Rook.class, 5, Rook::new),
    BISHOP(Bishop.class, 3, Bishop::new),
    KNIGHT(Knight.class, 2.5, Knight::new),
    PAWN(Pawn.class, 1, Pawn::new);

    private final Class<? extends Piece> category;
    private final double score;
    private final Function<Team, Piece> pieceFactory;

    PieceType(Class<? extends Piece> category, double score, Function<Team, Piece> pieceFactory) {
        this.category = category;
        this.score = score;
        this.pieceFactory = pieceFactory;
    }

    public static PieceType from(Piece piece) {
        return Arrays.stream(PieceType.values())
                .filter(pieceType -> pieceType.category == piece.getClass())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 기물이 존재하지 않습니다."));
    }

    public static double scoreOf(Piece piece) {
        return from(piece).score;
    }

    public Piece createPiece(Team team) {
        return pieceFactory.apply(team);
    }
}
