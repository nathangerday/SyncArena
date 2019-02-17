#include<unistd.h>
#include<sys/types.h>
#include<stdio.h>

int main()
{
	int i=0;
	int pid=fork();
	if(pid){
		printf("Je suis le pere et ma varible pid est %d\n",pid);
		printf("donne moi un nombre maintenant\n");
		scanf("%d",&i);
		printf("ma variable i vaut %d\n",i);
		}
	else
	{
		sleep(4);
		printf("\nje suis le fils et ma variable pid vaut %d\n",pid);
		printf("je suis le fils et ma variable i vaut %d\n",i);
	}

	
}
