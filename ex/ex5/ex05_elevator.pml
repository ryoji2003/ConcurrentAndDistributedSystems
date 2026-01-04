bool doors_open = true;
int state = 1;

// 1 = down, 2 = moving up, 3 = up, 4 = moving down

active proctype main()
{
do
:: state == 1 && doors_open -> doors_open = false; state = 2;
:: state == 2 -> state = 3;
:: state == 3 && !doors_open -> doors_open = true;
:: state == 3 && doors_open -> doors_open = false; state = 4;
:: state == 4 -> state = 1;
:: state == 1 && !doors_open -> doors_open = true;
od;
} 
// 1
ltl p1 { []<> (state == 1 && doors_open) }
// 2
ltl p2 { [] (doors_open -> (state == 1 || state == 3)) }
// 3
ltl p3 { [] (!doors_open -> <> (state == 2 || state == 4)) }