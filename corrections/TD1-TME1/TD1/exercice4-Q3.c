#include<unistd.h>
#include<sys/types.h>
#include<stdio.h>
#include <pthread.h>
#include<stdlib.h>

int i=0;
pthread_t th1;

void *fun_fils(void *toto)
{
  sleep(10);
  printf("c'est moi le fils et le contenu de pid vaut %d\n",getpid());
  printf("c'est moi le fils et le contenu de i vaut %d\n",i);
  return NULL;
}

int main()
{
  if (!pthread_create(&th1,NULL,fun_fils,NULL)) {
    printf("je suis le pere et ma variable pid vaut %d\n",getpid());
    printf("donne moi vite (moins de 10 secondes) un nombre \n");
    scanf("%d",&i);
    printf("je suis le pere et ma variable i vaut %d\n",i);
    pthread_join(th1,NULL);
  } else {
    printf("Creation de thread impossible.\n");
  }
	
}
