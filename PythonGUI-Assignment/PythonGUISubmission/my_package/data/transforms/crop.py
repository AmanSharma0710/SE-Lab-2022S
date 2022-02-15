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
            if (image.shape[0] - height)==0:
                y = 0
            else:
                y = np.random.randint(0, image.shape[0] - height)
            if (image.shape[1] - width)==0:
                x = 0
            else:
                x = np.random.randint(0, image.shape[1] - width)
        return image[y:y + height, x:x + width]