import pygame
import time
from const import TEST_DISPLAY_LENGTH

class Logger:

    def __init__(self):
        self.font = pygame.font.SysFont("Comis Sans MS", 20)
        self.messages = []
        self.max_height = 600

    def add_message(self, message, color=(0,255,255)):
        self.messages.append((message, time.time(), color))

    def draw(self, window):
        now = time.time()

        # Creates new list with message not displayed for too long
        self.messages = [msg for msg in self.messages if not self.is_message_too_old(now, msg)]
        ordinate_to_draw = pygame.display.get_surface().get_height() - 50
        for (msg, _, color) in reversed(self.messages):
            if(ordinate_to_draw < self.max_height):
                break
            surface = self.font.render(msg, True, color, (0,0,0))
            window.blit(surface, (0, ordinate_to_draw))
            ordinate_to_draw -= 10*1.8


    def is_message_too_old(self, now, message):
        if(TEST_DISPLAY_LENGTH == 0):
            return False
        timestamp = message[1]
        return now - timestamp > TEST_DISPLAY_LENGTH
