import math
import pygame

class Goal:

    def __init__(self, x, y):
        self.x = x
        self.y = y
        self.obj_radius = 0.05
        self.collected = False

    def isCollectable(self, otherx, othery):
        distance = math.sqrt(math.pow(otherx - self.x, 2) + math.pow(othery - self.y, 2))
        # print(distance)
        return (not self.collected) and (distance < self.obj_radius)

    def collect(self):
        self.collected = True

    def draw(self, window, xwindow, ywindow, xplayer, yplayer):
        if(self.isCollectable(xplayer, yplayer)):
            color = (255, 0, 0)
        else:
            color = (255, 255, 0)


        pygame.draw.circle(window, color, (xwindow, ywindow), int(self.obj_radius * pygame.display.get_surface().get_width()/2))
