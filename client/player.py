
import math
import random
import pygame
from const import *

class Player:
    """Represente un vaisseau ainsi que des operations permettant de le controler
    """
    

    def __init__(self, path_to_sprite, username, pos=(random.uniform(-1, 1), random.uniform(-1, 1)), to_display=False):
        self.sprite_size = 32
        self.username = username
        self.to_display = to_display
        self.pos = pos
        self.direction = 0
        self.vector = (0.0, 0.0)
        self.radius = VE_RADIUS
        self.score = 0
        self.hasShoot = False # Indique si on tire pendant ce tick, on ne peut tirer qu'une fois pas tick
        self.command_angle = 0
        self.command_thrust = 0
        self.original_sprite = pygame.image.load(path_to_sprite)
        self.original_sprite = pygame.transform.scale(self.original_sprite, (self.sprite_size, self.sprite_size)) # Original sprite which should never change
        self.current_sprite = self.original_sprite # Sprite with correct transform
        
    
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
    
    def shoot(self):
        self.hasShoot = True

    def moveTo(self, x, y):
        self.pos = (x, y)

    def inverseVector(self):
        self.vector = (-self.vector[0], -self.vector[1])

    def reset(self):
        self.vector = (0.0, 0.0)
        self.score = 0
        self.command_angle = 0
        self.command_thrust = 0
        self.hasShoot = False

    def isInCollisionWith(self, player):
        distance = math.sqrt(math.pow(player.pos[0] - self.pos[0], 2) + math.pow(player.pos[1] - self.pos[1], 2))
        return (distance < self.radius + player.radius)
        

    def update(self):
        """Fonction a appele lorsque l'on veut mettre a jour l'entite avec les bonnes coordonnees / rotation
        """

        newx = (self.pos[0] + 1.0 + self.vector[0]) % 2 - 1 
        newy = (self.pos[1] + 1.0 + self.vector[1]) % 2 - 1

        self.pos = (newx, newy)
        self.current_sprite = pygame.transform.rotate(self.original_sprite, math.degrees(self.direction))

    def draw(self, window, xwindow, ywindow):
        if(self.to_display):
            pygame.draw.circle(window, (45,45,45), (int(xwindow), int(ywindow)), int(self.radius * pygame.display.get_surface().get_width()/2))
            window.blit(self.current_sprite, (xwindow - self.sprite_size/2, ywindow- self.sprite_size/2))
