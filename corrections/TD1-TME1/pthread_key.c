#include<pthread.h>
#include<stdio.h>
#include<stdlib.h>
/* Key for the thread-specific buffer */
static pthread_key_t buffer_key;

/* Once-only initialisation of the key */
static pthread_once_t buffer_key_once = PTHREAD_ONCE_INIT;

/* Free the thread-specific buffer */
void buffer_destroy(void * buf)
{
  free(buf);
}
/* Allocate the key */
void buffer_key_alloc()
{
  pthread_key_create(&buffer_key, buffer_destroy);
}
/* Allocate the thread-specific buffer */
void buffer_alloc(void)
{
  pthread_once(&buffer_key_once, buffer_key_alloc);
  pthread_setspecific(buffer_key, malloc(100*sizeof(int)));
}

/* Return the thread-specific buffer */
int * get_buffer(void)
{
  return (int *) pthread_getspecific(buffer_key);
}


void population()
{
	int *buf=get_buffer();
	for(int i=0;i<100;i++)
	buf[i]=rand();
}
void affichage(int nth)
{
	int *buf=get_buffer();
	for(int i=0;i<100;i++)
	  printf("thread %d : val %d ; %d\n",nth,i,buf[i]);
	
}
void *fun_thread(void * arg)
{
	int numth=(int) arg;
	buffer_alloc();
	population();
	affichage(numth);	 
}
int main()
{
	pthread_t tab[10];
	for(long i=0;i<10;i++)
	  pthread_create(tab+i,NULL,fun_thread,i);
	for(int i=0;i<10;i++)
         pthread_join(tab[i],NULL);	
}
