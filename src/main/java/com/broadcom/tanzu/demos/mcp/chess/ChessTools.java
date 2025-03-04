/*
 * Copyright (c) 2025 Broadcom, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.broadcom.tanzu.demos.mcp.chess;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.wolfraam.chessgame.ChessGame;
import io.github.wolfraam.chessgame.notation.NotationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
class ChessTools {
    private final Logger logger = LoggerFactory.getLogger(ChessTools.class);
    private final ChessEngine chessEngine;

    ChessTools(ChessEngine chessEngine) {
        this.chessEngine = chessEngine;
    }

    @Tool(name = "chess_guess_next_move", description = """
            Guess the next move to play in a chess game.
            """)
    NextMove guessNextMove(@ToolParam(description = "Board state in Forsyth-Edwards Notation") String fen) {
        logger.atDebug().log("Guessing next move from FEN: {}", fen);
        final var game = new ChessGame(fen);
        final var resp = chessEngine.getNextMove(game)
                .map(move -> game.getNotation(NotationType.UCI, move))
                .orElse(null);
        logger.atInfo().log("Guessed next move from FEN: {}=>{}", fen, resp);
        return new NextMove(fen, resp);
    }

    @Tool(name = "chess_is_legal_move", description = """
            Check if a move is legal in a chess game.
            """)
    MoveLegality isLegalMove(@ToolParam(description = "Board state in Forsyth-Edwards Notation") String fen,
                             @ToolParam(description = "Move in UCI format") String move) {
        logger.atDebug().log("Checking if the move {} is legal in FEN: {}", move, fen);
        final var game = new ChessGame(fen);
        final var resp = game.isLegalMove(game.getMove(NotationType.UCI, move));
        logger.atInfo().log("Is move {} legal in FEN {}? {}", move, fen, resp ? "yes" : "no");
        return new MoveLegality(fen, move, resp);
    }
}

@JsonClassDescription("A structure holding the legality of a move in a chess game")
record MoveLegality(
        @JsonPropertyDescription("Board state in Forsyth-Edwards Notation (FEN) before the move")
        String fen,
        @JsonPropertyDescription("Move to check in UCI format (for instance: d2d3)")
        String move,
        @JsonPropertyDescription("Move legality: true if the move is legal in the current board state")
        boolean legal
) {
}

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonClassDescription("A structure holding a board state and the best move to play in a chess game")
record NextMove(
        @JsonPropertyDescription("Board state in Forsyth-Edwards Notation (FEN)")
        String fen,
        @JsonPropertyDescription("Next move to play in UCI format (for instance: d2d3), if any")
        String nextMove
) {
}
