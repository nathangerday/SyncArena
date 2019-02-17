#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include "fthread.h"

#define NB_IMPRIMANTES 3
enum etat { LIBRE, OCCUPE };

typedef struct _INFO_CLIENT {
  int numero_client;
  int numero_imprimante;
} INFO_CLIENT;

typedef struct _INFO_TRACE {
  int numero_imprimante;
  int nb_tours;
} INFO_TRACE;

int alea (int n) {
  return (int)(1.0 * rand() / RAND_MAX * n);
}

ft_thread_t    traces[NB_IMPRIMANTES];
ft_scheduler_t sched_imps[NB_IMPRIMANTES];
enum etat      etats[NB_IMPRIMANTES];

/********** ?????????? **********/

void traceinstants (void *arg) {
  int numero_imprimante = 
    ((INFO_TRACE *)arg)->numero_imprimante;
  int nb_tours          = ((INFO_TRACE *)arg)->nb_tours;
  int i;

  for (i = 0; i < nb_tours; i++) {
    printf(">>>>>>>>>> Imprimante %d - Instant %d :\n", 
      numero_imprimante, i);
    fflush(stdout);
    ft_thread_cooperate ();
  }
  printf(">>>>>>>>>> Imprimante %d - Trace last exit\n", numero_imprimante);
  fflush(stdout);
}

void client (void *arg) {
  int numero_client, numero_imprimante, nb_instants;
  int fini;
  numero_client     = ((INFO_CLIENT *)arg)->numero_client;
  numero_imprimante = 
    ((INFO_CLIENT *)arg)->numero_imprimante;
  fini = 0;

  /********** ?????????? **********/
}

void technicien (void *arg) {
  long numero_imprimante = (long)arg;
  int  nb_instants;
  
  /********** ?????????? **********/
}

int main (void) {
  int          i;
  INFO_CLIENT *info_client;
  INFO_TRACE  *info_trace;
  ft_thread_t  ft_technicien;
  ft_thread_t  ft_clients[10];

  for (i = 0; i < NB_IMPRIMANTES; ++i) {
      sched_imps[i] = ft_scheduler_create ();
      etats[i]         = LIBRE;
      info_trace =
        (INFO_TRACE *)malloc(sizeof(INFO_TRACE));
      info_trace->numero_imprimante = i;
      info_trace->nb_tours          = 1000;
      traces[i] =
        ft_thread_create(sched_imps[i],
          traceinstants, NULL, (void *)info_trace);

      /********** ?????????? **********/
  }
  ft_technicien = ft_thread_create(sched_imps[0],
                    technicien, NULL, (void *)0);

  for (i = 0; i < NB_IMPRIMANTES; ++i) {
    ft_scheduler_start(sched_imps[i]);
  }
  for (i = 0; i < 10; ++i) {
    info_client =
      (INFO_CLIENT *)malloc(sizeof(INFO_CLIENT));
    info_client->numero_client     = i;
    info_client->numero_imprimante = alea(NB_IMPRIMANTES);
    ft_clients[i] =
      ft_thread_create(sched_imps[alea(NB_IMPRIMANTES)],
        client, NULL, (void *)info_client);
  }
  for (i = 0; i < 10; ++i) {
    ft_thread_join(ft_clients[i]);
  }
  ft_scheduler_stop(ft_technicien);
  for (i = 0; i < NB_IMPRIMANTES; ++i) {
      ft_scheduler_stop(traces[i]);
  }
  ft_exit();
  return 0;
}
