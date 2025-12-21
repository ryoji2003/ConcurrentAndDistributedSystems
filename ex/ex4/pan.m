#define rand	pan_rand
#define pthread_equal(a,b)	((a)==(b))
#if defined(HAS_CODE) && defined(VERBOSE)
	#ifdef BFS_PAR
		bfs_printf("Pr: %d Tr: %d\n", II, t->forw);
	#else
		cpu_printf("Pr: %d Tr: %d\n", II, t->forw);
	#endif
#endif
	switch (t->forw) {
	default: Uerror("bad forward move");
	case 0:	/* if without executable clauses */
		continue;
	case 1: /* generic 'goto' or 'skip' */
		IfNotBlocked
		_m = 3; goto P999;
	case 2: /* generic 'else' */
		IfNotBlocked
		if (trpt->o_pm&1) continue;
		_m = 3; goto P999;

		 /* PROC Worker */
	case 3: // STATE 1 - Task4_4.pml:21 - [((ready==1))] (0:0:0 - 1)
		IfNotBlocked
		reached[1][1] = 1;
		if (!((((int)now.ready)==1)))
			continue;
		_m = 3; goto P999; /* 0 */
	case 4: // STATE 2 - Task4_4.pml:25 - [my_val = A[(_pid-1)]] (0:0:1 - 1)
		IfNotBlocked
		reached[1][2] = 1;
		(trpt+1)->bup.oval = ((P1 *)_this)->my_val;
		((P1 *)_this)->my_val = now.A[ Index((((int)((P1 *)_this)->_pid)-1), 5) ];
#ifdef VAR_RANGES
		logval("Worker:my_val", ((P1 *)_this)->my_val);
#endif
		;
		_m = 3; goto P999; /* 0 */
	case 5: // STATE 3 - Task4_4.pml:27 - [((my_val>max_val))] (12:0:2 - 1)
		IfNotBlocked
		reached[1][3] = 1;
		if (!((((P1 *)_this)->my_val>now.max_val)))
			continue;
		/* merge: max_val = my_val(12, 4, 12) */
		reached[1][4] = 1;
		(trpt+1)->bup.ovals = grab_ints(2);
		(trpt+1)->bup.ovals[0] = now.max_val;
		now.max_val = ((P1 *)_this)->my_val;
#ifdef VAR_RANGES
		logval("max_val", now.max_val);
#endif
		;
		/* merge: printf('Worker %d updated max to %d\\n',_pid,max_val)(12, 5, 12) */
		reached[1][5] = 1;
		Printf("Worker %d updated max to %d\n", ((int)((P1 *)_this)->_pid), now.max_val);
		/* merge: .(goto)(12, 9, 12) */
		reached[1][9] = 1;
		;
		/* merge: done_count = (done_count+1)(12, 10, 12) */
		reached[1][10] = 1;
		(trpt+1)->bup.ovals[1] = now.done_count;
		now.done_count = (now.done_count+1);
#ifdef VAR_RANGES
		logval("done_count", now.done_count);
#endif
		;
		_m = 3; goto P999; /* 4 */
	case 6: // STATE 9 - Task4_4.pml:33 - [.(goto)] (0:12:1 - 2)
		IfNotBlocked
		reached[1][9] = 1;
		;
		/* merge: done_count = (done_count+1)(12, 10, 12) */
		reached[1][10] = 1;
		(trpt+1)->bup.oval = now.done_count;
		now.done_count = (now.done_count+1);
#ifdef VAR_RANGES
		logval("done_count", now.done_count);
#endif
		;
		_m = 3; goto P999; /* 1 */
	case 7: // STATE 7 - Task4_4.pml:30 - [(1)] (12:0:1 - 1)
		IfNotBlocked
		reached[1][7] = 1;
		if (!(1))
			continue;
		/* merge: .(goto)(12, 9, 12) */
		reached[1][9] = 1;
		;
		/* merge: done_count = (done_count+1)(12, 10, 12) */
		reached[1][10] = 1;
		(trpt+1)->bup.oval = now.done_count;
		now.done_count = (now.done_count+1);
#ifdef VAR_RANGES
		logval("done_count", now.done_count);
#endif
		;
		_m = 3; goto P999; /* 2 */
	case 8: // STATE 12 - Task4_4.pml:35 - [-end-] (0:0:0 - 1)
		IfNotBlocked
		reached[1][12] = 1;
		if (!delproc(1, II)) continue;
		_m = 3; goto P999; /* 0 */

		 /* PROC Main */
	case 9: // STATE 1 - Task4_4.pml:9 - [A[0] = 10] (0:0:1 - 1)
		IfNotBlocked
		reached[0][1] = 1;
		(trpt+1)->bup.oval = now.A[0];
		now.A[0] = 10;
#ifdef VAR_RANGES
		logval("A[0]", now.A[0]);
#endif
		;
		_m = 3; goto P999; /* 0 */
	case 10: // STATE 2 - Task4_4.pml:9 - [A[1] = 55] (0:0:1 - 1)
		IfNotBlocked
		reached[0][2] = 1;
		(trpt+1)->bup.oval = now.A[1];
		now.A[1] = 55;
#ifdef VAR_RANGES
		logval("A[1]", now.A[1]);
#endif
		;
		_m = 3; goto P999; /* 0 */
	case 11: // STATE 3 - Task4_4.pml:9 - [A[2] = 3] (0:0:1 - 1)
		IfNotBlocked
		reached[0][3] = 1;
		(trpt+1)->bup.oval = now.A[2];
		now.A[2] = 3;
#ifdef VAR_RANGES
		logval("A[2]", now.A[2]);
#endif
		;
		_m = 3; goto P999; /* 0 */
	case 12: // STATE 4 - Task4_4.pml:9 - [A[3] = 99] (0:0:1 - 1)
		IfNotBlocked
		reached[0][4] = 1;
		(trpt+1)->bup.oval = now.A[3];
		now.A[3] = 99;
#ifdef VAR_RANGES
		logval("A[3]", now.A[3]);
#endif
		;
		_m = 3; goto P999; /* 0 */
	case 13: // STATE 5 - Task4_4.pml:9 - [A[4] = 20] (0:0:1 - 1)
		IfNotBlocked
		reached[0][5] = 1;
		(trpt+1)->bup.oval = now.A[4];
		now.A[4] = 20;
#ifdef VAR_RANGES
		logval("A[4]", now.A[4]);
#endif
		;
		_m = 3; goto P999; /* 0 */
	case 14: // STATE 6 - Task4_4.pml:11 - [printf('Array: %d, %d, %d, %d, %d\\n',A[0],A[1],A[2],A[3],A[4])] (0:0:0 - 1)
		IfNotBlocked
		reached[0][6] = 1;
		Printf("Array: %d, %d, %d, %d, %d\n", now.A[0], now.A[1], now.A[2], now.A[3], now.A[4]);
		_m = 3; goto P999; /* 0 */
	case 15: // STATE 7 - Task4_4.pml:13 - [ready = 1] (0:0:1 - 1)
		IfNotBlocked
		reached[0][7] = 1;
		(trpt+1)->bup.oval = ((int)now.ready);
		now.ready = 1;
#ifdef VAR_RANGES
		logval("ready", ((int)now.ready));
#endif
		;
		_m = 3; goto P999; /* 0 */
	case 16: // STATE 8 - Task4_4.pml:15 - [((done_count==5))] (0:0:0 - 1)
		IfNotBlocked
		reached[0][8] = 1;
		if (!((now.done_count==5)))
			continue;
		_m = 3; goto P999; /* 0 */
	case 17: // STATE 9 - Task4_4.pml:17 - [printf('The Maximum value is: %d\\n',max_val)] (0:0:0 - 1)
		IfNotBlocked
		reached[0][9] = 1;
		Printf("The Maximum value is: %d\n", now.max_val);
		_m = 3; goto P999; /* 0 */
	case 18: // STATE 10 - Task4_4.pml:18 - [-end-] (0:0:0 - 1)
		IfNotBlocked
		reached[0][10] = 1;
		if (!delproc(1, II)) continue;
		_m = 3; goto P999; /* 0 */
	case  _T5:	/* np_ */
		if (!((!(trpt->o_pm&4) && !(trpt->tau&128))))
			continue;
		/* else fall through */
	case  _T2:	/* true */
		_m = 3; goto P999;
#undef rand
	}

