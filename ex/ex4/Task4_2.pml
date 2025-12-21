#define N 5
#define P 3

int A[N];
int R[N];
int i = 0;
int turn = 1;

active proctype Main() {
    A[0] = 3; A[1] = 1; A[2] = 3; A[3] = 2; A[4] = 1;
    
    printf("Main: Array initialized: %d, %d, %d, %d, %d\n", A[0], A[1], A[2], A[3], A[4]);

    turn > P ->
    
    printf("Result: %d, %d, %d, %d, %d\n", R[0], R[1], R[2], R[3], R[4]);
}

active [P] proctype Worker() {
    int p_count = 0;
    int j = 0;
    
    do
    :: j < N ->
        if
        :: A[j] == _pid -> p_count++;
        :: else -> skip;
        fi;
        j++;
    :: else -> break;
    od;
    
    printf("Worker %d found %d occurrences.\n", _pid, p_count);
    
    atomic {
        turn == _pid; 
        
        j = 0;
        do
        :: j < p_count ->
            R[i] = _pid;
            i++;
            j++;
        :: else -> break;
        od;
        
        turn++;
    }
}