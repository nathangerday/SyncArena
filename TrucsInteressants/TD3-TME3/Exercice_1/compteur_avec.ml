(* Compteur au musee *) 
(* version 2 avec synchronisation et attente passive *)

(* pour compiler :    ocamlc -thread unix.cma threads.cma musee2.ml -o musee2 *) 
let compteur = ref 0 
let compteur_lock = Mutex.create () 
let fin_entrees = ref false

let rec entree nb =
  if nb > 0 then       
    begin         
      Mutex.lock compteur_lock;          
      compteur:=!compteur+1 ;          
      print_int nb ; 
      print_endline " => Entree" ;
      Mutex.unlock compteur_lock;
      Thread.delay (Random.float 0.2);
      entree (nb-1)       
    end      
  else       
    begin         
      print_endline "Fin entrees" ;
      fin_entrees := true
    end

let sortie dummy =   
  while not !fin_entrees or !compteur>0    do     
    Mutex.lock compteur_lock;     
    if !compteur >0 then      
      begin        
	compteur:=!compteur - 1;       
	print_int !compteur ; 
	print_endline " => Sortie" ;     
      end;     
    Mutex.unlock compteur_lock;     
    Thread.delay (Random.float 0.4)   
  done;   
  print_endline "Fin sorties"
    
let main () =   
  let nb_visiteurs = int_of_string Sys.argv.(1)   in     
  let t1 = Thread.create sortie ()      
  and t2 = Thread.create entree nb_visiteurs     in  
  (* Attendre la fin des entrees *)       
  Thread.join t2 ;       
  (* Attendre la fin des sorties *)       
  Thread.join t1 ;       
  exit 0;;

main ();;
