let creer_serveur port max_con =
  let sock = Unix.socket Unix.PF_INET Unix.SOCK_STREAM 0
  and addr = Unix.inet_addr_of_string "127.0.0.1" in
    Unix.setsockopt sock Unix.SO_REUSEADDR true;
    Unix.bind sock (Unix.ADDR_INET(addr, port));  
    Unix.listen sock max_con;
    sock;;

let serveur_process sock service =  
  while true do     
    let (s, caller) = Unix.accept sock     in 
      match Unix.fork() with          
        | 0 -> (* code du fils *)
            if Unix.fork() <> 0 then exit 0 ;
            let inchan = Unix.in_channel_of_descr s               
	    and outchan = Unix.out_channel_of_descr s                
	    in 
	      service inchan outchan ;
              close_in inchan ;                  
	      close_out outchan ;
              exit 0          
        | id -> (* code du pere *)
            Unix.close s; 
            ignore(Unix.waitpid [] id)   
  done;;

let echo_service inchan outchan =
  while true do     
    let line = input_line inchan in
      output_string outchan (line^"\n"); 
      flush outchan
  done;;

let main () =
  let port = int_of_string Sys.argv.(1)in    
  let sock = creer_serveur port 4 in
    serveur_process sock echo_service;;

let _ = main();;
