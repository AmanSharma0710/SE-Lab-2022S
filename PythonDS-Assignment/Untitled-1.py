# %%
import numpy as np

# %%
from PIL import Image

# %%
im1 = Image.open(r'/home/aman/Desktop/software-engg-lab-2022S/PythonDS-Assignment/data/imgs/3.jpg')

# %%
im1.show()

# %%
image = np.array(im1)
print(image.shape)

# %%
import numpy as np


class FlipImage(object):
    '''
        Flips the image.
    '''

    def __init__(self, flip_type='horizontal'):
        '''
            Arguments:
            flip_type: 'horizontal' or 'vertical' Default: 'horizontal'
        '''
        if flip_type not in ['horizontal', 'vertical']:
            raise ValueError('flip_type must be either horizontal or vertical')
        self.flip_type = flip_type

        
    def __call__(self, image):
        '''
            Arguments:
            image (numpy array or PIL image)

            Returns:
            image (numpy array or PIL image)
        '''
        if self.flip_type == 'horizontal':
            return np.fliplr(image)
        else:
            return np.flipud(image)

       

# %%
flipped_image = FlipImage('horizontal')(image)
Image.fromarray(flipped_image).show()

# %%
from PIL import Image
import numpy as np


class RotateImage(object):
    '''
        Rotates the image about the centre of the image.
    '''

    def __init__(self, degrees):
        '''
            Arguments:
            degrees: rotation degree.
        '''
        self.degrees = degrees

    def __call__(self, sample):
        '''
            Arguments:
            image (numpy array or PIL image)

            Returns:
            image (numpy array or PIL image)
        '''
        image1 = Image.fromarray(sample)
        image1 = image1.rotate(self.degrees)
        return np.array(image1)

# %%
rotated_image = RotateImage(69)(image)
Image.fromarray(rotated_image).show()

# %%
from PIL import Image
import numpy as np


class RescaleImage(object):
    '''
        Rescales the image to a given size.
    '''

    def __init__(self, output_size):
        '''
            Arguments:
            output_size (tuple or int): Desired output size. If tuple, output is
            matched to output_size. If int, smaller of image edges is matched
            to output_size keeping aspect ratio the same.
        '''
        self.output_size = output_size

    def __call__(self, image):
        '''
            Arguments:
            image (numpy array or PIL image)

            Returns:
            image (numpy array or PIL image)

            Note: You do not need to resize the bounding boxes. ONLY RESIZE THE IMAGE.
        '''
        if(type(self.output_size)==int):
            h, w = image.shape[:2]
            if(h>w):
                new_w = self.output_size
                new_h = int(h*self.output_size/w)
            else:
                new_h = self.output_size
                new_w = int(w*self.output_size/h)
            image1 = Image.fromarray(image)
            image1 = image1.resize((new_w, new_h))
            return np.array(image1)
        else:
            image1 = Image.fromarray(image)
            image1 = image1.resize(self.output_size)
            return np.array(image1)

# %%
resized_image = RescaleImage((100, 500))(image)
Image.fromarray(resized_image).show()

# %%
from PIL import Image, ImageFilter
import numpy as np

class GaussBlurImage(object):
    '''
        Applies Gaussian Blur on the image.
    '''

    def __init__(self, radius):
        '''
            Arguments:
            radius (int): radius to blur
        '''
        self.radius = radius
        

    def __call__(self, image):
        '''
            Arguments:
            image (numpy array or PIL Image)

            Returns:
            image (numpy array or PIL Image)
        '''
        image1 = Image.fromarray(image)
        image1 = image1.filter(ImageFilter.GaussianBlur(self.radius))
        return np.array(image1)

# %%
blurred_image = GaussBlurImage(1)(image)
Image.fromarray(blurred_image).show()

# %%
import numpy as np


class CropImage(object):
    '''
        Performs either random cropping or center cropping.
    '''

    def __init__(self, shape, crop_type='center'):
        '''
            Arguments:
            shape: output shape of the crop (h, w)
            crop_type: center crop or random crop. Default: center
        '''
        self.shape = shape
        if crop_type not in ['center', 'random']:
            raise ValueError('crop_type must be either center or random')
        self.crop_type = crop_type


    def __call__(self, image):
        '''
            Arguments:
            image (numpy array or PIL image)

            Returns:
            image (numpy array or PIL image)
        '''
        height, width = self.shape
        if (self.shape[0] > image.shape[0]) or (self.shape[1] > image.shape[1]):
            raise ValueError('Crop shape must be smaller than image shape')
        if self.crop_type == 'center':
            y = int((image.shape[0] - height) / 2)
            x = int((image.shape[1] - width) / 2)
        else:
            y = np.random.randint(0, image.shape[0] - height)
            x = np.random.randint(0, image.shape[1] - width)
        return image[y:y + height, x:x + width]

# %%
cropped_image = CropImage((180, 200), 'random')(image)
Image.fromarray(cropped_image).show()

# %%
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

        print(image.shape)
        print(gt_png_ann.shape)

        #Return the dictionary of the transformed image and annotations as specified.
        return {'image': image, 'gt_png_ann': gt_png_ann, 'gt_bboxes': annotation['bboxes']}
        

# %%
data = Dataset(r'/home/aman/Desktop/software-engg-lab-2022S/PythonDS-Assignment/data/annotations.jsonl', [RescaleImage((1000, 500)), GaussBlurImage(5), CropImage((200,200), 'random'), FlipImage(), RotateImage(90)])
image = data[0]['image']
print(image.shape)
image = (image*255).astype(np.uint8)
image = image.transpose((1, 2, 0))
Image.fromarray(image).show()

# %%



