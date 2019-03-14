def connect(socket, username):
    socket.sendall(("CONNECT/"+str(username)+"/\n").encode())

def newpos(socket, pos):
    posstr = "X"+str(pos[0])+"Y"+str(pos[1])
    socket.sendall(("NEWPOS/"+posstr+"/\n").encode())

def exitsession(socket, username):
    socket.sendall("EXIT/"+str(username)+"/\n")

def newcom(socket, angle, nb_thrust):
    socket.sendall(("NEWCOM/A"+str(angle)+"T"+str(nb_thrust)+"/\n").encode())
