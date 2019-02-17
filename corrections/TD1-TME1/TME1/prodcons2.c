// Fichier prodcons2.c // Producteurs/Consommateurs
// Deuxieme version synchronisee 
// (mais attentes actives)
// Compilation: 
// gcc -Wall --std=c99 -pedantic prodcons2.c -o prodcons2 -l pthread

#include <stdlib.h> 
#include <stdio.h> 
#include <sched.h>
#include <pthread.h>

// Constantes #define BUF_SIZE 10 #define NB_TURN 55

#define TRUE 1 
#define FALSE 0
#define BUF_SIZE 10
#define NB_TURN 55
// Structure de donnee BUFFER

typedef struct _Buffer { 
  int nb_elem;   
  int elem[BUF_SIZE];   
  pthread_mutex_t MUTEX_buf; } Buffer;

Buffer * CreerBuffer() 
{   
  Buffer* buf = (Buffer *) malloc(sizeof(Buffer));
  if(buf==NULL) 
    {     
      fprintf(stderr,"Impossible d'allouer le buffer\n"); 
      exit(EXIT_FAILURE);   
    }   
  buf->nb_elem=0;
  pthread_mutex_init(&(buf->MUTEX_buf), NULL);
  return buf;
}

void EcrireBuffer(Buffer *buf, int val) 
{   
  int cont = FALSE;

  while(!cont) 
    {	       
      pthread_mutex_lock(&(buf->MUTEX_buf)); 
      if(buf->nb_elem == BUF_SIZE) 
	{ 
	  // BUFFER plein
	  fprintf(stderr,"Buffer plein\n");
	  pthread_mutex_unlock(&(buf->MUTEX_buf));
	}
      else 
	{
	  // OK, on peut ecrire
	  cont=TRUE;     
	}   
    }

  buf->elem[buf->nb_elem] = val;
  sched_yield();   
  buf->nb_elem++;

  pthread_mutex_unlock(&(buf->MUTEX_buf)); 
}

int LireBuffer(Buffer *buf) 
{   
  int cont = FALSE;
  while(!cont) 
    {     
      pthread_mutex_lock(&(buf->MUTEX_buf));     
      if(buf->nb_elem == 0) 
	{ 
	  // BUFFER vide       
	  fprintf(stderr,"Buffer vide\n");       
	  pthread_mutex_unlock(&(buf->MUTEX_buf));     
	} 
      else 
	{ 
	  // OK, on peut lire       
	  cont=TRUE;     
	}   
    }

  int val = buf->elem[0];   
  int i;   sched_yield();   
  for(i=1;i<buf->nb_elem;i++)      
    buf->elem[i-1]=buf->elem[i];   
  sched_yield();

  buf->nb_elem--;   
  pthread_mutex_unlock(&(buf->MUTEX_buf));   
  return val; 
}   

// Routine du producteur 
void * THREAD_Producteur(void * arg) 
{   
  Buffer *buf = (Buffer *) arg;
  int i;   
  for(i=0;i<NB_TURN;i++) 
    {     
      EcrireBuffer(buf,i);     
      //usleep(hasard(100,1000));   
    } 
  return NULL;
}

// Routine du consommateur 
void * THREAD_Consommateur(void * arg) 
{   
  Buffer *buf = (Buffer *) arg;

  int val;   
  int compteur =0;   
  while(compteur< NB_TURN) 
    {     
      val = LireBuffer(buf);     
      printf("Value recue = %d\n",val);     
      //usleep(hasard(1000,30000));     
      compteur++;   
    } 
  return NULL;
}

// Programme principal  

int main(void) 
{   
  pthread_t thread_prod;   
  pthread_t thread_cons;   
  long stat_prod;   
  long stat_cons;

  Buffer * buf = CreerBuffer();   
  pthread_create(&thread_prod,NULL,THREAD_Producteur,(void *) buf);
  pthread_create(&thread_cons,NULL,THREAD_Consommateur,(void *) buf);
  pthread_join(thread_prod, (void**) &stat_prod);   
  pthread_join(thread_cons, (void**) &stat_cons);

  pthread_mutex_destroy(&(buf->MUTEX_buf));
}
