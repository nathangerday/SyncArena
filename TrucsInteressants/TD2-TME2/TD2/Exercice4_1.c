/******************************* td2_2.c *******************************/

#include "fthread.h"
#include "stdio.h"
#include "unistd.h"
#include "traceinstantsf.h"

ft_event_t  evt;

void awaiter (void *args)
{
  int i, res;

  for (i = 0; i< 5; i++) {
    fprintf(stdout, "debut awater\n");
    res = ft_thread_await(evt);
    if (res == OK)
       fprintf(stdout, "event received!\n");
    if (res == ETIMEOUT)
       fprintf (stdout, "timeout! ");
    ft_thread_cooperate_n(3);
  }
  fprintf(stdout, "stop generator!\n");
  ft_scheduler_stop((ft_thread_t)args);
}

void generator (void *args)
{
  int i;

  for (i=0;; ++i) {
    ft_thread_cooperate_n(7);
    fprintf(stdout, "event number %d generated!\n", i);
    ft_thread_generate(evt);
  }
}



int main (void)
{
  ft_thread_t ft_trace, ft_awaiter, ft_generator;
  ft_scheduler_t sched = ft_scheduler_create ();

  evt		= ft_event_create(sched);

  ft_trace	= ft_thread_create(sched, traceinstants, NULL, (void *)50);

  ft_generator	= ft_thread_create(sched, generator, NULL, NULL);
  ft_awaiter	= ft_thread_create(sched, awaiter, NULL, ft_generator);

  ft_scheduler_start(sched);
  

  ft_exit ();
  return 0;
}
