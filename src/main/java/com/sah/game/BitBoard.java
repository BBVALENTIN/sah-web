package com.sah.game;

public final class BitBoard {
   public static final long EMPTY = 0L;

   public static final long all = -1L;

   // E4,D4,E5,D5 8 - 1000
   public static final long center = 0x1818000000L;

   public static final long lightSquares = 0x55aa55aa55aa55aaL;
   public static final long blackSquares = 0x5aa5aa55aa55aa55L;
}
