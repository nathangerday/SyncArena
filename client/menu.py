import multiplayer_game
from const import REFRESH_TICKRATE
from input_box import InputBox
from logger import Logger
import pygame
import re

class Menu:
    """Ecran dans lequel on a une input_box permettant d'entrer un pseudo puis d'essayer de se connecter à une session.
    """



    def __init__(self, client, message=None):
        self.client = client
        self.inputbox = InputBox(client.window_width / 2 - 100, client.window_height / 2 - 12, self.launch)
        self.inputbox.isWriting = True
        self.logger = Logger()
        if(not message is None):
            self.logger.add_message(message, (255, 0, 0))
        self.logger.add_message("Please enter a username")
        self.main_loop()

    def main_loop(self):
        while(True):
            self.handle_keyboard_input()

            self.draw_frame()

            self.client.clock.tick(REFRESH_TICKRATE)
    
    def handle_keyboard_input(self):
        keys = pygame.key.get_pressed()
        self.inputbox.handle_continuous_pressed_keys(keys)

        for event in pygame.event.get():
            if(event.type == pygame.QUIT):
                self.stop()

            if(event.type == pygame.KEYDOWN):
                if(event.key == pygame.K_ESCAPE):
                    self.stop()
                self.inputbox.handle_input_event(event)
            
    def draw_frame(self):
        self.client.window.fill((0, 0, 0))
        self.inputbox.draw(self.client.window)
        self.logger.draw(self.client.window)
        pygame.display.update()


    def stop(self):
        pygame.quit()
        exit(0)

    def launch(self, text):
        """Fonction pour lancer une partie multijoueur. On vérifie la validité du pseudo avant de lancer la partie
        """
        if(not re.match('^[a-z]+$', text) or len(text) > 10):
            self.logger.add_message("The username must be composed of only lower case letters and be less than 10 characters")
            pass
        else:
            multiplayer_game.MultiplayerGame(self.client, text)
