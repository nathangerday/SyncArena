let main() = 
    let host =  Unix.gethostbyname (Unix.gethostname()) in 
    let host_addr = Unix.inet_addr_of_string "127.0.0.1" in 	
    let sock_descr = Unix.socket Unix.PF_INET Unix.SOCK_STREAM 0 in 
     Unix.bind sock_descr (Unix.ADDR_INET(host_addr, 12345));
     Unix.listen sock_descr 10;
    let (service_sock,client_sock_addr) = Unix.accept sock_descr in 
     let outchan =  Unix.out_channel_of_descr service_sock in
     let inchan =  Unix.in_channel_of_descr service_sock in	
    while true do	
    let ligne = input_line inchan in
    try
	output_string stdout (ligne ^ "\n");
	flush stdout;
    with
      End_of_file -> Printf.printf "Fin de connexion \n";flush stdout;
    | exn->
        print_string (Printexc.to_string exn)
   done;;
main();;
