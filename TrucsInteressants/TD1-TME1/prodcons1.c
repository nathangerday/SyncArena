// Fichier prodcons1.c 
// Producteurs/Consommateurs
// Premiere version : sans synchronisation 
// compilation 
// gcc -Wall --std=c99 prodcons1.c -o prodcons1 -lpthread

#include <stdlib.h> 
#include <stdio.h> 
#include <sched.h>
#include <pthread.h> 
// Constantes 
#define BUF_SIZE 10 
#define NB_TURN 55
#define TRUE 1 
#define FALSE 0

// Structure de donnee BUFFER 
typedef struct _Buffer {   int nb_elem;   int elem[BUF_SIZE]; } Buffer;

Buffer * CreerBuffer() 
{   
  Buffer* buf = (Buffer *) malloc(sizeof(Buffer));   
  if(buf==NULL) 
    {     
      fprintf(stderr,"Impossible d'allouer le buffer\n");     
      exit(EXIT_FAILURE);   
    }   buf->nb_elem=0;   
  return buf; 
}

void EcrireBuffer(Buffer *buf, int val) 
{   
  int cont = FALSE;

  while(!cont) 
    {     
      if(buf->nb_elem == BUF_SIZE) 
	// BUFFER plein       
	  fprintf(stderr,"P");     
      else 
	// OK, on peut ecrire       
	cont=TRUE;   
    }
  buf->elem[buf->nb_elem] = val;
  sched_yield();   buf->nb_elem++; 
}

int LireBuffer(Buffer *buf) 
{   
  int cont = FALSE;
  while(!cont) 
    {     
      if(buf->nb_elem == 0) 
	{ 
	  // BUFFER vide       
	  fprintf(stderr,"V");     
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
    } 
  return NULL;
}

// Routine du consommateur 
void * THREAD_Consommateur(void * arg) 
{   
  Buffer *buf = (Buffer *) arg;

  int compteur =0;   
  int val;   
  while(compteur<NB_TURN) 
    {     
      val = LireBuffer(buf);
      printf("Valeur recue = %d\n",val);
      compteur++;   
    } 
  return NULL;
}

// Programme principal  
int main(void) 
{   
  pthread_t thread_prod;
  pthread_t thread_cons;
  int stat_prod;
  int stat_cons;

  Buffer * buf = CreerBuffer();   
  pthread_create(&thread_prod,NULL,THREAD_Producteur,(void *) buf);


  pthread_create(&thread_cons,NULL,THREAD_Consommateur,(void *) buf);
  pthread_join(thread_prod, (void**) &stat_prod);
  pthread_join(thread_cons, (void**) &stat_cons);

}
