import pygame
from const import SHOOT_RADIUS

class Attack:
    """Represente une attaque unique dans le jeu avec sa position courante et son vecteur indiquant sa vitesse.
    """


    def __init__(self, pos, vector, direction):
        self.pos = pos
        self.vector = vector
        self.direction = direction


    def update(self):
        newx = (self.pos[0] + 1.0 + self.vector[0]) % 2 - 1 
        newy = (self.pos[1] + 1.0 + self.vector[1]) % 2 - 1
        self.pos = (newx, newy)


    def draw(self, window, xwindow, ywindow):
        color = (255, 0, 0)
        pygame.draw.circle(window, color, (xwindow, ywindow), int(SHOOT_RADIUS * pygame.display.get_surface().get_width()/2))