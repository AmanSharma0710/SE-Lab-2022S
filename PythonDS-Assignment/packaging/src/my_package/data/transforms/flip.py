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

       