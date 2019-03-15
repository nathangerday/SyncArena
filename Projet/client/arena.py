class Arena:
    """ Arene s'occupe de la gestion des toutes les entites du jeux (joueurs, objects, etc).
        Elle s'occupe egalement de convertir les positions abstraites des objects (entre -1 et 1) à des positions cohérentes dans la fenetre
        C'est donc l'arene qui s'occupe de draw directement chaque entite sur la fenetre
    """
    def __init__(self, window_width, window_height):
        self.h = window_height / 2
        self.l = window_width / 2
        self.players = {}
        self.goal = None
        self.obstacles = []

    def draw(self, window):
        """Dessine, sur la fenetre donnee, toutes les entites sur lesquelles l'arene a une reference
        """
        
        #Draw players
        for p in self.players.values():
            x = p.pos[0] * self.h + self.h - p.sprite_size/2
            y = (-p.pos[1]) * self.l + self.l - p.sprite_size/2
            p.draw(window, x, y)
        
        #Draw Goal
        if(self.goal != None):
            x = int(self.goal.x * self.h + self.h)
            y = int((-self.goal.y) * self.l + self.l)

            self.goal.draw(window, x, y)

        #Draw obstacles
        for o in self.obstacles:
            x = int(o.x * self.h + self.h)
            y = int((-o.y) * self.l + self.l)

            o.draw(window, x, y)

        
    def update(self):
        for p in self.players.values():
            oldX = p.pos[0]
            oldY = p.pos[1]
            p.update()
            for o in self.obstacles:
                if(o.isInCollisionWith(p)):
                    p.moveTo(oldX, oldY)
                    p.inverseVector()

            for otherp in self.players.values():
                if(otherp != p):
                    if(p.isInCollisionWith(otherp)):
                        p.moveTo(oldX, oldY)
                        p.inverseVector()
                        otherp.inverseVector() # TODO Potentiel problem, if both players do that, they cancel each other
