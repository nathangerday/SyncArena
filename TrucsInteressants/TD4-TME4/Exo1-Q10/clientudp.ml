let socket =
  Unix.socket Unix.PF_INET Unix.SOCK_DGRAM
    (Unix.getprotobyname "udp").Unix.p_proto;;

(* Send a UDP message. *)
let ipaddr = (Unix.gethostbyname Sys.argv.(1)).Unix.h_addr_list.(0);;
let portaddr = Unix.ADDR_INET (ipaddr, (int_of_string Sys.argv.(2)));;
let len = Unix.sendto socket Sys.argv.(3) 0 (String.length Sys.argv.(3)) [] portaddr;;

(* Receive a UDP message. *)
let msg = String.create 10000;;
let len, portaddr = Unix.recvfrom socket msg 0 10000 [];;

print_endline (String.sub msg 0 len);;
