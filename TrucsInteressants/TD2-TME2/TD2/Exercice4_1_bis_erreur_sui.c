/******************************* td2_2.c *******************************/

#include "fthread.h"
#include "stdio.h"
#include "unistd.h"
#include "traceinstantsf.h"

ft_event_t  evt;

void awaiter (void *args)
{
  int i, res, ft_generator;

  for (i = 0; i< 5; i++) {
    fprintf(stdout, "debut awater\n");
    res = ft_thread_await(evt);
    if (res == OK) {
       fprintf(stdout, "event received!\n");
       ft_thread_cooperate();
       ft_thread_get_value(evt, 0, (void **)(&ft_generator));
       printf("ft_generator = %ld et i == %d\n", ft_generator, i);
       /******* etonnant, i == 0 toujours.
                C'est un probleme d'allocation de place.
                Ici, on a declarer "int ft_generator" et on l'utilise comme un
                (void *) pointeur, plus grand donc qu'un entier.
                C = langage dangeureux. On le sait! *****/
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

  printf("ft_generator = %ld\n", ft_generator);

  ft_scheduler_start(sched);
  

  ft_exit ();
  return 0;
}
