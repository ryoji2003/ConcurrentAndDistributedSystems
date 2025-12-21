mtype = { HEADS, TAILS };

mtype p1_guess;
mtype p2_guess;
mtype coin;

proctype Player1() {
    if
    :: p1_guess = HEADS
    :: p1_guess = TAILS
    fi;
    printf("Player 1 guesses %e\n", p1_guess);
}

proctype Player2() {
    if
    :: p2_guess = HEADS
    :: p2_guess = TAILS
    fi;
    printf("Player 2 guesses %e\n", p2_guess);
}

proctype Referee() {
    p1_guess != 0 && p2_guess != 0;

    if
    :: coin = HEADS
    :: coin = TAILS
    fi;

    printf("Coin toss result: %e\n", coin);

    if
    :: (p1_guess == coin && p2_guess != coin) ->
        printf("Player 1 wins\n")
    :: (p2_guess == coin && p1_guess != coin) ->
        printf("Player 2 wins\n")
    :: else ->
        printf("Draw\n")
    fi;
}

init {
    atomic {
        run Player1();
        run Player2();
        run Referee();
    }
}