#include "fthread.h"
#include "pthread.h"
#include "stdio.h"

void run_p1 (void *rien) {
  while(1){
    fprintf(stderr,"Belle marquise \n");
    ft_thread_cooperate();
  }
}

void run_p2 (void *rien) {
  while(1){
    fprintf(stderr,"vos beaux yeux \n");
    ft_thread_cooperate();
  }
}

void run_p3 (void *rien) {
  while(1){
    fprintf(stderr,"me font mourir \n");
    ft_thread_cooperate();
  }
}

void run_p4 (void *rien) {
  while(1){
    fprintf(stderr,"d'amour \n");
    ft_thread_cooperate();
  }
}


int main(void) {
  
  ft_scheduler_t sched1 = ft_scheduler_create ();
  ft_scheduler_t sched2 = ft_scheduler_create ();
  ft_scheduler_t sched3 = ft_scheduler_create ();
  ft_scheduler_t sched4 = ft_scheduler_create ();
  
  ft_thread_create (sched1,run_p1,NULL,NULL);
  ft_thread_create (sched2,run_p2,NULL,NULL);
  ft_thread_create (sched3,run_p3,NULL,NULL);
  ft_thread_create (sched4,run_p4,NULL,NULL);
    
  ft_scheduler_start (sched1); 
  ft_scheduler_start (sched2); 
  ft_scheduler_start (sched3); 
  ft_scheduler_start (sched4); 
  
  ft_exit();
  return 0;
}
