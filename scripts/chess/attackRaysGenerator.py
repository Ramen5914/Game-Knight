import numpy as np

def main():
    print('{')
    for i in range(64):
        print('\t', end='')
        print(get_long_str(make_long(i)), end='')
        if i != 63:
            print(',')
    print('\n}')

def get_vertical_bits(x: int, y: int) -> np.uint64:
    long = 0
    start_bit = 1 << x
    for i in range(8):
        if i != y:
            long |= start_bit << i*8

    return np.uint64(long)

def get_horizontal_bits(x: int, y: int) -> np.uint64:
    long = 0
    start_bit = 1 << y*8
    for i in range(8):
        if i != x:
            long |= start_bit << i

    return np.uint64(long)


def get_northeast_bits(start_bit: int, x: int, y: int) -> np.uint64:
    long = np.uint64()

    i = 0
    while x != 7 and y != 7:
        long |= start_bit << 9 * (i + 1)
        x += 1
        y += 1
        i += 1

    return np.uint64(long)

def get_southeast_bits(start_bit: int, x: int, y: int) -> np.uint64:
    long = np.uint64()

    i = 0
    while x != 7 and y != 0:
        long |= start_bit >> 7 * (i + 1)
        x += 1
        y -= 1
        i += 1

    return np.uint64(long)


def get_southwest_bits(start_bit: int, x: int, y: int) -> np.uint64:
    long = np.uint64()

    i = 0
    while x != 0 and y != 0:
        long |= start_bit >> 9 * (i + 1)
        x -= 1
        y -= 1
        i += 1

    return np.uint64(long)

def get_northwest_bits(start_bit: int, x: int, y: int) -> np.uint64:
    long = np.uint64()

    i = 0
    while x != 0 and y != 7:
        long |= start_bit << 7 * (i + 1)
        x -= 1
        y += 1
        i += 1

    return np.uint64(long)

def get_diagonal_bits(start_bit: int, x: int, y: int) -> np.uint64:
    northeast_bits = get_northeast_bits(start_bit, x, y)
    southeast_bits = get_southeast_bits(start_bit, x, y)
    southwest_bits = get_southwest_bits(start_bit, x, y)
    northwest_bits = get_northwest_bits(start_bit, x, y)

    return northeast_bits | southeast_bits | southwest_bits | northwest_bits

def get_knight_bits(start_bit: int, x: int, y: int) -> np.uint64:
    long = np.uint64()

    if y >= 1 and x >= 2:
        long |= start_bit >> 10
    if y >= 2 and x >= 1:
        long |= start_bit >> 17
    if y >= 1 and x <= 6:
        long |= start_bit >> 6
    if y >= 2 and x <= 6:
        long |= start_bit >> 15
    if y <= 6 and x >= 2:
        long |= start_bit << 6
    if y <= 5 and x >= 1:
        long |= start_bit << 15
    if y <= 6 and x <= 5:
        long |= start_bit << 10
    if y <= 5 and x <= 6:
        long |= start_bit << 17

    return np.uint64(long)

def make_long(s8x8: int) -> np.uint64:
    x: int = s8x8 % 8
    y: int = s8x8 // 8

    vertical_bits: np.uint64 = get_vertical_bits(x, y)
    horizontal_bits = get_horizontal_bits(x, y)

    start_bit = 1 << (y * 8 + x)

    diagonal_bits = get_diagonal_bits(start_bit, x, y)
    knight_bits = get_knight_bits(start_bit, x, y)

    return vertical_bits | horizontal_bits | diagonal_bits | knight_bits

def print_long(long: np.uint64) -> None:
    for y in reversed(range(8)):
        for x in range(8):
            print((long >> (y*8 + x)) & 0b1, end='')
        print()

def get_long_str(long: np.uint64) -> str:
   return '0b' + str(bin(long))[2:].zfill(64)

if __name__ == '__main__':
    main()