// Fichier compteur2.c
// Compteur partage en Posix Threads
// version non-synchronisee avec jonction

// pour compiler (sous linux) : 
// gcc -Wall --std=c99 -pedantic -pedantic-errors compteur2.c -o compteur2 -lpthread

#include<stdlib.h> 
#include<unistd.h> 
#include<sched.h>
#include<pthread.h> 
#include<stdio.h>

#define NB_THREAD 10 

int SHARED_compteur; 

void* THREAD_IncrementeCompteur(void *arg) 
{   
  int temp = SHARED_compteur;   
  sched_yield();   
  SHARED_compteur=temp+1;   
  sched_yield();   
  printf("Compteur = %d\n", SHARED_compteur); 
  return NULL;
}

int main(void) 
{   
  int i;   
  pthread_t incr_threads[NB_THREAD];   
  void *status;

  for(i=0;i<NB_THREAD;i++) 
    pthread_create(&incr_threads[i], NULL, THREAD_IncrementeCompteur, NULL);
  for(i=0;i<NB_THREAD;i++) 
    pthread_join(incr_threads[i],&status);

  if(SHARED_compteur== NB_THREAD) 
    printf("TERMINE\n");

}
