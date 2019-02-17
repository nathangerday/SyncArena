#include "fthread.h"
#include "pthread.h"
#include "stdio.h"
#include "traceinstantsf.h"

ft_event_t  evt1, evt2, evt3, evt4;

void run_p1 (void *rien) {
  int res;
  while(1){
    fprintf(stdout, "event1 waiting!\n");
    res = ft_thread_await(evt1);
    if (res == OK)
       fprintf(stdout, "event1 received!\n");
    if (res == ETIMEOUT)
       fprintf (stdout, "timeout1! ");
    ft_thread_generate(evt2);
    ft_thread_cooperate();
  }
  fprintf(stderr,"Fin de run_p1 \n");
}

void run_p2 (void *rien) {
  int res;
  while(1){
    fprintf(stdout, "event2 waiting!\n");
    res = ft_thread_await(evt2);
    if (res == OK)
       fprintf(stdout, "event2 received!\n");
    if (res == ETIMEOUT)
       fprintf (stdout, "timeout2! ");
    ft_thread_generate(evt3);
    ft_thread_cooperate();
  }
  fprintf(stderr,"Fin de run_p2 \n");
}

void run_p3 (void *rien) {
  int res;
  while(1){
    fprintf(stdout, "event3 waiting!\n");
    res = ft_thread_await(evt3);
    if (res == OK)
       fprintf(stdout, "event3 received!\n");
    if (res == ETIMEOUT)
       fprintf (stdout, "timeout3! ");
    ft_thread_generate(evt4);
    ft_thread_cooperate();
  }
  fprintf(stderr,"Fin de run_p3 \n");
}

void run_p4 (void *rien) {
  int res;
  while(1){
    fprintf(stdout, "event4 waiting!\n");
    res = ft_thread_await(evt4);
    if (res == OK)
       fprintf(stdout, "event4 received!\n");
    if (res == ETIMEOUT)
       fprintf (stdout, "timeout4! ");
    ft_thread_cooperate();
  }
  fprintf(stderr,"Fin de run_p4 \n");
}

void generator (void *args)
{
  int i;
  for (i=0; i < 5; ++i) {
    fprintf(stdout, "event number %d generated!\n", i);
    ft_thread_generate(evt1);
    ft_thread_cooperate();
  }
}

int main(void) {
  ft_scheduler_t sched = ft_scheduler_create ();
  ft_thread_t ft_trace = ft_thread_create(sched, traceinstants, NULL,
					  (void *)5);
  evt1		= ft_event_create(sched);
  evt2		= ft_event_create(sched);
  evt3		= ft_event_create(sched);
  evt4		= ft_event_create(sched);

  ft_thread_create (sched,generator,NULL,NULL);

  ft_thread_create (sched,run_p4,NULL,NULL);
  ft_thread_create (sched,run_p3,NULL,NULL);
  ft_thread_create (sched,run_p2,NULL,NULL);
  ft_thread_create (sched,run_p1,NULL,NULL);
    
  ft_scheduler_start (sched); 
  
  ft_exit();
  return 0;
}

/*************************
>>>>>>>>>>> instant 0 :
event number 0 generated!
event4 waiting!
event3 waiting!
event2 waiting!
event1 waiting!
event1 received!
event2 received!
event3 received!
event4 received!
>>>>>>>>>>> instant 1 :
event number 1 generated!
event4 waiting!
event3 waiting!
event2 waiting!
event1 waiting!
event1 received!
event2 received!
event3 received!
event4 received!
>>>>>>>>>>> instant 2 :
event number 2 generated!
event4 waiting!
event3 waiting!
event2 waiting!
event1 waiting!
event1 received!
event2 received!
event3 received!
event4 received!
>>>>>>>>>>> instant 3 :
...
************************/
