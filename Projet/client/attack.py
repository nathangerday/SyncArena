import pygame
from const import SHOOT_RADIUS

class Attack:
    def __init__(self, pos, vector, direction):
        self.pos = pos
        self.vector = vector
        self.direction = direction


    # TODO Update



    def draw(self, window, xwindow, ywindow):
        color = (255, 0, 0)
        pygame.draw.circle(window, color, (xwindow, ywindow), int(SHOOT_RADIUS * pygame.display.get_surface().get_width()/2))