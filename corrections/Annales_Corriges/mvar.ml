type 'a mvar = MV of ('a Event.channel * 'a Event.channel * bool Event.channel);;

let mVar () = 
  let takeCh = Event.new_channel ()  and putCh = Event.new_channel () 
  and ackCh = Event.new_channel () in 
  let rec empty () = 
    let x = Event.sync (Event.receive putCh) in 
      Event.sync (Event.send ackCh true);
      full x
  and full x = 
    Event.select 
      [Event.wrap (Event.send takeCh x) empty ; 
       Event.wrap (Event.receive putCh) 
                  (fun _ -> (Event.sync (Event.send  ackCh false); full x))]
  in 
    ignore (Thread.create empty ());  MV (takeCh, putCh, ackCh) ;;

let mTakeEvt  ( mv : 'a mvar) = match mv with 
   MV (takechannel, _, _ ) -> Event.receive takechannel ;;

let mTake mv = Event.sync (mTakeEvt mv);;

exception Put;;

let mPut mv x = match mv with 
  MV (takechannel, putchannel, ackchannel) -> 
     Event.sync (Event.send putchannel x);
     if (Event.sync( Event.receive ackchannel)) then ()
     else raise Put ;;
