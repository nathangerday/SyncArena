import math
import pygame
from const import OB_RADIUS

class Obstacle:
    """Représente un unique obstacle dans le jeu.
    """


    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.radius = OB_RADIUS
    
    def isInCollisionWith(self, player):
        distance = math.sqrt(math.pow(player.pos[0] - self.x, 2) + math.pow(player.pos[1] - self.y, 2))
        return (distance < self.radius + player.radius)

    def draw(self, window, xwindow, ywindow):
        color = (255, 255, 255)
        pygame.draw.circle(window, color, (xwindow, ywindow), int(self.radius * pygame.display.get_surface().get_width()/2))
