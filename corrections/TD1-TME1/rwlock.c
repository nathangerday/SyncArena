#include<pthread.h>
#include<stdio.h>

int n=-1;
pthread_rwlock_t lock=PTHREAD_RWLOCK_INITIALIZER;
int val1,val2;

void * routinethread_read(void *arg)
{
  int num=(long)arg;
  int myn;
  printf("je suis le thread %d et je part\n",num);
  pthread_rwlock_rdlock(&lock);
  printf("je suis le thread %d et j'ai pris le lock en lecture\n",num);
  sleep(1);
  for(myn=0;myn<n;myn++)
    val1=rand(),usleep(1000000);
  printf("je suis le thread %d et je relache le lock en lecture\n",num);
  
}

int main()
{
  pthread_t th1,th2;
  pthread_rwlock_wrlock(&lock);
  pthread_create(&th1, NULL, routinethread_read,(void*) 1);
  pthread_create(&th2, NULL, routinethread_read, (void*)2);
  usleep(1000);
  pthread_rwlock_unlock(&lock);
  pthread_join(th1,NULL);
  pthread_join(th2,NULL);
  printf("le gagnant est %d\n",val1>val2?1:2);
}
