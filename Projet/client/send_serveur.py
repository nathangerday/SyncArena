def connect(socket, username):
    socket.sendall(("CONNECT/"+str(username)+"/\n").encode())

def newpos(socket, pos):
    posstr = "X"+str(pos[0])+"Y"+str(pos[1])
    socket.sendall(("NEWPOS/"+posstr+"/\n").encode())

def exitsession(socket, username):
    socket.sendall("EXIT/"+str(username)+"/\n")
