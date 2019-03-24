import pygame
from const import WIN_CAP

class Score:
    def __init__(self, username):
        self.font = pygame.font.SysFont("Comis Sans MS", 20)
        self.username = username

    def draw(self, players, window):
        sorted_players = sorted(players.values(), key=(lambda p: p.score), reverse=True)
        ordinate_to_draw = 10
        width = pygame.display.get_surface().get_size()[0]
        for p in sorted_players:
            if(p.username == self.username):
                color = (255, 215, 0)
            else:
                color = (255, 0, 255)
            surface = self.font.render(p.username + " : " + str(p.score), True, color, (0,0,0))
            window.blit(surface, (width - 100, ordinate_to_draw))
            ordinate_to_draw += 10*1.8
