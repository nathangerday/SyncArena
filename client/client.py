import re
import pygame
from menu import Menu
from const import WINDOW_WIDTH, WINDOW_HEIGHT
class Client:
    """Representation de la fenetre de base avec ses proprietes.
    """



    def __init__(self, window_width, window_height, title="Client window"):
        self.window_width = window_width
        self.window_height = window_height

        pygame.init()
        self.window = pygame.display.set_mode((window_width, window_height))
        pygame.display.set_caption(title)
        self.clock = pygame.time.Clock() # Used to control refresh rate
        Menu(self)


Client(WINDOW_WIDTH, WINDOW_HEIGHT)
