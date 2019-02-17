/******************************* automate.canevas ****************************/

#include "fthread.h"
#include "stdio.h"
#include "unistd.h"
#include "traceinstantsf.h"

ft_event_t  event1, event2;

/* ?????????? */

void generator (void *args)
{
  int i;

  for (i=0; i < 15; ++i) {
      /* ?????????? */
  }
}


int main ()
{
  ft_scheduler_t sched = ft_scheduler_create();
  event1 = ft_event_create(sched);
  event2 = ft_event_create(sched);

  ft_thread_create(sched, traceinstants, NULL, (void *)15);
  if (NULL == ft_automaton_create(sched, autom, NULL, NULL)) {
     fprintf(stdout, "Cannot create automaton!!!\n");
  }

  ft_thread_create(sched, generator, NULL, NULL);

  ft_scheduler_start(sched);
  

  ft_exit();
  return 0;
}
