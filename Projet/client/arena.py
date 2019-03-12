class Arena:
    """ Arene s'occupe de la gestion des toutes les entites du jeux (joueurs, objects, etc).
        Elle s'occupe egalement de convertir les positions abstraites des objects (entre -1 et 1) à des positions cohérentes dans la fenetre
        C'est donc l'arene qui s'occupe de draw directement chaque entite sur la fenetre
    """
    def __init__(self, window_width, window_height):
        self.h = window_height / 2
        self.l = window_width / 2
        self.players = []
        self.goal = None

    def draw(self, window):
        """Dessine, sur la fenetre donnee, toutes les entites sur lesquelles l'arene a une reference
        """
        
        #Draw players
        for p in self.players:
            x = p.pos[0] * self.h + self.h - p.sprite_size/2
            y = (-p.pos[1]) * self.l + self.l - p.sprite_size/2
            p.draw(window, x, y)
        
        #Draw Goal
        if(self.goal != None):
            x = int(self.goal.x * self.h + self.h)
            y = int((-self.goal.y) * self.l + self.l)

            # UGLY (Don't reelly wanna give the player coordinates) Changing color when the "real" player is passing through
            self.goal.draw(window, x, y, self.players[0].pos[0], self.players[0].pos[1])
        
    def update(self):
        for p in self.players:
            p.update()