import socket
import re
import time
import pygame
import send_serveur
from arena import Arena
from player import Player
from const import REFRESH_TICKRATE, HOST, PORT, WIN_CAP, SERVER_TICKRATE
from logger import Logger
from goal import Goal
from score import Score
from obstacle import Obstacle
from input_box import InputBox
from attack import Attack


class MultiplayerGame:

    def __init__(self, client, username):
        self.client = client
        self.logger = Logger()
        self.score_displayer = Score(username)
        self.inputbox = InputBox(5, self.client.window_width - 30, self.logger)
        self.username = username
        self.is_socket_connected_to_server = False
        self.session_state = "nosession" # Indicate whether we not connected, requested connection or in a session
        self.socket = None
        
        self.arena = Arena(self.client.window_width, self.client.window_height) # Creation de l'arene
        self.main_player = Player("evilFighter.png", self.username, to_display=True)

        self.last_newcom = 0

        self.arena.players[username] = self.main_player
        self.main_loop()

    def main_loop(self):
        while(True):
            self.handle_server_coms()

            self.handle_keyboard_input()

            self.enitities_update()

            self.draw_frame()

            self.client.clock.tick(REFRESH_TICKRATE) # Limite le nombre de mise a jour par seconde

    def handle_server_coms(self):
        if(not self.is_socket_connected_to_server):
            self.logger.add_message("Connecting to server...")

            # TODO Handle connection failed
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.connect((HOST, PORT))

            self.logger.add_message("Connection successful.")
            self.is_socket_connected_to_server = True
            
            self.inputbox.socket = self.socket
        
        if(self.session_state == "nosession"):
            self.logger.add_message("Trying to join a session...")
            self.session_state = "request"
            send_serveur.connect(self.socket, self.username)
        elif(self.session_state == "ingame"):
            # send_serveur.newpos(self.socket, self.main_player.pos)
        
            if(time.time() - self.last_newcom > 1 / SERVER_TICKRATE):
                send_serveur.newcom(self.socket, self.main_player.command_angle, self.main_player.command_thrust, self.main_player.hasShoot)
                self.main_player.hasShoot = False
                self.last_newcom = time.time()

                # Commands sent, reset
                self.main_player.command_angle = 0
                self.main_player.command_thrust = 0


        self.handle_server_responses()

    def handle_server_responses(self):
        try:
            data = self.socket.recv(8192, socket.MSG_DONTWAIT)
        except BlockingIOError: # Nothing to read
            return
        
        commands = data.decode().split("\n")
        commands = [cmd.split("/") for cmd in commands]
        print(commands)

        #TODO Handle theses commands in another method
        for cmd in commands:
            if(cmd[0] == "WELCOME"):
                self.apply_command_welcome(cmd)
            elif(cmd[0] == "DENIED"):
                self.apply_command_denied(cmd)
            elif(cmd[0] == "NEWPLAYER"):
                self.apply_command_newplayer(cmd)
            elif(cmd[0] == "PLAYERLEFT"):
                self.apply_command_playerleft(cmd)
            elif(cmd[0] == "SESSION"):
                self.apply_command_session(cmd)
            elif(cmd[0] == "WINNER"):
                self.apply_command_winner(cmd)
            elif(cmd[0] == "TICK"):
                self.apply_command_tick(cmd)
            elif(cmd[0] == "TICK2"):
                self.apply_command_tick2(cmd) 
            elif(cmd[0] == "NEWOBJ"):
                self.apply_command_newobj(cmd)
            elif(cmd[0] == "RECEPTION"):
                self.apply_command_reception(cmd)
            elif(cmd[0] == "PRECEPTION"):
                self.apply_command_preception(cmd)

    def apply_command_welcome(self, cmd):
        phase = cmd[1]
        scores = cmd[2]
        coord = cmd[3]
        obs_coords = cmd[4]

        if(phase == "waiting"):
            self.logger.add_message("Waiting to start session")
            self.session_state = "waiting"
        elif(phase == "ingame"):
            self.logger.add_message("Joining a game")   
            self.session_state = "ingame"
            goalx, goaly = parse_coord(coord)
            self.arena.goal = Goal(goalx, goaly)
            self.main_player.to_display = False # Wait to get coord before displaying
            for s in scores.split("|"):
                [name, score] = s.split(":")
                if(name in self.arena.players):
                    self.arena.players[name].score = int(score)
                else:
                    new_player = Player("spaceship_sprite.png", name)
                    new_player.score = int(score)
                    self.arena.players[name] = new_player
            
            if(len(obs_coords) > 0):
                for o in obs_coords.split("|"):
                    pos = parse_coord(o)
                    self.arena.obstacles.append(Obstacle(pos[0], pos[1]))

    def apply_command_denied(self, cmd):
        self.logger.add_message("Joining session failed")
        self.stop()

    def apply_command_newplayer(self, cmd):
        user = cmd[1]
        self.logger.add_message(user + " joins the game")
        self.arena.players[user] = Player("spaceship_sprite.png", user)

    def apply_command_playerleft(self, cmd):
        user = cmd[1]
        self.logger.add_message(user + " left the game")
        if(user in self.arena.players):
            del self.arena.players[user]

    def apply_command_session(self, cmd):
        coords = cmd[1]
        coord = cmd[2]
        obs_coords = cmd[3]

        self.logger.add_message("Session starting !")
        players_coords = coords.split("|")
        for p in players_coords:
            [name, pos] = p.split(":")
            pos = parse_coord(pos)
            if(name in self.arena.players):
                self.arena.players[name].reset()
                self.arena.players[name].moveTo(pos[0], pos[1])
                self.arena.players[name].to_display = True
            else:
                self.arena.players[name] = Player("spaceship_sprite.png", name, pos, True)
        goalx, goaly = parse_coord(coord)
        self.arena.goal = Goal(goalx, goaly)
        
        if(len(obs_coords) > 0):
            for o in obs_coords.split("|"):
                pos = parse_coord(o)
                self.arena.obstacles.append(Obstacle(pos[0], pos[1]))
        
        self.session_state = "ingame"
        

    def apply_command_winner(self, cmd):
        scores = cmd[1]
        for s in scores.split("|"):
            [name, score] = s.split(":")
            self.arena.players[name].score = int(score)
            if(int(score) == WIN_CAP):
                winner = name
        if(self.username == winner):
            self.logger.add_message("End of game, congratulations you are the winner !!")
        else:
            self.logger.add_message("End of game, winner is : " + winner)
        self.logger.add_message("A new game will restart soon")
        self.session_state = "waiting"
        self.arena.goal = None
        self.arena.obstacles.clear()
        self.arena.attacks.clear()
        # Don't display other player if not ingame
        for p in self.arena.players.values():
            if(not p.username == self.username):
                p.to_display = False
        

    def apply_command_tick(self, cmd):
        coords = cmd[1]
        self.arena.attacks.clear()
        players_coords = coords.split("|")
        for p in players_coords:
            [name, vals] = p.split(":")
            pos, vector, direction = parse_coord(vals)
            player = self.arena.players[name]
            player.moveTo(pos[0], pos[1])
            player.direction = direction
            player.vector = vector
            player.to_display = True

    def apply_command_tick2(self, cmd):
        self.apply_command_tick(cmd)

        self.arena.attacks.clear()
        coords = cmd[2]
        for a in coords.split("|"):
            pos = parse_coord(a)
            self.arena.attacks.append(Attack(pos[0], pos[1], pos[2]))


    def apply_command_newobj(self, cmd):
        coord = cmd[1]
        scores = cmd[2]
        self.logger.add_message("Setting a new objectif...")
        for s in scores.split("|"):
            [name, score] = s.split(":")
            self.arena.players[name].score = int(score)

        goalx, goaly = parse_coord(coord)
        self.arena.goal = Goal(goalx, goaly)

    def apply_command_reception(self, cmd):
        message = cmd[1]
        self.logger.add_message(message, (255,0,0))

    def apply_command_preception(self, cmd):
        message = cmd[1]
        user = cmd[2]
        self.logger.add_message("From "+user+" : "+message, (255,105,180))

    def handle_keyboard_input(self):
        keys = pygame.key.get_pressed()
        if(not self.inputbox.isWriting):
            if(keys[pygame.K_q] or keys[pygame.K_LEFT]):
                self.main_player.anticlock()
            elif(keys[pygame.K_d] or keys[pygame.K_RIGHT]):
                self.main_player.clock()
        else:
            self.inputbox.handle_continuous_pressed_keys(keys)
                
        for event in pygame.event.get():
            if(event.type == pygame.QUIT):
                self.stop()

            if(event.type == pygame.KEYDOWN):
                if(not self.inputbox.isWriting):
                    if(event.key == pygame.K_SPACE):
                        self.main_player.thrust()
                    if(event.key == pygame.K_e):
                        self.main_player.shoot()
                    if(event.key == pygame.K_ESCAPE):
                        self.stop()
                self.inputbox.handle_input_event(event)



    def enitities_update(self):
        self.arena.update() # Updates every players in the arena including the main_player

    def draw_frame(self):
        self.client.window.fill((0, 0, 0)) # Efface tout ce qui est sur la fenetre
        self.arena.draw(self.client.window) # Dessine toutes les entites
        self.logger.draw(self.client.window)
        self.score_displayer.draw(self.arena.players, self.client.window)
        self.inputbox.draw(self.client.window)
        pygame.display.update() # Met a jour la fenetre

    def stop(self):
        if(not self.socket is None):
            self.socket.close()
        pygame.quit()
        exit(0)




def parse_coord(coord):
        """Parse coordinates with format X0.84Y0.48 to a tuple of float
        """
        vals = re.split("VX|VY|T", coord)
        pos = re.split("X|Y", vals[0])
        if(len(vals) == 1): # No vector or direction in received coordinates
            return (float(pos[1]), float(pos[2]))
        elif(len(vals) == 4):
            vector = float(vals[1]), float(vals[2])
            angle = float(vals[3])
            return ((float(pos[1]), float(pos[2])), vector, angle)

