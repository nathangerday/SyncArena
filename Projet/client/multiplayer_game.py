import pygame
from arena import Arena
from player import Player
from const import REFRESH_TICKRATE
from logger import Logger

class MultiplayerGame:

    def __init__(self, client):
        self.client = client
        self.logger = Logger()
        self.arena = Arena(self.client.window_width, self.client.window_height) # Creation de l'arene
        self.main_player = Player("evilFighter.png")

        self.arena.players.append(self.main_player)
        self.main_loop()

    def main_loop(self):
        while(True):
            # now = current_milli_time() # TODO Delete

            self.handle_keyboard_input()
            

            self.enitities_update()

            self.draw_frame()            

            self.client.clock.tick(REFRESH_TICKRATE) # Limite le nombre de mise a jour par seconde
            # print(current_milli_time() - now, " ms per frame") #TODO Delete


    def handle_keyboard_input(self):
        for event in pygame.event.get():
            if(event.type == pygame.QUIT):
                self.stop()

            if(event.type == pygame.KEYDOWN):
                if(event.key == pygame.K_SPACE):
                    self.main_player.thrust()
                if(event.key == pygame.K_ESCAPE):
                    self.stop()

        keys = pygame.key.get_pressed()
        if(keys[pygame.K_q] or keys[pygame.K_LEFT]):
            self.main_player.anticlock()
        elif(keys[pygame.K_d] or keys[pygame.K_RIGHT]):
            self.main_player.clock()


    def enitities_update(self):
        self.arena.update() # Updates every players in the arena including the main_player

    def draw_frame(self):
        #TODO Optimisation : Effacer uniquement ce qui change
        self.client.window.fill((0, 0, 0)) # Efface tout ce qui est sur la fenetre
        self.arena.draw(self.client.window) # Dessine toutes les entites
        self.logger.draw(self.client.window)
        pygame.display.update() # Met a jour la fenetre

    def stop(self):
        pygame.quit()
        exit(0)