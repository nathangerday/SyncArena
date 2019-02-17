let creer_serveur port max_con = 
  let sock = Unix.socket Unix.PF_INET Unix.SOCK_DGRAM 0  
  and addr = Unix.inet_addr_of_string "127.0.0.1" in
    Unix.setsockopt sock Unix.SO_REUSEADDR true;
    Unix.bind sock (Unix.ADDR_INET(addr, port));
    sock ;;

let serveur_process sock=  
  let message = String.create 10000 in
    while true do
      match Unix.recvfrom sock message 0 10000 [] with
        | (longueur, Unix.ADDR_INET (addr, port)) ->
            Printf.printf "Client %s said ``%s''\n%!" 
              (Unix.gethostbyaddr addr).Unix.h_name 
              (String.sub message 0 longueur);
            ignore (Unix.sendto sock message 0 longueur [] (Unix.ADDR_INET(addr,port)));
        | _ -> failwith "Cas non géré dans echoserver3.ml"
    done ;; 

let main () = 
  let port = int_of_string Sys.argv.(1)    in    
  let sock = creer_serveur port 4 in
    serveur_process sock;;

main();;
