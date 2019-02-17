/******************************* td2_2.c *******************************/

#include "fthread.h"
#include "stdio.h"
#include "unistd.h"
#include "traceinstantsf.h"

ft_event_t  evt;
ft_thread_t ft_generator;

void awaiter (void *args)
{
  printf("Debut de awaiter %d\n", (long) args);
  ft_thread_await(evt);
  printf("Fin de awaiter %d\n", (long) args);
}

void generator (void *args)
{
  printf("Debut de generator\n");
  ft_thread_generate(evt);
  printf("Cooperate de generator\n");
  ft_thread_cooperate();
  printf("Fin de generator\n");
}



int main (void)
{
  ft_thread_t ft_trace, ft_awaiter, ft_generator;
  ft_scheduler_t sched = ft_scheduler_create ();

  evt		= ft_event_create(sched);

  ft_trace	= ft_thread_create(sched, traceinstants, NULL, (void *)50);

  ft_awaiter	= ft_thread_create(sched, awaiter, NULL, (void *)1);
  ft_awaiter	= ft_thread_create(sched, awaiter, NULL, (void *)2);
  ft_awaiter	= ft_thread_create(sched, awaiter, NULL, (void *)3);
  ft_generator	= ft_thread_create(sched, generator, NULL, NULL);
  ft_awaiter	= ft_thread_create(sched, awaiter, NULL, (void *)4);
  ft_awaiter	= ft_thread_create(sched, awaiter, NULL, (void *)5);
  ft_awaiter	= ft_thread_create(sched, awaiter, NULL, (void *)6);
 
  ft_scheduler_start(sched);
  

  ft_exit ();
  return 0;
}


/**********

$ ./AwaitsGenerator
>>>>>>>>>>> instant 0 :
Debut de awaiter 1
Debut de awaiter 2
Debut de awaiter 3
Debut de generator
Cooperate de generator
Debut de awaiter 4
Fin de awaiter 4
Debut de awaiter 5
Fin de awaiter 5
Debut de awaiter 6
Fin de awaiter 6
Fin de awaiter 1
Fin de awaiter 2
Fin de awaiter 3
>>>>>>>>>>> instant 1 :
Fin de generator
>>>>>>>>>>> instant 2 :
>>>>>>>>>>> instant 3 :
>>>>>>>>>>> instant 4 :
>>>>>>>>>>> instant 5 :
>>>>>>>>>>> instant 6 :

**********/
