import pygame

class InputBox:

    def __init__(self, x, y):
        self.font = pygame.font.SysFont("monospace", 13)
        self.text = ""
        self.x = x
        self.y = y
        self.isWriting = False

    def handle_input_event(self, event):
        if(event.key == pygame.K_RETURN):
            if(self.isWriting):
                #TODO Send message
                print("HELLO1")
                self.text = ""
                return
            else:
                print("HELLO2")
                self.isWriting = True
                return
        if(self.isWriting):
            if(event.key == pygame.K_BACKSPACE):
                self.text = self.text[:-1]
            elif(event.key == pygame.K_ESCAPE):
                self.isWriting = False
            else:
                self.text += event.unicode


    def draw(self, window):
        pygame.draw.rect(window, (255, 0, 0), (self.x, self.y, 200, 25), 1)
        if(self.isWriting):
            text_to_display = self.text + "|"
        else:
            text_to_display = self.text
        
        surface = self.font.render(text_to_display[-27:], False, (255, 255, 120))
        window.blit(surface, (self.x+5, self.y+4))
    