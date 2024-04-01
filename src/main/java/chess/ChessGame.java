package chess;

import chess.domain.Board;
import chess.domain.BoardFactory;
import chess.domain.ScoreCalculator;
import chess.domain.piece.Team;
import chess.domain.position.Position;
import chess.dto.BoardDto;
import chess.view.GameCommand;
import chess.view.InputView;
import chess.view.OutputView;

public class ChessGame {

    private final InputView inputView;
    private final OutputView outputView;

    public ChessGame(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void tryStart() {
        try {
            outputView.printStartGame();
            start();
        } catch (IllegalArgumentException exception) {
            outputView.printExceptionMessage(exception);
            tryStart();
        }
    }

    private void start() {
        GameCommand command = inputView.readCommand();
        if (command == GameCommand.START) {
            Board board = getBoard();
            showBoard(board);
            play(board);
            return;
        }
        if (GameCommand.isImpossibleBeforeStartGame(command)) {
            throw new IllegalArgumentException("아직 게임을 시작하지 않았습니다.");
        }
    }

    private Board getBoard() {
        BoardDao boardDao = new BoardDao();
        if (boardDao.isExistBoard(1)) {
            return boardDao.loadBoard(1);
        }
        return BoardFactory.createInitialBoard();
    }

    private void play(Board board) {
        GameStatus gameStatus = GameStatus.PLAYING;
        while (GameStatus.isPlaying(gameStatus)) {
            gameStatus = tryProcessTurn(board);
        }
    }

    private GameStatus tryProcessTurn(Board board) {
        try {
            GameCommand command = inputView.readCommand();
            return processTurn(command, board);
        } catch (IllegalArgumentException exception) {
            outputView.printExceptionMessage(exception);
            tryProcessTurn(board);
        }
        return GameStatus.PLAYING;
    }

    private GameStatus processTurn(GameCommand command, Board board) {
        if (command == GameCommand.START) {
            throw new IllegalArgumentException("이미 게임을 시작했습니다.");
        }
        if (command == GameCommand.END) {
            return endGame(board);
        }
        if (command == GameCommand.STATUS) {
            return showStatus(board);
        }
        return executeMove(board);
    }

    private GameStatus endGame(Board board) {
        BoardDao boardDao = new BoardDao();
        boardDao.delete(1);

        BoardDto boardDto = BoardDto.of(board);
        boardDao.addGame(1, boardDto);
        boardDao.addBoard(1, boardDto);

        return GameStatus.END;
    }

    private GameStatus showStatus(Board board) {
        ScoreCalculator scoreCalculator = new ScoreCalculator(board);

        double blackScore = scoreCalculator.getBlackScore();
        double whiteScore = scoreCalculator.getWhiteScore();
        Team winner = scoreCalculator.chooseWinner();

        outputView.printStatus(blackScore, whiteScore, winner);
        return GameStatus.PLAYING;
    }

    private GameStatus executeMove(Board board) {
        Position start = inputView.readPosition();
        Position end = inputView.readPosition();
        GameStatus gameStatus = board.tryMove(start, end);
        showBoard(board);
        if (gameStatus != GameStatus.PLAYING) {
            outputView.printWinner(gameStatus);
            BoardDao boardDao = new BoardDao();
            boardDao.delete(1);
        }
        return gameStatus;
    }

    private void showBoard(Board board) {
        BoardDto boardDto = BoardDto.of(board);
        outputView.printBoard(boardDto);
    }
}
