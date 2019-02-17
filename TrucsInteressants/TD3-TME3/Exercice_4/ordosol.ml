(* Ordonnanceur concurrent *) 
(* version 1 : synchro par conditions *)

(* Probleme : le thread ordonnanceur peut recuperer la main avant               que la tache ne soit executee *)

(* pour compiler :    ocamlc -thread unix.cma threads.cma ordosol.ml -o ordosol *) 

let ordon_cond = Condition.create ()
let servi_cond = Condition.create()
let ordon_mutex = Mutex.create ()
let servi_clef = Mutex.create()

let ordon_valeur = ref 0 
let ordonnanceur nb =   
  while true do     
    for i = 1 to nb do       
      Thread.delay 2.0;  
(* Pour laisser le temps aux taches de se mettre en attente *)
      Mutex.lock ordon_mutex;
      ordon_valeur:=i;
      Condition.broadcast ordon_cond;
      Mutex.unlock ordon_mutex;
      for i = 1 to nb do
	Mutex.lock servi_clef;
	Condition.wait servi_cond servi_clef;
	Mutex.unlock servi_clef
      done
    done   
  done;; 

let task nb =   
  while true do
    Mutex.lock ordon_mutex;
    Condition.wait ordon_cond ordon_mutex;
    (if(!ordon_valeur=nb)      then
      begin        
	print_string "Tache #" ; 
	print_int nb ; 
	print_endline " activee";
        (* tache a realiser *)
        Mutex.unlock ordon_mutex
      end     
    else
      Mutex.unlock ordon_mutex);
    Mutex.lock servi_clef;
    Condition.signal servi_cond;
    Mutex.unlock servi_clef
  done;; 

let main () =   
  let nbtask = int_of_string Sys.argv.(1)    in
  for i=1 to nbtask do       
    let th = Thread.create task i in ()
  done ;     
  let th = (Thread.create ordonnanceur nbtask)     in
  Thread.join th;;

main ();;
