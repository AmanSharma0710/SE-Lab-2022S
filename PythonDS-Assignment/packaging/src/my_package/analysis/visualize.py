import numpy as np
import cv2
import matplotlib.pyplot as plt
import numpy as np
import random


def plot_visualization(image, pred_boxes, pred_masks, pred_class, pred_score, save_location='.', output_name='output.png', save_image=False, show_image=False):
  '''
    Visualize the output of the model.
    Arguments:
      image (numpy array): The image to be displayed of shape (3,H,W).
      pred_boxes (list): A list of bounding boxes containing elements of type [(X1, Y1), (X2, Y2)].
      pred_masks (list): A list of masks of shape (1, H, W).
      pred_class (list): A list of class labels.
      pred_score (list): A list of scores which give confidence in the prediction.
      save_location (str): The location to save the visualization.
      output_name (str): The name of the output image.
      show_image (bool): Whether to show the image.
  '''
  #convert image to (H,W,3) for visualization and convert elements to [0, 255]
  image = image.transpose(1,2,0)
  image = (image*255).astype(np.uint8)
  number_of_boxes = len(pred_boxes)
  if(number_of_boxes<=3):
    indices = range(number_of_boxes)
  else:
    #we choose 3 best bounding boxes
    temp = dict()
    for i in range(number_of_boxes):
      temp[pred_score[i]] = i
    indices = [temp[x] for x in temp]
    while(len(indices)>3):
      indices.pop()
  #we add all the masks and in the end add that on top of the image
  masks = np.zeros((image.shape[0], image.shape[1], 3), dtype=np.uint8)
  for i in indices:
    color = [random.randint(0, 255) for _ in range(3)]
    (x1, y1), (x2, y2) = pred_boxes[i]
    name = pred_class[i]
    confidence = pred_score[i]
    image = cv2.rectangle(image, (x1, y1), (x2, y2), color, 1)

    image = cv2.putText(image, '{}, {:.2f}%'.format(name, confidence*100), ((int)(x1+1), (int)(y1+11)), cv2.FONT_HERSHEY_DUPLEX, 0.4, color, 1, cv2.LINE_AA)
    #to get labels outside the bounding box uncomment the following line and comment the line above
    #image = cv2.putText(image, '{}, {:.2f}%'.format(name, confidence*100), (x,y), cv2.FONT_HERSHEY_DUPLEX, 0.4, color, 1, cv2.LINE_AA)
    
    mask = pred_masks[i][0, :, :]
    mask = np.stack((color[0]*mask, color[1]*mask, color[2]*mask), axis=-1).astype(np.uint8)
    masks = cv2.addWeighted(mask, 0.5, masks, 1, 0)
  image = cv2.addWeighted(masks, 0.95, image, 1, 0)

  if show_image:
    plt.imshow(image)
    plt.show()

  if save_image:
    cv2.imwrite(save_location+'/'+output_name, image)
    
  return image