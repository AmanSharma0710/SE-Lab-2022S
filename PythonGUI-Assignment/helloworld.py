from tkinter import *
from PIL import ImageTk, Image

root = Tk()
root.title("HW")
img = Image.open("./data/imgs/0.jpg")
photo = ImageTk.PhotoImage(img)
helloworld = Label(root, image=photo)
hellolabel2 = Label(root, text = "Hello World 2!!", bd = 10)

helloworld.pack()
hellolabel2.pack()

root.mainloop()