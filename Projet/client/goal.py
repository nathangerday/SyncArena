import math
import pygame
from const import OBJ_RADIUS
class Goal:

    def __init__(self, x, y, is_secondary=False):
        self.x = x
        self.y = y
        self.obj_radius = OBJ_RADIUS
        self.collected = False
        self.is_secondary = is_secondary

    def isCollectable(self, otherx, othery):
        distance = math.sqrt(math.pow(otherx - self.x, 2) + math.pow(othery - self.y, 2))
        # print(distance)
        return (not self.collected) and (distance < self.obj_radius)

    def collect(self):
        self.collected = True

    def draw(self, window, xwindow, ywindow):
        if(self.is_secondary):
            color = (127, 127, 0)
        else:
            color = (255, 255, 0)

        pygame.draw.circle(window, color, (xwindow, ywindow), int(self.obj_radius * pygame.display.get_surface().get_width()/2))
