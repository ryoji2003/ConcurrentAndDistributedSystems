#define N 5

int A[N];
int max_val = 0;
bool ready = false;
int done_count = 0;

active proctype Main() {
    A[0] = 10; A[1] = 55; A[2] = 3; A[3] = 99; A[4] = 20;
    
    printf("Array: %d, %d, %d, %d, %d\n", A[0], A[1], A[2], A[3], A[4]);
    
    ready = true;
    
    done_count == N;
    
    printf("The Maximum value is: %d\n", max_val);
}

active [N] proctype Worker() {
    ready == true;
    
    int my_val = A[_pid - 1];
    
    atomic {
        if
        :: my_val > max_val -> 
            max_val = my_val;
            printf("Worker %d updated max to %d\n", _pid, max_val);
        :: else -> skip;
        fi;
        
        done_count++;
    }
}