// Fichier compteur3.c 
// Compteur partage en Posix Threads 
// version synchronisee

// pour compiler (sous linux) : 
// gcc -Wall --std=c99 -pedantic compteur3.c -o compteur3 -lpthread

#include <stdlib.h> 
#include <unistd.h> 
#include <sched.h>
#include<stdio.h>
#include <pthread.h> 
#define NB_THREAD 10000
int SHARED_compteur; 
pthread_mutex_t MUTEX_compteur;

void* THREAD_IncrementeCompteur(void *arg)
{   
  // DEBUT SECTION CRITIQUE   
  pthread_mutex_lock(&MUTEX_compteur);   
  int temp = SHARED_compteur;   
  sched_yield();   
  SHARED_compteur=temp+1;   
  sched_yield();   
  printf("Compteur = %d\n", SHARED_compteur);   
  pthread_mutex_unlock(&MUTEX_compteur);   
  // FIN SECTION CRITIQUE 
  return NULL;
}

int main(void) 
{   
  int i;   
  pthread_t incr_threads[NB_THREAD];   
  void *status;

  pthread_mutex_init(&MUTEX_compteur,NULL);   
  for(i=0;i<NB_THREAD;i++) 
    pthread_create(&incr_threads[i], NULL, THREAD_IncrementeCompteur, NULL);
  for(i=0;i<NB_THREAD;i++)
    pthread_join(incr_threads[i],&status);

  pthread_mutex_destroy(&MUTEX_compteur);   
  if(SHARED_compteur== NB_THREAD) 
    printf("TERMINE\n");

}
