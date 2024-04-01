package chess.dto;

import chess.domain.Board;
import chess.domain.piece.Piece;
import chess.domain.piece.Team;
import chess.domain.position.Position;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BoardDto {

    private final Map<Position, PieceDto> boardDto;
    private final Team turn;

    private BoardDto(Map<Position, PieceDto> boardDto, Team turn) {
        this.boardDto = boardDto;
        this.turn = turn;
    }

    public static BoardDto of(Board board) {
        List<Position> positions = Position.ALL_POSITIONS;
        Map<Position, PieceDto> boardDto = new HashMap<>();
        positions.forEach(position -> addPiece(board, position, boardDto));
        Team turn = board.getTurn();
        return new BoardDto(boardDto, turn);
    }

    private static void addPiece(Board board, Position position, Map<Position, PieceDto> boardDto) {
        Optional<Piece> optionalPiece = board.find(position);

        if (optionalPiece.isEmpty()) {
            return;
        }

        Piece piece = optionalPiece.get();
        PieceDto pieceDto = PieceDto.from(piece);
        boardDto.put(position, pieceDto);
    }

    public Optional<PieceDto> find(Position position) {
        return Optional.ofNullable(boardDto.get(position));
    }

    public Map<Position, PieceDto> getBoardDto() {
        return boardDto;
    }

    public String getTurn() {
        return turn.toString();
    }
}
