(* Compteur au musee *) 
(* version 1 sans synchronisation et attente active *)

(* pour compiler :    ocamlc -vmthread unix.cma threads.cma musee1.ml -o musee1 *) 
let compteur = ref 0 
let rec entree nb =     if nb > 0 then       
	begin          
	compteur:=!compteur+1 ;
	print_int nb ; 
	print_endline " => Entree" ;
	Thread.delay (Random.float 0.2);
	entree (nb-1)       
	end      
	else      
	print_endline "Fin entrees";;


let sortie dummy =   while true    do     
	if !compteur >0 then      
		begin        
		compteur:=!compteur - 1;       
		print_int !compteur ; 
		print_endline " => Sortie" ;     
		end;     
	Thread.yield ();   
	Thread.delay (Random.float 0.4);   
	done;;

let main () =   
	let nb_visiteurs = int_of_string Sys.argv.(1)   in     
	let t1 = Thread.create sortie ()  
	and t2 = Thread.create entree nb_visiteurs     in        
	(* Attendre la fin des entrees *)       
	Thread.join t2 ;     
	(* Attendre ACTIVEMENT la fin des sorties *)       
	while !compteur > 0 do () done;       
	(* Puis "tuer" le thread des sorties *)       
	(* Attention: kill ne marche qu'avec -vmthread *)       
	Thread.kill t1 ;       
	print_string "Fin sorties\n";       
	exit 0;;

main ();;

