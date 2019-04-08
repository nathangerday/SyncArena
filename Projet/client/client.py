import re
import pygame
from multiplayer_game import MultiplayerGame
from menu import Menu
class Client:

    def __init__(self, window_width, window_height, title="Client window"):
        self.window_width = window_width
        self.window_height = window_height

        pygame.init()
        self.window = pygame.display.set_mode((window_width, window_height))
        pygame.display.set_caption(title)
        self.clock = pygame.time.Clock() # Used to control refresh rate
        Menu(self)
        # MultiplayerGame(self, username)
        


Client(900, 900)













########### EXEMPLE SOCKET ASYNCHRONE ############



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


# import socket
# import sys


# HOST = "localhost"
# PORT = 45678

# sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# sock.connect((HOST, PORT))


# sock.sendall(("CONNECT/"+sys.argv[1]+"/\n").encode())
# time.sleep(10)
# i=0
# while(True):
#     try:
#         data = sock.recv(1024, socket.MSG_DONTWAIT)
#         print(data.decode().split("\n"))
#     except KeyboardInterrupt:
#         sock.close()
#         exit(0)
#     except Exception:
#         pass
#     print("================")
#     time.sleep(5)
#     i += 1
#     # if(i==150 and sys.argv[1] == "kyrnale"):
#     #     print("SEND POS ====================================")
#     #     sock.sendall(("NEWPOS/X0.46Y-0.21/\n").encode())













# time.sleep(3)
# sock.sendall(b"CONNECT/kyrnale\n")
# time.sleep(30)