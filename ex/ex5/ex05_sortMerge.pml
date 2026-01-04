int Semaphore = 2; 		// array sorting
bool sorted1 = false; 	// (previous lecture)
bool sorted2 = false; 
bool merging = false;

active proctype SortFirstHalf() 
    { sorted1 = true; Semaphore--; }

active proctype SortSecondHalf() 
    { sorted2 = true; Semaphore--; }

active proctype Merging(){
    Semaphore == 0;
    merging = true;
    assert(sorted1 && sorted2);
}