import pygame
import time
import send_serveur
class InputBox:
    """Boite dans laquelle on peut écrire du texte et donner une action a executer lorsuque l'on appuie sur Entrée
    """


    def __init__(self, x, y, on_enter):
        self.font = pygame.font.SysFont("monospace", 13)
        self.text = ""
        self.x = x
        self.y = y
        self.isWriting = False
        self.last_backspace_done = 0
        self.on_enter = on_enter # Fonction a executer lorsque l'on appuie sur Entree

    def handle_input_event(self, event):
        if(event.key == pygame.K_RETURN):
            if(self.isWriting and self.text != ""):
                if(not self.on_enter is None):
                    self.on_enter(self.text) # Callback
                self.text = "" # Vide le texte
                return
            else:
                self.isWriting = True
                return
        if(self.isWriting):
            if(event.key == pygame.K_BACKSPACE):
                pass
                # self.text = self.text[:-1]
            elif(event.key == pygame.K_ESCAPE):
                self.isWriting = False
            else:
                self.text += event.unicode

    def handle_continuous_pressed_keys(self, keys):
        """Permet d'effacer le texte en restant appuyer sur Backspace
        """

        if(keys[pygame.K_BACKSPACE] and time.time() - self.last_backspace_done > 1 / 11):
            self.text = self.text[:-1]
            self.last_backspace_done = time.time()

    def draw(self, window):
        pygame.draw.rect(window, (255, 0, 0), (self.x, self.y, 200, 25), 1)
        if(self.isWriting):
            text_to_display = self.text + "|"
        else:
            text_to_display = self.text
        
        surface = self.font.render(text_to_display[-23:], False, (255, 255, 120), (0,0,0))
        window.blit(surface, (self.x+5, self.y+4))