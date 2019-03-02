import time
import math
import pygame

########## FOR DEBUG ################
current_milli_time = lambda: int(round(time.time() * 1000))
#####################################

WINDOW_HEIGHT = 900
WINDOW_WIDTH = 900

REFRESH_TICKRATE = 60
MAX_THRUST = 0.01



class Player:
    """Represente une voiture pouvant etre controlee par le joueur ou par le serveur
    """
    
    def __init__(self, path_to_sprite):
        self.pos = (0.0, 0.0)
        self.direction = 0
        self.vector = (0.0, 0.0)
        self.turnit = 0.03
        self.thrustit = 0.002
        self.original_sprite = pygame.image.load(path_to_sprite)
        self.original_sprite = pygame.transform.scale(self.original_sprite, (64, 64)) # Original sprite which should never change
        self.current_sprite = self.original_sprite # Sprite with correct transform

    def thrust(self):
        self.vector = (self.vector[0] + self.thrustit*math.cos(self.direction),
                       self.vector[1] + self.thrustit*math.sin(self.direction))
        
        self.vector = (min(self.vector[0], MAX_THRUST), min(self.vector[1], MAX_THRUST))
        self.vector = (max(self.vector[0], -MAX_THRUST), max(self.vector[1], -MAX_THRUST))


    def clock(self):
        self.direction -= self.turnit

    def anticlock(self):
        self.direction += self.turnit

    def update(self):
        """Fonction a appele lorsque l'on veut mettre a jour l'entite avec les bonnes coordonnees / rotation
        """

        #TODO Modulo on float not precise, possibly use Decimal, see https://stackoverflow.com/questions/20830067/remainder-on-float-in-python?lq=1
        newx = (self.pos[0] + 1.0 + self.vector[0]) % 2 - 1 
        newy = (self.pos[1] + 1.0 + self.vector[1]) % 2 - 1

        self.pos = (newx, newy)
        self.current_sprite = pygame.transform.rotate(self.original_sprite, math.degrees(self.direction))
        # print(self.pos)


class Arena:
    """ Arene s'occupe de la gestion des toutes les entites du jeux (joueurs, objects, etc).
        Elle s'occupe egalement de convertir les positions abstraites des objects (entre -1 et 1) à des positions cohérentes dans la fenetre
        C'est donc l'arene qui s'occupe de draw directement chaque entite sur la fenetre
    """
    def __init__(self, window_width, window_height):
        self.h = window_height / 2
        self.l = window_width / 2
        self.players = []

    def draw(self, window):
        """Dessine, sur la fenetre donnee, toutes les entites sur lesquelles l'arene a une reference
        """
        for p in self.players:
            x = p.pos[0] * self.h + self.h
            y = (-p.pos[1]) * self.l + self.l
            window.blit(p.current_sprite, (x, y))
            


def createPygameWindow(width, height, title):
    pygame.init()
    window = pygame.display.set_mode((width, height))
    pygame.display.set_caption(title)
    return window



def start():
    window = createPygameWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "PC2R Projet / Nathan GERDAY")
    clock = pygame.time.Clock() # Used to control refresh rate

    # Creation de l'arnene
    arena = Arena(WINDOW_WIDTH, WINDOW_HEIGHT)

    # Creation des entites
    main_player = Player("evilFighter.png")
    arena.players.append(main_player)
    
    

    run = True
    while(run):
        # now = current_milli_time() # TODO Delete

        for event in pygame.event.get():
            if(event.type == pygame.QUIT):
                run = False

            if(event.type == pygame.KEYDOWN):
                if(event.key == pygame.K_SPACE):
                    main_player.thrust()
                if(event.key == pygame.K_ESCAPE):
                    run = False


        keys = pygame.key.get_pressed()
        if(keys[pygame.K_q] or keys[pygame.K_LEFT]):
            main_player.anticlock()
        elif(keys[pygame.K_d] or keys[pygame.K_RIGHT]):
            main_player.clock()

        main_player.update()

        #TODO Optimisation : Effacer uniquement ce qui change
        window.fill((0, 0, 0)) # Efface tout ce qui est sur la fenetre
        arena.draw(window) # Dessine toutes les entites
        pygame.display.update() # Met a jour la fenetre

        clock.tick(REFRESH_TICKRATE) # Limite le nombre de mise a jour par seconde
        # print(current_milli_time() - now, " ms per frame") #TODO Delete
    pygame.quit()

start()


########### EXEMPLE SOCKET ############

# import socket

# HOST = "localhost"
# PORT = 8080

# sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# sock.connect((HOST, PORT))

# sock.sendall(b"Hello\n")
# sock.send
# data = sock.recv(1024)
# print(float(data))
