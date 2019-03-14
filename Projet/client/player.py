
import math
import random
import pygame
from const import *

class Player:
    """Represente une voiture pouvant etre controlee par le joueur ou par le serveur
    """
    

    def __init__(self, path_to_sprite, username, pos=(random.uniform(-1, 1), random.uniform(-1, 1)), to_display=False):
        self.sprite_size = 32
        self.username = username
        self.to_display = to_display
        self.pos = pos
        self.direction = 0
        self.vector = (0.0, 0.0)
        self.score = 0
        self.original_sprite = pygame.image.load(path_to_sprite)
        self.original_sprite = pygame.transform.scale(self.original_sprite, (self.sprite_size, self.sprite_size)) # Original sprite which should never change
        self.current_sprite = self.original_sprite # Sprite with correct transform

        self.command_angle = 0
        self.command_thrust = 0
        
    
    def thrust(self):
        ## Partie B
        self.command_thrust += 1
        

        ## Partie A : Les joueurs calculent eux meme leur vecteur vitesse
        # self.vector = (self.vector[0] + THRUSTIT*math.cos(self.direction),
        #                self.vector[1] + THRUSTIT*math.sin(self.direction))
        
        # self.vector = (min(self.vector[0], MAX_THRUST), min(self.vector[1], MAX_THRUST))
        # self.vector = (max(self.vector[0], -MAX_THRUST), max(self.vector[1], -MAX_THRUST))


    def clock(self):
        self.command_angle -= TURNIT

        ## Le client ne change plus directement la position du joueur
        # self.direction -= TURNIT

    def anticlock(self):
        self.command_angle += TURNIT
        
        ## Le client ne change plus directement la position du joueur
        # self.direction += TURNIT
    
    def moveTo(self, x, y):
        self.pos = (x, y)

    def reset(self):
        self.vector = (0.0, 0.0)
        self.score = 0
        self.command_angle = 0
        self.command_thrust = 0

    def update(self):
        """Fonction a appele lorsque l'on veut mettre a jour l'entite avec les bonnes coordonnees / rotation
        """

        #TODO Modulo on float not precise, possibly use Decimal, see https://stackoverflow.com/questions/20830067/remainder-on-float-in-python?lq=1
        newx = (self.pos[0] + 1.0 + self.vector[0]) % 2 - 1 
        newy = (self.pos[1] + 1.0 + self.vector[1]) % 2 - 1

        self.pos = (newx, newy)
        self.current_sprite = pygame.transform.rotate(self.original_sprite, math.degrees(self.direction))
        # print(self.pos)

    def draw(self, window, xwindow, ywindow):
        if(self.to_display):
            window.blit(self.current_sprite, (xwindow, ywindow))
