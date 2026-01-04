	switch (t->back) {
	default: Uerror("bad return move");
	case  0: goto R999; /* nothing to undo */

		 /* CLAIM p3 */
;
		;
		;
		;
		
	case 5: // STATE 13
		;
		p_restor(II);
		;
		;
		goto R999;

		 /* CLAIM p2 */
;
		
	case 6: // STATE 1
		goto R999;

	case 7: // STATE 10
		;
		p_restor(II);
		;
		;
		goto R999;

		 /* CLAIM p1 */
;
		;
		;
		;
		
	case 10: // STATE 13
		;
		p_restor(II);
		;
		;
		goto R999;

		 /* PROC main */
;
		;
		
	case 12: // STATE 2
		;
		now.doors_open = trpt->bup.oval;
		;
		goto R999;

	case 13: // STATE 3
		;
		now.state = trpt->bup.oval;
		;
		goto R999;
;
		;
		
	case 15: // STATE 5
		;
		now.state = trpt->bup.oval;
		;
		goto R999;
;
		;
		
	case 17: // STATE 7
		;
		now.doors_open = trpt->bup.oval;
		;
		goto R999;
;
		;
		
	case 19: // STATE 9
		;
		now.doors_open = trpt->bup.oval;
		;
		goto R999;

	case 20: // STATE 10
		;
		now.state = trpt->bup.oval;
		;
		goto R999;
;
		;
		
	case 22: // STATE 12
		;
		now.state = trpt->bup.oval;
		;
		goto R999;
;
		;
		
	case 24: // STATE 14
		;
		now.doors_open = trpt->bup.oval;
		;
		goto R999;

	case 25: // STATE 18
		;
		p_restor(II);
		;
		;
		goto R999;
	}

