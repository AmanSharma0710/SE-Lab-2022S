####### REQUIRED IMPORTS FROM THE PREVIOUS ASSIGNMENT #######
from tkinter import filedialog
from my_package.model import InstanceSegmentationModel
from my_package.data import Dataset
from my_package.analysis import plot_visualization
from my_package.data.transforms import FlipImage, RescaleImage, BlurImage, CropImage, RotateImage
from PIL import Image

####### ADD THE ADDITIONAL IMPORTS FOR THIS ASSIGNMENT HERE #######
from tkinter import *
import numpy as np


# Define the function you want to call when the filebrowser button is clicked.
def fileClick(clicked, dataset, segmentor, e, root):
    
    ####### CODE REQUIRED (START) #######
    # This function should pop-up a dialog for the user to select an input image file.
    # Once the image is selected by the user, it should automatically get the corresponding outputs from the segmentor.
    # Hint: Call the segmentor from here, then compute the output images from using the `plot_visualization` function and save it as an image.
    # Once the output is computed it should be shown automatically based on choice the dropdown button is at.
    # To have a better clarity, please check out the sample video.
    global input_image, output_segmentation, output_bb
    directory = "./data/"
    getFile = filedialog.askopenfilename(title="Select Image", defaultextension=".jpg", filetypes=[("Image Files", "*.jpg")], initialdir=directory)
    if(getFile == ""):
        print("No file selected!")
        return
    nameOfImage = getFile.split("/")[-1]
    e.delete(0, END)
    e.insert(0, nameOfImage)
    index = 0
    for i in dataset.annotations:
        if(i["img_fn"].split("/")[-1] == nameOfImage):
            print(i)
            print(directory)
            print("Image shape: ", Image.open(directory + i["img_fn"]).size)
            print("PNG Annotation shape: ", Image.open(directory + i["png_ann_fn"]).size)
            break
        index += 1
    image = dataset[index]["image"]
    pred_boxes, pred_masks, pred_labels, pred_scores = segmentor(image)
    input_image = Image.open(directory + i["img_fn"])
    output_segmentation = plot_visualization(image, pred_boxes, pred_masks, pred_labels, pred_scores, addBoundingBoxes=False)
    output_bb = plot_visualization(image, pred_boxes, pred_masks, pred_labels, pred_scores, addMasks=False)
    process(clicked, e, root)
    ####### CODE REQUIRED (END) #######

# `process` function definition starts from here.
# will process the output when clicked.
def process(clicked, e, root):

    ####### CODE REQUIRED (START) #######
    # Should show the corresponding segmentation or bounding boxes over the input image wrt the choice provided.
    # Note: this function will just show the output, which should have been already computed in the `fileClick` function above.
    # Note: also you should handle the case if the user clicks on the `Process` button without selecting any image file.
    if (e.get() == ""):
        print("No file selected!")
        return
    img1 = Label(root, image=input_image)
    img1.grid(row=1, column=0, ipadx=15, ipady=5)
    if (clicked.get() == "Segmentation"):
        img2 = Label(root, image=output_segmentation)
        img2.grid(row=1, column=1, ipadx=15, ipady=5)
    else:
        img2 = Label(root, image=output_bb)
        img2.grid(row=1, column=1, ipadx=15, ipady=5)
    ####### CODE REQUIRED (END) #######

# `main` function definition starts from here.
if __name__ == '__main__':

    root = Tk()
    root.title("SWE LAB | IIT KGP | Image Viewer")


    # Setting up the segmentor model.
    annotation_file = './data/annotations.jsonl'
    transforms = []

    # Instantiate the segmentor model.
    segmentor = InstanceSegmentationModel()
    # Instantiate the dataset.
    dataset = Dataset(annotation_file, transforms=transforms)


    options = ["Segmentation", "Bounding-box"]
    clicked = StringVar()
    clicked.set(options[0])

    e = Entry(root, width=70)
    e.grid(row=0, column=0)

    # Declare the file browsing button
    filebrowser = Button(root, text="...", command= lambda: fileClick(clicked, dataset, segmentor, e, root))
    filebrowser.grid(row=0, column=1)


    # Declare the drop-down button
    dropdown = OptionMenu(root, clicked, *options)
    dropdown.grid(row=0, column=2)
    #dropdown.get() returns the current value of the dropdown.

    # This is a `Process` button, check out the sample video to know about its functionality
    myButton = Button(root, text="Process", command= lambda: process(clicked, e, root))
    myButton.grid(row=0, column=3)


    root.mainloop()