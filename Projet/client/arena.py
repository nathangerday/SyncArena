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
        self.next_goal = None
        self.obstacles = []
        self.attacks = []

    def draw(self, window):
        """Dessine, sur la fenetre donnee, toutes les entites sur lesquelles l'arene a une reference
        """
        #Draw attacks
        for a in self.attacks:
            x = int(a.pos[0] * self.h + self.h)
            y = int((-a.pos[1]) * self.l + self.l)
            a.draw(window, x, y)

        #Draw obstacles
        for o in self.obstacles:
            x = int(o.x * self.h + self.h)
            y = int((-o.y) * self.l + self.l)

            o.draw(window, x, y)
        
        
        #Draw Goal
        if(self.goal != None):
            x = int(self.goal.x * self.h + self.h)
            y = int((-self.goal.y) * self.l + self.l)

            self.goal.draw(window, x, y)

        #Draw next goal (only if in race mode)
        if(self.next_goal != None):
            x = int(self.next_goal.x * self.h + self.h)
            y = int((-self.next_goal.y) * self.l + self.l)

            self.next_goal.draw(window, x, y)

        #Draw players
        for p in self.players.values():
            x = p.pos[0] * self.h + self.h
            y = (-p.pos[1]) * self.l + self.l
            p.draw(window, x, y)

       

        
    def update(self):
        for p in self.attacks:
            p.update()

        for p in self.players.values():
            oldX = p.pos[0]
            oldY = p.pos[1]
            p.update()

            #Collision with obstacles
            for o in self.obstacles:
                if(o.isInCollisionWith(p)):
                    p.moveTo(oldX, oldY)
                    p.inverseVector()

            #Collision with players
            for otherp in self.players.values():
                if(otherp != p):
                    if(p.isInCollisionWith(otherp)):
                        p.moveTo(oldX, oldY)
                        p.inverseVector()
