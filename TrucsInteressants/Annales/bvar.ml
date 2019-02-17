open Mvar;;

type 'a bvar  = {read : 'a stream mvar; write : 'a stream mvar}
and 'a stream = 'a item mvar
and 'a item = Item of 'a * 'a stream 
;;



let bVar () = 
  let read = mVar() 
  and write = mVar()
  and hole = mVar() in 
    mPut read hole;
    mPut write hole;
    {read=read;write=write};;


let bPut bVar v = 
  let new_hole = mVar () in 
  let old_hole = mTake bVar.write in 
    mPut bVar.write new_hole;
    mPut old_hole (Item (v,  new_hole));;

    
let bGet bVar = 
  let cts = mTake bVar.read in 
  let (Item (v , n)) = mTake cts in 
    mPut bVar.read n;
    v;; 

