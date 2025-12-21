	switch (t->back) {
	default: Uerror("bad return move");
	case  0: goto R999; /* nothing to undo */

		 /* PROC Worker */
;
		;
		
	case 4: // STATE 2
		;
		((P1 *)_this)->my_val = trpt->bup.oval;
		;
		goto R999;

	case 5: // STATE 10
		;
		now.done_count = trpt->bup.ovals[1];
		now.max_val = trpt->bup.ovals[0];
		;
		ungrab_ints(trpt->bup.ovals, 2);
		goto R999;

	case 6: // STATE 10
		;
		now.done_count = trpt->bup.oval;
		;
		goto R999;

	case 7: // STATE 10
		;
		now.done_count = trpt->bup.oval;
		;
		goto R999;

	case 8: // STATE 12
		;
		p_restor(II);
		;
		;
		goto R999;

		 /* PROC Main */

	case 9: // STATE 1
		;
		now.A[0] = trpt->bup.oval;
		;
		goto R999;

	case 10: // STATE 2
		;
		now.A[1] = trpt->bup.oval;
		;
		goto R999;

	case 11: // STATE 3
		;
		now.A[2] = trpt->bup.oval;
		;
		goto R999;

	case 12: // STATE 4
		;
		now.A[3] = trpt->bup.oval;
		;
		goto R999;

	case 13: // STATE 5
		;
		now.A[4] = trpt->bup.oval;
		;
		goto R999;
;
		;
		
	case 15: // STATE 7
		;
		now.ready = trpt->bup.oval;
		;
		goto R999;
;
		;
		;
		;
		
	case 18: // STATE 10
		;
		p_restor(II);
		;
		;
		goto R999;
	}

