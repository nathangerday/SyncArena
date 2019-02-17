#include <stdio.h>  
#include <stdlib.h>
#include <pthread.h>  

void* THREAD_Routine(void *arg) 
{    /* routine de thread*/ 
  return NULL;  
}

int main(void) 
{    
  pthread_t thread_id;    
  int ok;   

  ok = pthread_create(&thread_id, NULL, THREAD_Routine, /* arg */ NULL);    
  if(ok!=0) 
    {      
      fprintf(stderr,"Impossible de creer de le thread\n");      
      exit(EXIT_FAILURE);      
    }
}
