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
            image1 = image1.resize((new_h, new_w))
            return np.array(image1)
        else:
            image1 = Image.fromarray(image)
            image1 = image1.resize(self.output_size)
            return np.array(image1)