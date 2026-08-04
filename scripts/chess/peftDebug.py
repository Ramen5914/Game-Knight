import os
import re
import subprocess
from chess import Board

def main():
    board = Board()
    board.set_fen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1")

    fenPositions = []

    for move1 in board.legal_moves:
        board1 = board.copy()
        board1.push(move1)

        for move2 in board1.legal_moves:
            board2 = board1.copy()
            board2.push(move2)
            fenPositions.append(board2.fen())

            for move3 in board2.legal_moves:
                board3 = board2.copy()
                board3.push(move3)
                fenPositions.append(board3.fen())

    os.chdir("/")
    subprocess.run(["gradlew.bat", "build"], check=True)

    depth = 1

    i = 0
    for fen in fenPositions:
        # print(f'#{i+1}')

        javaOutput = subprocess.run(['java', '-jar', './build/libs/JavaChess-0.0.1.jar', fen, str(depth)],
                                    capture_output=True, text=True)

        javaPerft = int(javaOutput.stdout)
        stockfishPerft = run_stockfish_perft(fen, depth)

        if (javaPerft != stockfishPerft):
            print(f'FEN: {fen}\n\tMy Engine: {javaPerft}\n\tStockfish: {stockfishPerft}')

        i += 1


def run_stockfish_perft(fen: str, depth: int) -> int:
    stockfish_exe = r"C:/Users/omar/AppData/Roaming/org.encroissant.app/engines/stockfish/stockfish-windows-x86-64-avx2.exe"
    commands = f"position fen {fen}\ngo perft {depth}\nquit\n"

    result = subprocess.run(
        [stockfish_exe],
        input=commands,
        capture_output=True,
        text=True,
        check=True,
    )

    match = re.search(r"Nodes searched:\s*(\d+)", result.stdout)
    if not match:
        raise RuntimeError(f"Could not parse Stockfish output:\n{result.stdout}")

    return int(match.group(1))


if __name__ == '__main__':
    main()
