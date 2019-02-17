#include<unistd.h>
#include<sys/types.h>
#include<stdio.h>
#include<stdlib.h>

#include <sched.h>


int i=0;
int TID;
long pile[1000];

int fun_fils (void *toto)
{
  sleep(10);
  printf("c'est moi le fils et le contenu de pid vaut %d\n",getpid());
  printf("c'est moi le fils et le contenu de i vaut %d\n",i);
  return 0;
}

int main()
{
  TID = clone(fun_fils, (void *)&pile[999],
	      CLONE_VM|CLONE_FS|CLONE_FILES|CLONE_SIGHAND|CLONE_THREAD,
	      NULL);
  printf("je suis le pere et ma variable pid vaut %d\n",getpid());
  printf("et le TID du fils vaut %d.\n", TID);

  printf("donne moi vite (moins de 10 secondes) un nombre \n");
  scanf("%d",&i);
  printf("je suis le pere et ma variable i vaut %d\n",i);
  sleep(10);
}
