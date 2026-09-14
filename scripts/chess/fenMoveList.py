from chess import Board


def main():
    board = Board()

    board.set_fen('r3k2r/Pp1p1ppp/1b3nbN/nPp5/BBP1P3/q4N2/Pp1P2PP/R2Q1R1K w kq - 0 2')

    print(board.legal_moves)
    print(board.legal_moves.count())


if __name__ == '__main__':
    main()
