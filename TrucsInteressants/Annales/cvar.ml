open Mvar;;

type 'a state = {ack  : unit mvar; data : 'a mvar};;
type 'a cvar  = Buf of 'a state;;

let cVar  () = 
  let ack_var = mVar () 
  and data_var = mVar () in 
     mPut ack_var ();
     Buf {ack=ack_var; data = data_var}
;;

let putCvar (Buf r) v = 
   mTake r.ack;
   mPut r.data v

let getCvar (Buf r) = 
  let v = mTake r.data in 
     mPut r.ack (); v
