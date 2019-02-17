let main() =
  let host = Unix.gethostbyname (Sys.argv.(1)) in
  let host_addr	 =  host.Unix.h_addr_list.(1) in 	
  let sock_descr = Unix.socket Unix.PF_INET Unix.SOCK_STREAM 0 in 
  let outchan =  Unix.out_channel_of_descr sock_descr in
    Unix.connect sock_descr (Unix.ADDR_INET(host_addr, 12345));
    while true do
      let ligne = read_line () in
        try
	  output_string outchan (ligne ^ "\n");
	  flush outchan;
        with
          | End_of_file -> Printf.printf "Fin de connexion \n";flush stdout;
          | exn->
              print_endline (Printexc.to_string exn)
    done;;

let _ = main();;
