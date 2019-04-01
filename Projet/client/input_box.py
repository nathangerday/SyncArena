import pygame
import time
import send_serveur
class InputBox:

    def __init__(self, x, y, logger):
        self.font = pygame.font.SysFont("monospace", 13)
        self.text = ""
        self.x = x
        self.y = y
        self.isWriting = False
        self.logger = logger
        self.socket = None # Need to be set to be able to send messages
        self.last_backspace_done = 0

    def handle_input_event(self, event):
        if(event.key == pygame.K_RETURN):
            if(self.isWriting and self.text != ""):
                self.send_message()
                self.text = ""
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
        if(keys[pygame.K_BACKSPACE] and time.time() - self.last_backspace_done > 1 / 11):
            self.text = self.text[:-1]
            self.last_backspace_done = time.time()

    def draw(self, window):
        pygame.draw.rect(window, (255, 0, 0), (self.x, self.y, 200, 25), 1)
        if(self.isWriting):
            text_to_display = self.text + "|"
        else:
            text_to_display = self.text
        
        surface = self.font.render(text_to_display[-25:], False, (255, 255, 120), (0,0,0))
        window.blit(surface, (self.x+5, self.y+4))
    
    def send_message(self):
        words = self.text.split(" ")
        if(len(words) >= 3 and words[0] == "/w" and len(words[1]) > 0 and len(words[2]) > 0):
            msg = " ".join(words[2:])
            self.logger.add_message("To " + words[1] + " : " + msg, (255,105,180))
            send_serveur.sendpmsg(self.socket, words[1], msg)
        elif(len(words) == 1 and words[0] == "/race"):
            self.logger.add_message("The next session will be a race")
            send_serveur.sendrace(self.socket)
        else:
            self.logger.add_message(self.text, (255,0,0))
            send_serveur.sendmsg(self.socket, self.text)