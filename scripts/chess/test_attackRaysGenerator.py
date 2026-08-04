from unittest import TestCase

from scripts.chess.attackRaysGenerator import get_long_str, make_long


class Test(TestCase):
    def test_make_long(self):
        # Corners
        #                               8       7       6       5       4       3       2       1
        self.assertEqual("0b1000000101000001001000010001000100001001000001110000011111111110", get_long_str(make_long(0)))
        self.assertEqual("0b1000000110000010100001001000100010010000111000001110000001111111", get_long_str(make_long(7)))
        self.assertEqual("0b1111111000000111000001110000100100010001001000010100000110000001", get_long_str(make_long(56)))
        self.assertEqual("0b0111111111100000111000001001000010001000100001001000001010000001", get_long_str(make_long(63)))

        # Center Squares
        #                               8       7       6       5       4       3       2       1
        self.assertEqual("0b1000100001001001001111100011111011110111001111100011111001001001", get_long_str(make_long(27)))
        self.assertEqual("0b0001000110010010011111000111110011101111011111000111110010010010", get_long_str(make_long(28)))
        self.assertEqual("0b0100100100111110001111101111011100111110001111100100100110001000", get_long_str(make_long(35)))
        self.assertEqual("0b1001001001111100011111001110111101111100011111001001001000010001", get_long_str(make_long(36)))

        # Corners (diagonally in 1 towards center)
        #                               8       7       6       5       4       3       2       1
        self.assertEqual("0b1000001001000010001000100001001000001111000011111111110100001111", get_long_str(make_long(9)))
        self.assertEqual("0b0100000101000010010001000100100011110000111100001011111111110000", get_long_str(make_long(14)))
        self.assertEqual("0b0000111111111101000011110000111100010010001000100100001010000010", get_long_str(make_long(49)))
        self.assertEqual("0b1111000010111111111100001111000001001000010001000100001001000001", get_long_str(make_long(54)))

        # Corners (diagonally in 2 towards center)
        #                               8       7       6       5       4       3       2       1
        self.assertEqual("0b1000010001000100001001000001111100011111111110110001111100011111", get_long_str(make_long(18)))
        self.assertEqual("0b0010000100100010001001001111100011111000110111111111100011111000", get_long_str(make_long(21)))
        self.assertEqual("0b0001111100011111111110110001111100011111001001000100010010000100", get_long_str(make_long(42)))
        self.assertEqual("0b1111100011111000110111111111100011111000001001000010001000100001", get_long_str(make_long(45)))
