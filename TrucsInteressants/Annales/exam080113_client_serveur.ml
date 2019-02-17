(* Q4 *)

module Chef = struct
  (* Q4.1 Statuts des employés *)
  type statut_employe = Vacant | Pause of int | Busy

  (* Q4.1 *)
  let gestion_employes nombre_employes =
  object
    val employes : 'employe array = 
      Array.init (fun i -> this#creer_employe i) nombre_employes

    (* File (FIFO) pour gérer les employés disponibles. *)
    val employes_disponibles =
      (object
         val data = Event.new_channel ()
         method select = 
           (* Attention: sélection naïve! *)
           Event.sync (Event.receive data)
         method add e = 
           ignore (Thread.create (fun () -> Event.send data e) ())
       end)

    (* Q4.2 *)
    val mutable temperature = 20

    (* Q4.1 *)
    method creer_employe id = 
      (* Les employés seuls n'ont pas besoin d'être protégés des accès
         concurrents car seuls eux communiquent leurs statuts au
         chef. En Java, pas besoin de synchroniser, et en OCaml et C,
         pas besoin de mutex.  Vous pouviez toutefois choisir une autre
         architecture logicielle imposant une gestion de la concurrence
         à ce niveau.  *)
    object
      val id : int = id

      val mutable contribution_chaleur = 0

      (* Q4.1 statut courant de l'employé, tel que déclaré par celui-ci. *)
      val mutable statut = Vacant
        
      (* Q4.1 quota de pause restante *)
      val mutable pause_restante = 42

      (* Q4.1 busy : trivial à faire *) 
      method setBusy = statut <- Busy

      (* Q4.1 pause : il accorder une pause dépendant du quota!  (et
         éventuellement de la demande).  Le calcul de la pause
         accordée se fait ici mais le retour se fera à la gestion du
         protocole. *)
      method setPause (duree: int) : int =
        if pause_restante >= duree then
          begin
            statut <- Pause duree; 
            (* Q6. Fondamentalement, ce n'est pas possible d'empêcher
               l'augmentation de plus de 2°C car au bout d'un moment
               tous les employés ont consommé leur quota de pause. En
               revanche, on peut faire en sorte de n'accorder qu'une
               seule minute de pause plutôt que tout ce que demande
               l'employé, sauf si les employés ont une auto-gestion
               optimale vis-à-vis de la recette du bureau de poste. *)
            pause_restante <- pause_restante - duree;
            duree
          end
        else
          if pause_restante > 0 then (* il reste au moins une minute accordable *)
            begin
              statut <- Pause 1;
              pause_restante <- pause_restante - 1;
              1
            end
          else
            (* aucune pause accordée *)
            0
              
      (* vacant : si l'employé revient de pause, il faut faire baisser
         la température! *)
      method setVacant = 
        match statut with
          | Pause n ->
              statut <- Vacant;
              if contribution_chaleur > n then
                temperature <- temperature - n
              else
                (* Q4.2 Il ne peut pas faire baisser la température davantage qu'il ne l'a fait augmenter. S'il a fait trop de pause, tant pis. *)
                (* Q6. On pourrait empêcher l'employé de faire trop de pause.  *)
                temperature <- temperature - contribution_chaleur;
          | _ -> statut <- Vacant

      method selection_employes = 
        
    end (* employe *)


      

  end
