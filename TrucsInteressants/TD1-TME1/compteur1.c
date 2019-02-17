// Fichier compteur1.c 
// Compteur partage en Posix Threads
// version NON-SYNCHRONISEE

// pour compiler (sous linux) : 
// gcc -Wall --std=c99 -pedantic -pedantic-errors compteur1.c -o compteur1 -lpthread

#include<stdlib.h> 
#include<stdio.h>
#include<unistd.h> 
#include<sched.h>
#include<pthread.h> 

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

  for(i=0;i<NB_THREAD;i++) 
    pthread_create(&incr_threads[i], NULL, THREAD_IncrementeCompteur, NULL);
  
  while(SHARED_compteur!= NB_THREAD) 
    {     
      // ne rien faire   
    }   
  printf("TERMINE\n");      
}
