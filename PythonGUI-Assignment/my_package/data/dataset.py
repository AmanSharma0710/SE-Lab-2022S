import json
import numpy as np
from PIL import Image


class Dataset(object):
    '''
        A class for the dataset that will return data items as per the given index
    '''

    def __init__(self, annotation_file, transforms = None):
        '''
            Arguments:
            annotation_file: path to the annotation file
            transforms: list of transforms (class instances)
                        For instance, [<class 'RandomCrop'>, <class 'Rotate'>]
        '''
        self.annotations_path = annotation_file
        with open(annotation_file) as file:
            list_of_annotations = [json.loads(line) for line in file]
        self.annotations = list_of_annotations
        self.transforms = transforms
        
        

    def __len__(self):
        '''
            return the number of data points in the dataset
        '''
        return len(self.annotations)
        

    def __getitem__(self, idx):
        '''
            return the dataset element for the index: "idx"
            Arguments:
                idx: index of the data element.

            Returns: A dictionary with:
                image: image (in the form of a numpy array) (shape: (3, H, W))
                gt_png_ann: the segmentation annotation image (in the form of a numpy array) (shape: (1, H, W))
                gt_bboxes: N X 5 array where N is the number of bounding boxes, each 
                            consisting of [class, x1, y1, x2, y2]
                            x1 and x2 lie between 0 and width of the image,
                            y1 and y2 lie between 0 and height of the image.

            You need to do the following, 
            1. Extract the correct annotation using the idx provided.
            2. Read the image, png segmentation and convert it into a numpy array (wont be necessary
                with some libraries). The shape of the arrays would be (3, H, W) and (1, H, W), respectively.
            3. Scale the values in the arrays to be with [0, 1].
            4. Perform the desired transformations on the image.
            5. Return the dictionary of the transformed image and annotations as specified.
        '''

        annotation = self.annotations[idx]

        path_to_dir = self.annotations_path.replace('annotations.jsonl', '')
        image_path = path_to_dir + annotation['img_fn']
        image = np.array(Image.open(image_path))

        #Perform the desired transformations on the image.
        if self.transforms:
            for transform in self.transforms:
                image = transform(image)
        
        #Scale the values in the arrays to be with [0, 1].
        image = image.transpose((2, 0, 1))
        image = image / 255.0

        gt_png_ann = np.array(Image.open(path_to_dir + annotation['png_ann_fn']))
        gt_png_ann = gt_png_ann[..., np.newaxis].transpose((2, 0, 1))
        gt_png_ann = gt_png_ann / 255.0
        
        gt_bboxes = []
        for i in range(len(annotation['bboxes'])):
          gt_bboxes.append([annotation['bboxes'][i]['category'], annotation['bboxes'][i]['bbox'][0], annotation['bboxes'][i]['bbox'][1], annotation['bboxes'][i]['bbox'][2] + annotation['bboxes'][i]['bbox'][0], annotation['bboxes'][i]['bbox'][1]+ annotation['bboxes'][i]['bbox'][3]])
        gt_bboxes = np.array(gt_bboxes)

        #Return the dictionary of the transformed image and annotations as specified.
        return {'image': image, 'gt_png_ann': gt_png_ann, 'gt_bboxes': gt_bboxes}
        