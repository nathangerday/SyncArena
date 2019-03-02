import time
import math
import pygame

########## FOR DEBUG ################
import time
current_milli_time = lambda: int(round(time.time() * 1000))
#####################################

def start():
    pygame.init()
    screen = pygame.display.set_mode((800, 600))
    pygame.display.set_caption("Client PC2R Nathan GERDAY")
    pos = (0.0, 0.0)
    direction = 0 # 360 degrees angle relative to x axe
    vector = (0.0, 0.0)
    refresh_tickrate = 60
    clock = pygame.time.Clock()
    turnit = 20
    thrustit = 2

    run = True
    while(run):
        now = current_milli_time() # TODO Delete
         
        for event in pygame.event.get():
            if(event.type == pygame.QUIT):
                run = False

            if(event.type == pygame.KEYDOWN):
                if(event.key == pygame.K_SPACE):
                    vector = (vector[0] + thrustit*math.cos(direction), vector[1]+ thrustit*math.sin(direction))


            # keys= pygame.key.get_pressed()

            # if(keys[pygame.K_SPACE]):
            #     vector = (vector[0] + thrustit*math.cos(direction), vector[1]+ thrustit*math.sin(direction))
            #     print(vector)

        screen.fill((0,0,0))
        pos = (pos[0] + vector[0], pos[1] + vector[1])
        pygame.draw.rect(screen, (255, 0, 0), (pos[0], pos[1], 10, 20))
        
        pygame.display.update()
        clock.tick(refresh_tickrate)
        # print(current_milli_time() - now, " ms per frame") #TODO Delete
    pygame.quit()

start()


# import socket
 
# HOST = "localhost"
# PORT = 8080
 
# sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# sock.connect((HOST, PORT))
 
# sock.sendall(b"Hello\n")
# sock.send
# data = sock.recv(1024)
# print(float(data))
