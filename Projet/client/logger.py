import pygame
import time
from const import TEST_DISPLAY_LENGTH

class Logger:

    def __init__(self):
        self.font = pygame.font.SysFont("Comis Sans MS", 20)
        self.messages = []

    def add_message(self, message):
        self.messages.append((message, time.time()))

    def draw(self, window):
        now = time.time()

        # Creates new list with message not displayed for too long
        self.messages = [msg for msg in self.messages if not self.is_message_too_old(now, msg)]
        ordinate_to_draw = 0
        for (msg, _) in self.messages:
            surface = self.font.render(msg, True, (0, 255, 255))
            surface.set_alpha(120)
            window.blit(surface, (0, ordinate_to_draw))
            ordinate_to_draw += 10*1.8


    def is_message_too_old(self, now, message):
        timestamp = message[1]
        return now - timestamp > TEST_DISPLAY_LENGTH
