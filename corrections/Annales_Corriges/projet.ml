(*********************************************************)
(* Projet OCaml LI 332 - dim. oct. 13 12:05:04 CEST 2013 *)
(* Alexis BECIRSPAHIC - 3005993                          *)
(* Alexandre ENOUF - 3000469                             *)
(*********************************************************)


(**
   Un objet musical est une note, un silence, une séquence de d'objets musicaux
   ou des objets musicaux joués en parallèles.
*)
type objet_musical =
| Note of (int * int * int) (* hauteur * durée * volume *)
| Silence of (int)	    (* durée *)
| Sequence of (objet_musical list)
| Parallele of (objet_musical list)
;;


(**
   Exemple figure 2 du TME.
*)
let exemple = Parallele [
  Sequence [Note (60, 1000, 127) ; Note (64, 500, 127) ; Note (62, 500, 127) ; Silence 1000 ; Note (67, 1000, 127)] ;
  Sequence [Note (52, 2000, 127) ; Note (55, 1000, 127) ; Note (55, 1000, 127)] ;
] ;;


(**
   Permet de connaître la durée total d'un objet musical.
   @param obj objet musical dont on veut connaître la durée.
   @return durée totale de l'objet musical.
*)
let duree obj =
  let rec loop = function
    | Note (_, d, _) -> d
    | Silence d -> d
    | Sequence s -> List.fold_left (+) 0 (List.map loop s)
    | Parallele s -> List.fold_left (max) 0 (List.map loop s)
  in loop obj
;;


(**
   Permet de copier un objet musical.
   @param obj objet musical que l'on veut copier.
   @return une copie de l'objet musical.
*)
let copy obj =
  let rec loop = function
    | Note (a, b, c) -> Note (a, b, c)
    | Silence d -> Silence d
    | Sequence s -> Sequence (List.map loop s)
    | Parallele s -> Parallele (List.map loop s)
  in loop obj
;;


(**
   Permet de savoir combien de note contient un objet musical.
   @param obj objet musical dont on veut connaître le nombre de notes.
   @return le nombre de notes de l'objet musical.
*)
let note_count obj =
  let rec loop = function
    | Note (_, _, _) -> 1
    | Silence _ -> 0
    | Sequence s -> List.fold_left (+) 0 (List.map loop s)
    | Parallele s -> List.fold_left (+) 0 (List.map loop s)
  in loop obj
;;


(**
   Permet de multiplier toutes les durées (notes, silences) par un flottant.
   @param obj objet musical que l'on souhaite allongé.
   @param t flottant multiplicateur de chaque durée.
   @return un nouvel objet musical allongé.
*)
let stretch obj t =
  let rec loop = function
    | Note (a, b, c) -> Note (a, int_of_float (float_of_int b *. t), c)
    | Silence d -> Silence (int_of_float (float_of_int d *. t))
    | Sequence s -> Sequence (List.map loop s)
    | Parallele s -> Parallele (List.map loop s)
  in loop obj
;;


(**
   Permet de transposer l'objet musical d'un certain nombre de tons.
   @param obj objet musical que l'on souhaite transposer.
   @param t nombre de tons à ajouter (peut éventuellement être < 0).
   @return un nouvel objet musical transposé.
*)
let transpose obj t =
   let rec loop = function
    | Note (a, b, c) -> Note (a + t, b, c)
    | Silence d -> Silence d
    | Sequence s -> Sequence (List.map loop s)
    | Parallele s -> Parallele (List.map loop s)
  in loop obj
;;


(**
   Permet d'inverser l'ojbet musical (ordre des notes, voix).
   @param obj objet musical à inverser.
   @return un nouvel objet musical inversé.
*)
let retrograde obj =
  let rec loop = function
    | Note (a, b, c) -> Note (a, b, c)
    | Silence d -> Silence d
    | Sequence s -> Sequence (List.rev (List.map loop s))
    | Parallele s -> Parallele (List.rev (List.map loop s))
  in loop obj
;;


(**
   Permet de réaliser une symétrie axiale sur un objet musical.
   @param obj objet musical auquel on souhaite appliquer un miroir.
   @param valeur de symétrie (nb : si v = 60 alors f (58, v) = 62
   @return un nouvel objet musical modifié par miroir.
*)
let mirror obj v =
  let rec loop = function
    | Note (a, b, c) -> Note ((abs (a - (2 * v)) mod 128), b, c)
    | Silence d -> Silence d
    | Sequence s -> Sequence (List.map loop s)
    | Parallele s -> Parallele (List.map loop s)
  in loop obj
;;


(**
   Permet de concaténer deux objets musicaux en formant une séquence.
   @param obj1 premier objet musical.
   @param obj2 second objet musical.
   @return une séquence des deux objets.
*)
let concat_mo obj1 obj2 = match obj1, obj2 with
  | Sequence s1, Sequence s2 -> Sequence (s1 @ s2)
  | Sequence s1, obj -> Sequence (s1 @ [obj])
  | obj, Sequence s2 -> Sequence (obj :: s2)
  | obj1, obj2 -> Sequence [obj1 ; obj2]
;;
 

(**
   Permet de répéter n fois un objet musical pour en former un autre.
   @param obj objet musical à répéter.
   @param n nombre de fois que l'on souhaite répéter l'objet musical.
   @return un nouvel objet musical consitué de n fois le premier.
*)
let repeat obj n =
  let rec loop = function
    | 0 -> copy obj
    | n -> concat_mo (copy obj) (loop (n - 1))
  in loop n
;;


(**
   Permet de créer un canon à partir d'un objet musical et d'un décallage.
   @param obj objet musical dont on souhaite faire un canon.
   @param n décallage (en ms) de la seconde voix du canon.
   @return un objet musical correspondant au canon.
*)
let canon obj n =
  Parallele [
    copy obj ;
    Sequence [Silence n ; copy obj]
  ]
;;


(**
   Permet un affichage (rudimentaire) dans le style piano roll.
   @param obj objet musical à afficher sous forme de piano roll.
*)
let show_and_play obj echelle =
  let rec loop_seq offset = function
    | [] -> offset
    | t :: q -> let dis = loop offset t in loop_seq (offset + dis) q
  and loop_par offset acc = function
    | [] -> acc
    | t :: q -> let dis = loop offset t in loop_par offset (max acc (offset + dis)) q
  and loop offset = function
    | Note (h, d, _) -> Graphics.fill_rect offset h (d / echelle) 1 ; (d / echelle)
    | Silence d -> (d / echelle)
    | Sequence s -> loop_seq offset s
    | Parallele p -> loop_par offset offset p
  in
  let x = string_of_int (duree obj / echelle) in
  Graphics.open_graph (" " ^ x ^ "x128") ;
  let osef = loop 0 obj in () 
;;


(**
   Permet l'exportation d'un objet musical au format MIDI.
   @param obj objet musical à exporter.
*)
let save_as_midi obj =
  let rec loop = function
    | Note (h, d, v) -> [[(0, 0, NoteON (h, v)) ; (d, 0, NoteOFF (h, v))] ]
    | Silence d -> [[(0, 0, NoteON (0, 0)) ; (d, 0, NoteOFF (0, 0))]]
    | Sequence s -> [List.fold_left (@) [] (List.map note_or_quiet s)]
    | Parallele p -> List.fold_left (@) [] (List.map loop p)
  and note_or_quiet = function
    | Note (h, d, v) -> [(0, 0, NoteON (h, v)) ; (d, 0, NoteOFF (h, v))] 
    | Silence d -> [(0, 0, NoteON (0, 0)) ; (d, 0, NoteOFF (0, 0))]
    | _ -> failwith "Pas de séquences de séquences || parralele de parralele"
  in
  
  let lst = loop obj in
  let fmap e = [(0, 0, TimeSignature (4, 4, 24, 8)); (0, 0, Tempo 3250)] @ e @ [(0, 0, EndOfTrack)] in
  
  write (4, List.map (fmap) lst) "test.mid"
;;


(**
   Première voix (thème) du canon perpétuel de Bach.
*)
let premiere_voix = Sequence [
  (* Mesure 1 *)
  Note (60, 1500, 127) ;
  Note (62, 500, 127) ;
  Note (63, 500, 127) ;
  Note (64, 500, 127) ;
  Note (65, 500, 127) ;
  Note (66, 500, 127) ;

  (* Mesure 2 *)
  Note (67, 2000, 127) ;
  (* Note (68, 1000, 127) ;*)
  Note (68, 1250, 127) ;
  Note (65, 250, 127) ;
  Note (61, 250, 127) ;
  Note (60, 250, 127) ;

  (* Mesure 3 *)
  Note (59, 1000, 127) ;
  Silence 1000 ;
  Silence 1000 ;
  (* Note (67, 1000, 127) ; *)

  (* Mesure 4 *)
  Note (67, 2000, 127) ;
  Note (66, 2000, 127) ;
  (* Note (65, 1000, 127) ; *)

  (* Mesure 5 *)
  Note (65, 2000, 127) ;
  Note (64, 2000, 127) ;
  (* Note (63, 1000, 127) ; *)
  
  (* Mesure 6 *)
  Note (63, 2000, 127) ;
  (*  Note (62, 1000, 127) ;*)
  Note (62, 1500, 127) ;
  Note (61, 500, 127) ;
  Note (58, 500, 127) ;
  Note (57, 500, 127) ;
  
  (* Mesure 7 *)
  Note (62, 1000, 127) ;
  Silence 1000 ;
  Silence 1000 ;
  (*Note (67, 1000, 127) ; *)
  
  (* Mesure 8 *)
  Note (67, 2000, 127) ;
  Note (65, 1000, 127) ;
  Note (64, 2000, 127) ;
] ;;


(**
   Seconde voix (accompagnement) du canon perpétuel de Bach.
*)
let seconde_voix =
  Sequence [
    (* Mesure 1 *)
    Silence 250 ;
    Note (48, 250, 127) ;
    Note (51, 250, 127) ;
    Note (55, 250, 127) ;
    Note (60, 2000, 127) ;
    Note (58, 500, 127) ;
    Note (57, 500, 127) ;
    
    (* Mesure 2 *)
    (*Note (58, 1000, 127) ; *)
    Note (58, 1250, 127) ;
    Note (52, 250, 127) ;
    Note (50, 250, 127) ;
    Note (52, 250, 127) ;
    Note (53, 250, 127) ;
    Note (48, 250, 127) ;
    Note (53, 250, 127) ;
    Note (55, 250, 127) ;
    (*Note (56, 1000, 127) ;*)
    
    (* Mesure 3 *)
    Note (56, 1500, 127) ;
    Note (56, 500, 127) ;
    Note (55, 500, 127) ;
    Note (53, 500, 127) ;
    (*Note (51, 1000, 127) ;*)
    Note (51, 1250, 127) ;
    Note (51, 250, 127) ;
    Note (53, 250, 127) ;
    Note (51, 250, 127) ;
    
    (* Mesure 4 *)
    Note (50, 500, 127) ;
    Note (48, 500, 127) ;
    Note (49, 1000, 127) ;
    Silence 500 ;
    Note (50, 500, 127) ;
    Note (51, 500, 127) ;
    Note (50, 500, 127) ;
    
    (* Mesure 5 *)
    Note (48, 250, 127) ;    
    Note (47, 250, 127) ;
    Note (48, 250, 127) ;
    Note (47, 250, 127) ;
    Note (48, 250, 127) ;
    Note (50, 250, 127) ;
    Note (48, 250, 127) ;
    Note (46, 250, 127) ;
    Note (45, 250, 127) ;
    Note (43, 250, 127) ;
    Note (45, 250, 127) ;
    Note (46, 250, 127) ;
    Note (48, 250, 127) ;
    Note (45, 250, 127) ;
    Note (46, 250, 127) ;
    Note (48, 250, 127) ;
    
    (* Mesure 6 *)
    Note (50, 500, 127) ;
    Note (60, 1000, 127) ;
    Note (58, 250, 127) ;
    Note (57, 250, 127) ;
    Note (58, 500, 127) ;
    Note (55, 500, 127) ;
    Note (52, 500, 127) ;
    Note (57, 250, 127) ;
    Note (55, 250, 127) ;
    
    (* Mesure 7 *)
    Note (54, 500, 127) ;
    Note (55, 250, 127) ;
    Note (57, 250, 127) ;
    Note (58, 500, 127) ;
    Note (49, 500, 127) ;
    Note (50, 1000, 127) ;
    Silence 1000 ;
    
    (* Mesure 8 *)
    Silence 500 ;
    (* Note (50, 500, 127) ; *)
    Note (50, 750, 127) ;
    Note (53, 250, 127) ;
    Note (52, 250, 127) ;
    Note (50, 250, 127) ;
    Note (49, 250, 127) ;
    Note (50, 250, 127) ;
    Note (52, 250, 127) ;
    Note (53, 250, 127) ;
    Note (55, 250, 127) ;
    Note (58, 250, 127) ;
    Note (57, 250, 127) ;
    Note (55, 250, 127) ;
  ] ;;


(** Cette variable contient le thème du roi généré à partir de la première voix.
    On transpose 6 fois de deux tons la première voix. *)
let theme_roi =
  let rec loop nb_tons = function
    | 0 -> Silence 0
    | n -> concat_mo (transpose premiere_voix nb_tons) (loop (nb_tons + 2) (n -1))
  in loop 0 6
;;


(** Cette variable contient la voix d'accompagnement. *)
let accompagnement =
  let rec loop nb_tons = function
    | 0 -> Silence 0
    | n -> concat_mo (transpose seconde_voix nb_tons) (loop (nb_tons + 2) (n -1))
  in loop 0 6
;;


(** Cette variable contient le canon perpétuel de Bach. *)
let canon_bach = Parallele [
  concat_mo theme_roi (Silence 4000) ;
  concat_mo (Silence 4000) (transpose accompagnement 7) ;
  concat_mo (copy accompagnement) (Silence 4000) ;
] ;;


let () = save_as_midi canon_bach ;;
