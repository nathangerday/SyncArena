#include<time.h>
#include<unistd.h>
#include<stdio.h>
#include<stdlib.h>
#include<fthread.h>
#include<pthread.h>

#define NBBARMEN 10
#define NBBOUTEILLES 20 
#define NBRECETTES 10
typedef struct {
  int nb_bouteille;
  int * numero_bouteille;
} recette;

recette recettes[NBRECETTES];

int numero_attente[NBBARMEN];
int numero_servi[NBBARMEN];
int commande[NBBARMEN];

ft_scheduler_t files[NBBARMEN];
ft_event_t commande_evt[NBBARMEN];
ft_event_t servi[NBBARMEN];

ft_scheduler_t filesbouteilles[NBBOUTEILLES];
//Modif pour mathias
int niveaubouteilles[NBBOUTEILLES];
ft_event_t anapu[NBBOUTEILLES];
ft_event_t youpi[NBBOUTEILLES];
ft_scheduler_t Mathias;

//fin modif

void client(void *numbarman)
{
  int num=(long)numbarman;
  int monnumero=numero_attente[num]++;
  //On attend que le barman ecoute notre requete
  while(numero_servi[num]!=monnumero)
    {
      //      printf("\t client %d file %d reveille %d est servi \n",monnumero,num,numero_servi[num]);    
      //on attend sur l'instant d'apres/...
      ft_thread_cooperate();
      ft_thread_await(servi[num]); 
    }
  commande[num]=rand() %NBRECETTES;
  ft_thread_generate(commande_evt[num]);
  //On essaye de commander
  ft_thread_await(servi[num]); 
  //On attend sa reponse
}

void barman(void* arg)
{
  int num=(long)arg;
  for(;;)
    {
      int commandeencours,i;
      printf("Barman %d : en attente commande %d\n",num,numero_servi[num]);
      fflush(stdout);
      ft_thread_await(commande_evt[num]);
      printf("Barman %d : recu commande %d du client %d\n",num,commande[num],
	     numero_servi[num]);
      commandeencours=commande[num];
      //je pars en courant faire la commande
      ft_thread_unlink();
      for(i=0;i<recettes[commandeencours].nb_bouteille;i++)
	{
	  ft_thread_link(filesbouteilles[recettes[commandeencours]
					 .numero_bouteille[i]]);
	  //Modif pour buteilles pas infinies
	  if(niveaubouteilles[recettes[commandeencours]
			      .numero_bouteille[i]]==0)
	    {
	      ft_thread_generate(anapu[recettes[commandeencours]
				       .numero_bouteille[i]]);
	      ft_thread_await(youpi[recettes[commandeencours]
				    .numero_bouteille[i]]);
	    }
	  //Fin modif
	  
	  sleep(1);
	  niveaubouteilles[recettes[commandeencours]
			   .numero_bouteille[i]]--;
	  ft_thread_unlink();
	}
      ft_thread_link(files[num]);
      //on attend le client suivant suivant
      numero_servi[num]++;
      //on previent le client en cours que sa commande est arrivé
      ft_thread_generate(servi[num]);
      printf("Barman %d : en servi commande %d \n",num,commande[num]);
    }
}


void W(void *arg)
{
  int num=(long)arg;
  int total=0;
  int nbcommande=0;
  for(;;)
    {
      int depart,arrive;
      ft_thread_await(commande_evt[num]);
      depart=clock()/CLOCKS_PER_SEC;
      ft_thread_await(servi[num]);
      arrive=clock()/CLOCKS_PER_SEC;
      total=(arrive-depart);
      nbcommande++;
    }
}


void threadbourgoin(void *arg)
{
  int num=(long)arg;
  for(;;)
    {
      ft_thread_await(anapu[num]);
      printf("Mathias : recu evenement de %d\n",num);
      ft_thread_unlink();
      ft_thread_link(Mathias);
      sleep(30);
      ft_thread_unlink();
      ft_thread_link(filesbouteilles[num]);
      niveaubouteilles[num]=30;
      ft_thread_generate(youpi[num]);
      printf("Mathias : rempli bouteille de %d\n",num);
    }
}

void * Emmanuel(void *dummy)
{
  int i;
  for(i=0;i<100000;i++)
    {
      long numfile=rand()%NBBARMEN;
      usleep(100000);
      ft_thread_create(files[numfile],client,NULL,(void *)numfile);
      printf("nouveau client pour %d \n",numfile);
    }
}

int main()
{
  long i;
  pthread_t chaillou;
  for(i=0;i<NBRECETTES;i++)
    {
      recette tmp;
      int j;
      tmp.nb_bouteille=rand() %NBBOUTEILLES;
      tmp.numero_bouteille=malloc(tmp.nb_bouteille*sizeof(int));
      for(j=0;j<tmp.nb_bouteille;j++)
	tmp.numero_bouteille[j]=rand()%NBBOUTEILLES;
      recettes[i]=tmp;
    }
  for(i=0;i<NBBARMEN;i++)
    {
      files[i]=ft_scheduler_create();
      ft_thread_create(files[i],barman,NULL,(void *)i);
      numero_servi[i]=0;
      numero_attente[i]=0;
      commande_evt[i]=ft_event_create(files[i]);
      servi[i]=ft_event_create(files[i]);
      ft_scheduler_start(files[i]);
    }
    for(i=0;i<NBBOUTEILLES;i++)
    {
      filesbouteilles[i]=ft_scheduler_create();
      ft_thread_create(filesbouteilles[i],threadbourgoin,NULL,(void*)i);
      niveaubouteilles[i]=30;
      anapu[i]=ft_event_create(filesbouteilles[i]);
      youpi[i]=ft_event_create(filesbouteilles[i]);
      ft_scheduler_start(filesbouteilles[i]);
    }
    Mathias=ft_scheduler_create();
    ft_scheduler_start(Mathias);
    pthread_create(&chaillou,NULL,Emmanuel,NULL);
    ft_exit();
}
