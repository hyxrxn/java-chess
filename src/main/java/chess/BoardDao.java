package chess;

import chess.domain.Board;
import chess.domain.piece.Piece;
import chess.domain.piece.PieceType;
import chess.domain.piece.Team;
import chess.domain.position.File;
import chess.domain.position.Position;
import chess.domain.position.Rank;
import chess.dto.BoardDto;
import chess.dto.PieceDto;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BoardDao {

    private static final String SERVER = "localhost:13306"; // MySQL 서버 주소
    private static final String DATABASE = "chess"; // MySQL DATABASE 이름
    private static final String OPTION = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root"; //  MySQL 서버 아이디
    private static final String PASSWORD = "root"; // MySQL 서버 비밀번호

    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + SERVER + "/" + DATABASE + OPTION, USERNAME, PASSWORD);
        } catch (final SQLException e) {
            System.err.println("DB 연결 오류:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void addGame(final int gameId, final BoardDto boardDto) {
        final var query = "INSERT INTO game VALUES(?, ?)";
        try (final var connection = getConnection();
             final var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);
            preparedStatement.setString(2, boardDto.getTurn());
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addBoard(final int gameId, final BoardDto boardDto) {
        Map<Position, PieceDto> board = boardDto.getBoardDto();

        final var query = "INSERT INTO board VALUES(?, ?, ?, ?, ?)";
        try (final var connection = getConnection();
             final var preparedStatement = connection.prepareStatement(query)) {
            for (Entry<Position, PieceDto> pieceEntry : board.entrySet()) {
                preparedStatement.setInt(1, gameId);
                preparedStatement.setString(2, pieceEntry.getKey().getFile());
                preparedStatement.setString(3, pieceEntry.getKey().getRank());
                preparedStatement.setString(4, pieceEntry.getValue().getType());
                preparedStatement.setString(5, pieceEntry.getValue().getTeam());
                preparedStatement.executeUpdate();
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int gameId) {
        deleteGame(gameId);
        deleteBoard(gameId);
    }

    private void deleteGame(int gameId) {
        final var query = "DELETE FROM game WHERE gameId = ?";
        try (final var connection = getConnection();
             final var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteBoard(int gameId) {
        final var query = "DELETE FROM board WHERE gameId = ?";
        try (final var connection = getConnection();
             final var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Board loadBoard(int gameId) {
        Team turn = getGameTurn(gameId);
        Map<Position, Piece> pieces = getBoardPieces(gameId);
        return new Board(pieces, turn);
    }

    private Team getGameTurn(int gameId) {
        final String query = "SELECT turn FROM game WHERE gameId = ?";
        try (final var connection = getConnection();
             final var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String turn = resultSet.getString("turn");
                    return Team.valueOf(turn.toUpperCase());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("게임 턴 정보 조회 실패", e);
        }
        throw new IllegalArgumentException("해당 게임 ID를 찾을 수 없습니다.");
    }

    private Map<Position, Piece> getBoardPieces(int gameId) {
        Map<Position, Piece> pieces = new HashMap<>();
        final String query = "SELECT file, `rank`, type, team FROM board WHERE gameId = ?";
        try (final var connection = getConnection();
             final var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    File file = File.valueOf(resultSet.getString("file").toUpperCase());
                    Rank rank = Rank.valueOf(resultSet.getString("rank").toUpperCase());
                    PieceType type = PieceType.valueOf(resultSet.getString("type").toUpperCase());
                    Team team = Team.valueOf(resultSet.getString("team").toUpperCase());
                    Position position = new Position(file, rank);
                    Piece piece = type.createPiece(team);
                    pieces.put(position, piece);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("보드 정보 조회 실패", e);
        }
        return pieces;
    }

    public boolean isExistBoard(int gameId) {
        final String query = "SELECT EXISTS(SELECT 1 FROM game WHERE gameId = ?) AS Exist";
        boolean exists = false;
        try (final var connection = getConnection();
             final var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, gameId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getBoolean("Exist");
                }
                return exists;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }
}
