(* compilation: ocamlc unix.cma pongclient.ml -o pongclient *)

let inet_addr_of_hostname serveur = 
  try Unix.inet_addr_of_string serveur
  with Failure("inet_addr_of_string") ->
    try (Unix.gethostbyname serveur).Unix.h_addr_list.(0) 
    with Not_found -> print_string serveur ;
      print_endline "Not found";
      exit 1;;

let connecter_client client_fun adresse port = 
  let sock = Unix.socket Unix.PF_INET Unix.SOCK_STREAM 0 
  and addr = inet_addr_of_hostname adresse in 
    Unix.connect sock (Unix.ADDR_INET(addr,port)) ; 
  let inchan = Unix.in_channel_of_descr sock 
      and outchan = Unix.out_channel_of_descr sock in 
      client_fun sock inchan outchan ;
      close_in inchan ; 
      close_out outchan ; 
      exit 0;; 

let pongclient sock inchan outchan = 
  Printf.fprintf outchan "Ping\n%!"; (* %! sert à flusher *)
  Printf.printf "Réponse = %s\n%!" (input_line inchan);
  Printf.fprintf outchan "Ping\n%!";

  Printf.printf "Réponse = %s\n%!" (input_line inchan); 
  Printf.fprintf outchan "Ping\n%!"; 
  
  Printf.printf "Réponse = %s\n%!" (input_line inchan);
  Printf.fprintf outchan "Toto\n%!"; 

  Printf.fprintf outchan "Exit\n%!";
  Unix.shutdown sock Unix.SHUTDOWN_ALL;;

let main () = 
  let adresse = Sys.argv.(1) 
  and port = int_of_string Sys.argv.(2) in 
    connecter_client pongclient adresse port;; 

let _ = main ();;
