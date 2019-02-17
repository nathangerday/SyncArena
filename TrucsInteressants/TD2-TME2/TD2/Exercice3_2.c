#include<stdlib.h>
#include<stdio.h> 
#include <sys/types.h>
#include<pthread.h>

int n=0;
pthread_mutex_t fmutex;
pthread_cond_t condition=PTHREAD_COND_INITIALIZER;

void * lecteur(void * toto)
{
  int my_n=0;
  FILE * fic=fopen("/dev/urandom","rb");
  for(;;)
    {
      pthread_mutex_lock(&fmutex);
      pthread_cond_wait(&condition,&fmutex);
      if(n!=my_n)
	{
	  int tmp;
	  my_n=n;
	  for(int i=0;i<n;i++)
	    {
	      fscanf(fic,"%d",&tmp);
	      printf("%d\n",tmp);
	    }
	}
      pthread_mutex_unlock(&fmutex);
    }
  return NULL;
}

void *requete(void * toto)
{
  for(;;)
    {
      int tmp;
      printf("donne moi un nombre \n");
      scanf("%d",&tmp);
      pthread_mutex_lock(&fmutex);
      n=tmp;
      pthread_mutex_unlock(&fmutex);
      pthread_cond_signal(&condition); 
    }
  return NULL;
}

int main()
{
  pthread_t th1,th2;
  pthread_mutex_init(&fmutex,NULL);
  pthread_create(&th1,NULL,lecteur,NULL);
  pthread_create(&th2,NULL,requete,NULL);
  pthread_join(th1,NULL);
  //on ne devrait jamais passer par la
}
