int sticks = 2; // rice-eating example

active [2] proctype P1() {
  atomic { sticks > 0;    // wait for a stick
           sticks--; }	   // take a stick
  atomic { sticks > 0;    // wait for a stick
           sticks--; }	   // take a stick
  sticks++;   // eat and put the sticks back
  sticks++;
  assert(sticks == 2);
}
