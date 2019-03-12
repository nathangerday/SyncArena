import time
import pygame
from goal import Goal
from player import Player
from multiplayer_game import MultiplayerGame

########## FOR DEBUG ################
current_milli_time = lambda: int(round(time.time() * 1000))
#####################################





class Client:

    def __init__(self, window_width, window_height, title="Client window"):
        self.window_width = window_width
        self.window_height = window_height

        
        pygame.init()
        self.window = pygame.display.set_mode((window_width, window_height))
        pygame.display.set_caption(title)
        self.clock = pygame.time.Clock() # Used to control refresh rate
        MultiplayerGame(self)
        


Client(900, 900)













########### EXEMPLE SOCKET ASYNCHRONE ############

import socket
import sys


HOST = "localhost"
PORT = 45678

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((HOST, PORT))


# readyToSend = True

# while(True):
#     if(readyToSend):
#         readyToSend = False
#         var = input("Give me something to send : ") + "\n"
#         sock.sendall(var.encode(encoding='UTF-8'))

#     try:
#         data = sock.recv(1024, socket.MSG_DONTWAIT) # Will send exception if nothing to receive
#         print("Received : ", data.decode("utf-8"))
#         readyToSend = True
#     except Exception:
#         pass
#         # print("Dors 5 secondes")
#         # time.sleep(5)

# sock.sendall(b"NEWPOS/X0.23231Y-0.5789\n")
# time.sleep(2)






# sock.sendall(("CONNECT/"+sys.argv[1]+"/\n").encode())
# i=0
# while(True):
#     try:
#         data = sock.recv(1024, socket.MSG_DONTWAIT)
#         print(data.decode())
#     except KeyboardInterrupt:
#         sock.close()
#         exit(0)
#     except Exception:
#         pass
#     time.sleep(0.1)
#     i += 1
#     if(i==150 and sys.argv[1] == "kyrnale"):
#         print("SEND POS ====================================")
#         sock.sendall(("NEWPOS/X0.46Y-0.21/\n").encode())













# time.sleep(3)
# sock.sendall(b"CONNECT/kyrnale\n")
# time.sleep(30)