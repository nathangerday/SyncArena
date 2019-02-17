/********************* ft_await_generate_avec_fin.c **********************/

/***** A compiler avec

   gcc -m32 -o partiel_20150319 partiel_20150319.c \
       traceinstantsf.c -I $CHEMIN/ft_v1.1/include -L $CHEMIN/ft_v1.1/lib \
       -lfthread -lpthread
*****/

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


int alea (int n)
{
  return (int)(1.0 * rand() / RAND_MAX * n);
}


ft_thread_t    traces[NB_IMPRIMANTES];
ft_scheduler_t sched_imps[NB_IMPRIMANTES];
enum etat      etats[NB_IMPRIMANTES];

ft_event_t     evt_fin_occupe[NB_IMPRIMANTES];

void traceinstants (void *arg)
{
  int numero_imprimante = ((INFO_TRACE *)arg)->numero_imprimante;
  int nb_tours          = ((INFO_TRACE *)arg)->nb_tours;
  int i;

  for (i = 0; i < nb_tours; i++) {
    printf(">>>>>>>>>> Imprimante %d - Instant %d :\n", numero_imprimante, i);
    fflush(stdout);
    ft_thread_cooperate ();
  }

  printf(">>>>>>>>>> Imprimante %d - Trace last exit\n", numero_imprimante);
  fflush(stdout);
}


void client (void *arg)
{
  int numero_client, numero_imprimante, nb_instants;
  int fini;
  
  numero_client     = ((INFO_CLIENT *)arg)->numero_client;
  numero_imprimante = ((INFO_CLIENT *)arg)->numero_imprimante;

  fini = 0;

  while (!fini) {
      printf("Le client %d fait la queue dans la file de l'imprimante %d.\n",
	     numero_client, numero_imprimante);
      fflush(stdout);

    if (etats[numero_imprimante] == LIBRE) {
      nb_instants = 1 + alea(5);

      etats[numero_imprimante] = OCCUPE;
      printf("Le client %d occupe l'imprimante %d pour %d instants.\n",
	     numero_client, numero_imprimante, nb_instants);
      fflush(stdout);
      ft_thread_cooperate_n(nb_instants);
      printf("Le client %d libere l'imprimante %d.\n",
	     numero_client, numero_imprimante);
      fflush(stdout);
      etats[numero_imprimante] = LIBRE;
      ft_thread_generate(evt_fin_occupe[numero_imprimante]);
      fini = 1;
    } else if (alea(2) % 2) { /* choisit de rester ou non au plus n instants */
      nb_instants = 1 + alea(5);
      printf("L'imprimante %d est occupe, le client %d veut bien attendre au plus %d instants.\n", numero_imprimante, numero_client, nb_instants);
      fflush(stdout);
      ft_thread_await_n(evt_fin_occupe[numero_imprimante], nb_instants);
    } else {                  /* on quitte vers la prochaine imprimante */
      printf("L'imprimante %d est occupee. Le client %d a choisi de quitter la file de l'imprimante %d pour aller vers la file de l'imprimante %d.\n",
	     numero_imprimante, numero_client, numero_imprimante,
	     (numero_imprimante + 1) % NB_IMPRIMANTES);
      numero_imprimante = (numero_imprimante + 1) % NB_IMPRIMANTES;
      fflush(stdout);
      ft_thread_unlink();
      /* usleep(alea(10000)); */
      ft_thread_link(sched_imps[numero_imprimante]);
    }
  }
}


void technicien (void *arg)
{
  long numero_imprimante = (long)arg;
  int  nb_instants;
  
  while (1) {
    if (etats[numero_imprimante] == LIBRE) {
      nb_instants = 1 + alea(5);

      printf("Debut maintenance sur l'imprimante %d pour %d instants.\n",
	     numero_imprimante, nb_instants);
      fflush(stdout);

      etats[numero_imprimante] = OCCUPE;
      ft_thread_cooperate_n(nb_instants);
      etats[numero_imprimante] = LIBRE;

      ft_thread_generate(evt_fin_occupe[numero_imprimante]);

      printf("Fin maintenance sur l'imprimante %d.\n", numero_imprimante);
      fflush(stdout);

      ft_thread_unlink();
      /* usleep(alea(10000)); */

      numero_imprimante = (numero_imprimante + 1) % NB_IMPRIMANTES;
      ft_thread_link(sched_imps[numero_imprimante]);
    } else {
      printf("Le technicien quitte la file de l'imprimante %d occupee pour aller a la file de l'imprimante %d.\n ",
	     numero_imprimante, (numero_imprimante + 1) % NB_IMPRIMANTES);
      numero_imprimante = (numero_imprimante + 1) % NB_IMPRIMANTES;
      fflush(stdout);
      ft_thread_unlink();
      /* usleep(alea(10000)); */
      ft_thread_link(sched_imps[numero_imprimante]);
    }
  }
}


int main (void)
{
  int          i;
  INFO_CLIENT *info_client;
  INFO_TRACE  *info_trace;
  ft_thread_t  ft_technicien;
  ft_thread_t  ft_clients[10];

  for (i = 0; i < NB_IMPRIMANTES; ++i) {
      sched_imps[i] = ft_scheduler_create ();
      etats[i]         = LIBRE;

      info_trace = (INFO_TRACE *)malloc(sizeof(INFO_TRACE));
      info_trace->numero_imprimante = i;
      info_trace->nb_tours          = 1000;
      traces[i] = ft_thread_create(sched_imps[i],
				   traceinstants, NULL, (void *)info_trace);

      evt_fin_occupe[i] = ft_event_create(sched_imps[i]);
  }

  ft_technicien = ft_thread_create(sched_imps[0],
		       technicien, NULL, (void *)0);

  for (i = 0; i < NB_IMPRIMANTES; ++i) {
    ft_scheduler_start(sched_imps[i]);
  }

  for (i = 0; i < 10; ++i) {
    info_client = (INFO_CLIENT *)malloc(sizeof(INFO_CLIENT));
    info_client->numero_client     = i;
    info_client->numero_imprimante = alea(NB_IMPRIMANTES);
    ft_clients[i] = ft_thread_create(sched_imps[alea(NB_IMPRIMANTES)],
				     client, NULL, (void *)info_client);
  }

  for (i = 0; i < 10; ++i) {
    ft_thread_join(ft_clients[i]);
  }

  ft_scheduler_stop(ft_technicien);

  ft_exit();
  return 0;
}
