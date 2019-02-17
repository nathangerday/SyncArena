/******************************* td2_2.c *******************************/

#include "fthread.h"
#include "stdio.h"
#include "unistd.h"
#include "traceinstantsf.h"

ft_event_t  evt;

void awaiter (void *args)
{
  int i, res;
  long ft_generator;

  for (i = 0; i < 5; ++i) {
    fprintf(stdout, "debut awater\n");
    res = ft_thread_await(evt);
    if (res == OK) {
       fprintf(stdout, "event received!\n");
       ft_thread_cooperate();
       ft_thread_get_value(evt, 0, (void **)(&ft_generator));
    }
    if (res == ETIMEOUT)
       fprintf (stdout, "timeout! ");
  }
  fprintf(stdout, "stop generator!\n");
  ft_scheduler_stop(ft_generator);
}

void generator (void *args)
{
  int i;

  for (i=0;; ++i) {
    fprintf(stdout, "event number %d generated!\n", i);
    ft_thread_generate_value(evt, ft_thread_self());
    ft_thread_cooperate();
  }
}



int main (void)
{
  ft_thread_t ft_trace, ft_awaiter, ft_generator;
  ft_scheduler_t sched = ft_scheduler_create ();

  evt		= ft_event_create(sched);

  ft_trace	= ft_thread_create(sched, traceinstants, NULL, (void *)50);

  ft_awaiter	= ft_thread_create(sched, awaiter, NULL, NULL);
  ft_generator	= ft_thread_create(sched, generator, NULL, NULL);

  ft_scheduler_start(sched);
  

  ft_exit ();
  return 0;
}

/******

$ Exercice4_1_bis
>>>>>>>>>>> instant 0 :
debut awater
event number 0 generated!
event received!
>>>>>>>>>>> instant 1 :
event number 1 generated!
debut awater
event received!
>>>>>>>>>>> instant 2 :
event number 2 generated!
debut awater
event received!
>>>>>>>>>>> instant 3 :
event number 3 generated!
debut awater
event received!
>>>>>>>>>>> instant 4 :
event number 4 generated!
debut awater
event received!
>>>>>>>>>>> instant 5 :
event number 5 generated!
stop generator!
>>>>>>>>>>> instant 6 :
>>>>>>>>>>> instant 7 :
>>>>>>>>>>> instant 8 :
>>>>>>>>>>> instant 9 :
>>>>>>>>>>> instant 10 :
>>>>>>>>>>> instant 11 :
>>>>>>>>>>> instant 12 :
>>>>>>>>>>> instant 13 :
>>>>>>>>>>> instant 14 :
>>>>>>>>>>> instant 15 :
>>>>>>>>>>> instant 16 :
>>>>>>>>>>> instant 17 :
>>>>>>>>>>> instant 18 :
>>>>>>>>>>> instant 19 :
>>>>>>>>>>> instant 20 :
>>>>>>>>>>> instant 21 :
>>>>>>>>>>> instant 22 :
>>>>>>>>>>> instant 23 :
>>>>>>>>>>> instant 24 :
>>>>>>>>>>> instant 25 :
>>>>>>>>>>> instant 26 :
>>>>>>>>>>> instant 27 :
>>>>>>>>>>> instant 28 :
>>>>>>>>>>> instant 29 :
>>>>>>>>>>> instant 30 :
>>>>>>>>>>> instant 31 :
>>>>>>>>>>> instant 32 :
>>>>>>>>>>> instant 33 :
>>>>>>>>>>> instant 34 :
>>>>>>>>>>> instant 35 :
>>>>>>>>>>> instant 36 :
>>>>>>>>>>> instant 37 :
>>>>>>>>>>> instant 38 :
>>>>>>>>>>> instant 39 :
>>>>>>>>>>> instant 40 :
>>>>>>>>>>> instant 41 :
>>>>>>>>>>> instant 42 :
>>>>>>>>>>> instant 43 :
>>>>>>>>>>> instant 44 :
>>>>>>>>>>> instant 45 :
>>>>>>>>>>> instant 46 :
>>>>>>>>>>> instant 47 :
>>>>>>>>>>> instant 48 :
>>>>>>>>>>> instant 49 :
Last exit

*****/
