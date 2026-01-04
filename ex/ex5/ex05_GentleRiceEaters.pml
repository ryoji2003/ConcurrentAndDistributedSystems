int free = 0;
int eat = 0;

active [2] proctype Eater() {
    int my = 0;

    if
    :: (_pid == 0) -> 
        my = 2;
    :: else -> 
        my = 0;
    fi;

    do
    :: free >= 1 -> 
        atomic { free--; my++ }
    :: free == 0 && my == 1 -> 
        atomic { my--; free++ }
    :: my == 2 -> 
        progress:
        eat++;
        atomic { free=2; my=0; eat-- }
    od;
}

ltl p1 { <> (eat > 0) }

ltl p2 { []<> (eat > 0) }