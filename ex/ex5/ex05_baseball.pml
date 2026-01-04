int A[10];
int i = 0;

active [10] proctype Setup() {
    atomic {
        A[i] = _pid;
        i++;
    }
}

active proctype PuzzleSolver() {
    i == 10;

    int B = A[0];
    int Val_A = A[1];
    int S = A[2];
    int E = A[3];
    int L = A[4];
    int G = A[5];
    int M = A[6];

    int Val_Base  = 1000*B + 100*Val_A + 10*S + E;
    int BALL  = 1000*B + 100*Val_A + 10*L + L;
    int GAMES = 10000*G + 1000*Val_A + 100*M + 10*E + S;

    if
    :: (B != 0 && G != 0) ->
        printf("Check: %d%d%d%d + %d%d%d%d = %d%d%d%d%d\n", B,Val_A,S,E, B,Val_A,L,L, G,Val_A,M,E,S);
        assert(Val_Base + BALL != GAMES);
    :: else ->
        skip;
    fi;
}