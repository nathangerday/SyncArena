#include<unistd.h>
#include<sys/types.h>
#include<stdio.h>

int main()
{
	int i=0;
	int pid=fork();
	printf("Ma variable pid est %d\n",pid);
}
