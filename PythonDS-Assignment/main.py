#Imports
from my_package.model import InstanceSegmentationModel
from my_package.data import Dataset
from my_package.analysis import plot_visualization
from my_package.data.transforms import FlipImage, RescaleImage, BlurImage, CropImage, RotateImage
import numpy as np
from PIL import Image
import matplotlib.pyplot as plt

def experiment(annotation_file, segmentor, transforms, outputs):
    '''
        Function to perform the desired experiments

        Arguments:
        annotation_file: Path to annotation file
        segmentor: The image segmentor
        transforms: List of transformation classes
        outputs: path of the output folder to store the images
    '''
    #Create an instance of the dataset
    data = Dataset(annotation_file, transforms)
    #Visualize and save the output on all the images
    for i in range(len(data)):
        data_item = data[i]
        image = data_item['image']
        pred_boxes, pred_masks, pred_labels, pred_scores = segmentor(image)
        plot_visualization(image, pred_boxes, pred_masks, pred_labels, pred_scores, outputs, 'image{}.png'.format(i), save_image = True, show_image = False)
    


def main():
    segmentor = InstanceSegmentationModel()
    experiment('./data/annotations.jsonl', segmentor, [], "./outputs/imgs")

    #The code below is for part 2 of the analysis task and can be commented out if just the above function is needed

    #Import image 3 for roll number 20CS30063
    image_to_analyse = np.array(Image.open('./data/imgs/3.jpg'))
    #make a list of all the transformations
    list_of_transforms = []
    list_of_transforms.append([])
    list_of_transforms.append([FlipImage('horizontal')])
    list_of_transforms.append([BlurImage(2)])
    list_of_transforms.append([RescaleImage((2*image_to_analyse.shape[1], 2*image_to_analyse.shape[0]))])
    list_of_transforms.append([RescaleImage((image_to_analyse.shape[1]//2, image_to_analyse.shape[0]//2))])
    list_of_transforms.append([RotateImage(-90)])
    list_of_transforms.append([RotateImage(45)])
    
    #for all the 7 transformations, we perform them, plot the output and save it
    for i in range(len(list_of_transforms)):
        img = image_to_analyse.copy()
        for transform in list_of_transforms[i]:
            img = transform(img)
        fig = plt.figure()
        fig.set_size_inches(16, 8)
        fig.set_dpi(100)
        ax1 = fig.add_subplot(1, 2, 1)
        ax1.imshow(img)
        ax2 = fig.add_subplot(1, 2, 2)
        img1 = img.copy()
        img1 = img1.transpose(2, 0, 1)
        img1 = img1/255.0
        pred_boxes, pred_masks, pred_labels, pred_scores = segmentor(img1)
        img1 = plot_visualization(img1, pred_boxes, pred_masks, pred_labels, pred_scores)
        ax2.imshow(img1)
        #uncomment the following line to view the plots
        #plt.show()
        fig.savefig('./outputs/transformations/transform{}.png'.format(i+1))


if __name__ == '__main__':
    main()