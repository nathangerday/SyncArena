import time
import math
import pygame

########## FOR DEBUG ################
current_milli_time = lambda: int(round(time.time() * 1000))
#####################################

REFRESH_TICKRATE = 60
MAX_THRUST = 0.01



class Player:

    def __init__(self, path_to_sprite):
        self.pos = (0.0, 0.0)
        self.direction = 0
        self.vector = (0.0, 0.0)
        self.turnit = 0.03
        self.thrustit = 0.002
        self.original_sprite = pygame.image.load(path_to_sprite)
        self.original_sprite = pygame.transform.scale(self.original_sprite, (64, 64))
        self.current_sprite = self.original_sprite

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
        # self.pos = (self.pos[0] + self.vector[0], self.pos[1] + self.vector[1])

        #TODO Modulo on float not precise, possible use Decimal, see https://stackoverflow.com/questions/20830067/remainder-on-float-in-python?lq=1
        newx = (self.pos[0] + 1.0 + self.vector[0]) % 2 - 1 
        newy = (self.pos[1] + 1.0 + self.vector[1]) % 2 - 1

        self.pos = (newx, newy)
        self.current_sprite = pygame.transform.rotate(self.original_sprite, math.degrees(-self.direction))

    # def draw(self, window):
        # window.blit(sprite, self.pos)
        # pygame.draw.rect(window, (255, 0, 0), (self.pos[0], self.pos[1], 10, 20))


class Arena:
    def __init__(self, window_width, window_height):
        self.h = window_height / 2
        self.l = window_width / 2
        self.players = []

    def draw(self, window):
        for p in self.players:
            x = p.pos[0] * self.h + self.h
            y = p.pos[1] * self.l + self.l
            window.blit(p.current_sprite, (x, y))
            


def createPygameWindow(width, height, title):
    pygame.init()
    window = pygame.display.set_mode((width, height))
    pygame.display.set_caption(title)
    return window



def start():
    window = createPygameWindow(900, 900, "PC2R Projet / Nathan GERDAY")
    arena = Arena(900, 900)
    clock = pygame.time.Clock() # Used to control refresh rate

    main_player = Player("evilFighter.png")
    arena.players.append(main_player)

    run = True
    while(run):
        now = current_milli_time() # TODO Delete

        for event in pygame.event.get():
            if(event.type == pygame.QUIT):
                run = False

            if(event.type == pygame.KEYDOWN):
                if(event.key == pygame.K_SPACE):
                    main_player.thrust()


        keys = pygame.key.get_pressed()
        if(keys[pygame.K_q]):
            main_player.clock()
        elif(keys[pygame.K_d]):
            main_player.anticlock()

        window.fill((0, 0, 0))
        main_player.update()

        arena.draw(window)

        pygame.display.update()
        clock.tick(REFRESH_TICKRATE)
        print(current_milli_time() - now, " ms per frame") #TODO Delete
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
