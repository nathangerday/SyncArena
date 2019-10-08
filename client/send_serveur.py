"""Ensemble de fonctions permettant d'envoyer des données au serveur en respectant le protocole
"""



def connect(socket, username):
    socket.sendall(("CONNECT/"+str(username)+"/\n").encode())

def newpos(socket, pos):
    posstr = "X"+str(pos[0])+"Y"+str(pos[1])
    socket.sendall(("NEWPOS/"+posstr+"/\n").encode())

def exitsession(socket, username):
    socket.sendall("EXIT/"+str(username)+"/\n")

def newcom(socket, angle, nb_thrust, shoot):
    # If to allow compatibily with servers which don't handle shoot
    if(shoot):
        socket.sendall(("NEWCOM2/A"+str(angle)+"T"+str(nb_thrust)+"S1/\n").encode())
    else:
        socket.sendall(("NEWCOM/A"+str(angle)+"T"+str(nb_thrust)+"/\n").encode())

def sendmsg(socket, msg):
    socket.sendall(("ENVOI/"+str(msg)+"/\n").encode())

def sendpmsg(socket, user, msg):
    socket.sendall(("PENVOI/"+str(user)+"/"+str(msg)+"/\n").encode())
    
def sendrace(socket): # Indique qu'on veut lancer une course au serveur
    socket.sendall("RACE/\n".encode())